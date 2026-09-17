package com.bank.model;

import java.math.BigDecimal;

public class Account
{
    private String accountId;
    private String deposit1Number; // Пополнение и снятие
    private String deposit2Number; // Только пополнение
    private BigDecimal balance1;
    private BigDecimal balance2;

    public Account(String accountId, String deposit1Number, String deposit2Number, BigDecimal balance1, BigDecimal balance2)
    {
        this.accountId = accountId;
        this.deposit1Number = deposit1Number;
        this.deposit2Number = deposit2Number;
        // Если ничего не передано, ставим BigDecimal.ZERO по ТЗ
        this.balance1 = balance1 != null ? balance1 : BigDecimal.ZERO;
        this.balance2 = balance2 != null ? balance2 : BigDecimal.ZERO;
    }

    public String getAccountId() { return accountId; }
    public BigDecimal getBalance1() { return balance1; }
    public void setBalance1(BigDecimal balance1) { this.balance1 = balance1; }
    public BigDecimal getBalance2() { return balance2; }
    public void setBalance2(BigDecimal balance2) { this.balance2 = balance2; }
}