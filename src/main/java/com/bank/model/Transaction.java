package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction
{
    private String transactionId;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, BigDecimal amount)
    {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = LocalDateTime.now(); // Время транзакции
    }

    @Override
    public String toString()
    {
        return "ID транзакции: " + transactionId + " | Сумма: " + amount + " | Время: " + timestamp;
    }
}