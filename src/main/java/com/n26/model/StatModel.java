package com.n26.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatModel {
    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public StatModel() {
        resetStats();
    }

    public StatModel(StatModel stats) {
        this.sum = new BigDecimal(stats.getSum().doubleValue());
        this.avg = new BigDecimal(stats.getAvg().doubleValue());
        this.max = new BigDecimal(stats.getMax().doubleValue());
        this.min = new BigDecimal(stats.getMin().doubleValue());
        this.count = stats.getCount();
    }

    public void resetStats() {
        this.sum = BigDecimal.valueOf(0);
        this.avg = BigDecimal.valueOf(0);
        this.max = BigDecimal.valueOf(0);
        this.min = BigDecimal.valueOf(0);
        this.count = 0L;
    }
}
