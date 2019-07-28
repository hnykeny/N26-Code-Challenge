package com.n26.dao;

import com.n26.model.StatModel;
import com.n26.model.TransactionModel;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionRepository {
    private static final ConcurrentHashMap<UUID, TransactionModel> datastore = new ConcurrentHashMap<>();
    private static final StatModel stats = new StatModel();
    private static final MathContext mathContext = new MathContext(10);

    public synchronized void addTransaction(TransactionModel transactionModel) {
        datastore.put(transactionModel.getId(), transactionModel);

        stats.setSum(stats.getSum().add(BigDecimal.valueOf(transactionModel.getAmount()), mathContext));
        stats.setCount(stats.getCount() + 1L);
        stats.setAvg(stats.getSum().divide(BigDecimal.valueOf(stats.getCount()), mathContext));
        updateMinMaxStats();
    }

    public synchronized void removeTransaction(UUID id) {
        TransactionModel transactionModel = datastore.remove(id);
        if (Objects.nonNull(transactionModel)) {
            stats.setSum(stats.getSum().subtract(BigDecimal.valueOf(transactionModel.getAmount()), mathContext));
            stats.setCount(stats.getCount() - 1L);
            stats.setAvg(stats.getSum().divide(BigDecimal.valueOf(stats.getCount()), mathContext));
            updateMinMaxStats();
        }
    }

    public synchronized void removeAll() {
        datastore.clear();
        stats.resetStats();
    }

    public synchronized StatModel getStats() {
        return new StatModel(stats);
    }

    private void updateMinMaxStats() {
        stats.setMax(new BigDecimal(Collections.max(datastore.values(), Comparator.comparing(TransactionModel::getAmount)).getAmount()));
        stats.setMin(new BigDecimal(Collections.min(datastore.values(),
                Comparator.comparing(TransactionModel::getAmount)).getAmount()));
    }
}
