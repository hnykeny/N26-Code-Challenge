package com.n26.util;

import com.n26.dao.TransactionRepository;

import java.util.TimerTask;
import java.util.UUID;

public class TransactionDecayTimerTask extends TimerTask {

    private UUID transactionId;
    private TransactionRepository transactionRepo;

    public TransactionDecayTimerTask(UUID transactionId, TransactionRepository transactionRepo) {
        this.transactionId = transactionId;
        this.transactionRepo = transactionRepo;
    }

    @Override
    public void run() {
        transactionRepo.removeTransaction(transactionId);
    }
}
