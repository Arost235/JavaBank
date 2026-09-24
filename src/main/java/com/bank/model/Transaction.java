package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction
{
    private String transactionId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, BigDecimal amount, String description)
    {
        this.transactionId = transactionId;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString()
    {
        return "ID: " + transactionId + " | Описание: " + description + " | Сумма: " + amount + " | Время: " + timestamp;
    }
}