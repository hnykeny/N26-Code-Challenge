package com.n26.service;

import com.n26.dao.TransactionRepository;
import com.n26.dto.StatsDto;
import com.n26.dto.TransactionDto;
import com.n26.exception.FutureTransactionException;
import com.n26.exception.OldTransactionException;
import com.n26.model.StatModel;
import com.n26.model.TransactionModel;
import com.n26.util.TransactionDecayTimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.logging.Logger;

@Service
public class TransactionService {

    private final Logger logger = Logger.getLogger(TransactionModel.class.getName());
    private static DecimalFormat amountFormatter = new DecimalFormat("0.00");

    private static final SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private TransactionRepository transactionRepo;

    @Autowired
    public TransactionService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void saveTransaction(TransactionDto transactionDto) throws ParseException {

        Date transactionDateTime = simpleDateFormat.parse(transactionDto.getTimestamp());
        long currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        long millisecondsAgo = currentTime - transactionDateTime.getTime();
        if (millisecondsAgo < 60000 && millisecondsAgo > 0) {
            TransactionModel transactionModel = new TransactionModel(transactionDto.getAmount(), transactionDto.getTimestamp());

            Timer decayTimer = new Timer();
            decayTimer.schedule(new TransactionDecayTimerTask(transactionModel.getId(), this.transactionRepo), millisecondsAgo);

            transactionModel.setDecayTimer(decayTimer);
            transactionRepo.addTransaction(transactionModel);
        } else if (millisecondsAgo < 0) {
            throw new FutureTransactionException();
        } else {
            throw new OldTransactionException();
        }
    }

    public StatsDto getStats() {
        StatsDto statsDto = new StatsDto();
        StatModel stats = this.transactionRepo.getStats();
        statsDto.setSum(amountFormatter.format(stats.getSum()));
        statsDto.setAvg(amountFormatter.format(stats.getAvg()));
        statsDto.setMax(amountFormatter.format(stats.getMax()));
        statsDto.setMin(amountFormatter.format(stats.getMin()));
        statsDto.setCount(stats.getCount());
        return statsDto;
    }

    public void deleteAllTransactions() {
        transactionRepo.removeAll();
    }
}
