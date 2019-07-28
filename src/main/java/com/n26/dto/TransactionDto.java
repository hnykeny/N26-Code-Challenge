package com.n26.dto;

import lombok.Data;

@Data
public class TransactionDto {
    private Double amount;
    private String timestamp;
}
