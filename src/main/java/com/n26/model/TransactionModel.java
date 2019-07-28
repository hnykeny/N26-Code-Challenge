package com.n26.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Timer;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionModel {


    @EqualsAndHashCode.Include
    private UUID id;
    private Double amount;
    private String timestamp;
    private Timer decayTimer;

    public TransactionModel(Double amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.id = UUID.randomUUID();
    }
}
