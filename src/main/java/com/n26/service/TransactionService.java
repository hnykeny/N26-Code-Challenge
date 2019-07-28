package com.n26.service;

import com.n26.dao.TransactionRepository;
import com.n26.dto.StatsDto;
import com.n26.dto.TransactionDto;
import com.n26.exception.OldTransactionException;
import com.n26.model.StatModel;
import com.n26.model.TransactionModel;
import com.n26.util.TransactionDecayTimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Logger;

@Service
public class TransactionService {

    private final Logger logger = Logger.getLogger(TransactionModel.class.getName());

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private TransactionRepository transactionRepo;

    @Autowired
    public TransactionService(TransactionRepository transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public void saveTransaction(TransactionDto transactionDto) throws ParseException, OldTransactionException {

        Date transactionDateTime = simpleDateFormat.parse(transactionDto.getTimestamp());
        long currentTime = Calendar.getInstance().getTimeInMillis();
        logger.info(String.format("Current Time: %d", currentTime));
        long millisecondsAgo = currentTime - transactionDateTime.getTime();
        if (millisecondsAgo < 999) {
            TransactionModel transactionModel = new TransactionModel(transactionDto.getAmount(), transactionDto.getTimestamp());

            Timer decayTimer = new Timer();
            decayTimer.schedule(new TransactionDecayTimerTask(transactionModel.getId(), this.transactionRepo), millisecondsAgo);
            logger.info("Started Decay Timer at: " + Calendar.getInstance().getTimeInMillis());

            transactionModel.setDecayTimer(decayTimer);
            this.transactionRepo.addTransaction(transactionModel);
        } else {
            logger.info("Transaction Discarded : " + transactionDto.toString());
            throw new OldTransactionException();
        }
    }

    public StatsDto getStats() {
        StatsDto statsDto = new StatsDto();
        StatModel stats = this.transactionRepo.getStats();
        statsDto.setSum(stats.getSum().round(new MathContext(2, RoundingMode.HALF_UP)).toPlainString());
        statsDto.setAvg(stats.getAvg().round(new MathContext(2, RoundingMode.HALF_UP)).toPlainString());
        statsDto.setMax(stats.getMax().round(new MathContext(2, RoundingMode.HALF_UP)).toPlainString());
        statsDto.setMin(stats.getMin().round(new MathContext(2, RoundingMode.HALF_UP)).toPlainString());
        statsDto.setCount(stats.getCount());
        return statsDto;
    }

    public void deleteAllTransactions() {
        transactionRepo.removeAll();
    }
}
