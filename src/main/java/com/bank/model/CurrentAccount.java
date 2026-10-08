package com.bank.model;

import java.math.BigDecimal;

public class CurrentAccount
{
    private String accountNumber;
    private Currency currency;
    private BigDecimal balance;

    public CurrentAccount(String accountNumber, Currency currency)
    {
        this.accountNumber = accountNumber;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
    }

    public String getAccountNumber() { return accountNumber; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    @Override
    public String toString()
    {
        return "[" + currency + "] " + accountNumber + " — баланс: " + balance;
    }
}