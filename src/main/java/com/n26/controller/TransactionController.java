package com.n26.controller;

import com.n26.dto.StatsDto;
import com.n26.dto.TransactionDto;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
public class TransactionController {

    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transactions", consumes = "application/json")
    public ResponseEntity addTransaction(@RequestBody TransactionDto transactionDto) throws ParseException {
        this.transactionService.saveTransaction(transactionDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/statistics")
    public StatsDto getStats() {
        return this.transactionService.getStats();
    }

    @DeleteMapping("/transactions")
    public ResponseEntity deleteAllTransactions() {
        this.transactionService.deleteAllTransactions();
        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }
}
