package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Account
{
    private String accountId;

    // Депозит 1 (пополнение и снятие)
    private String deposit1Number;
    private BigDecimal balance1;
    private double rate1;
    private int term1Months;
    private LocalDate startDate1;
    private LocalDate endDate1;
    private BigDecimal accruedInterest1;

    // Депозит 2 (только пополнение)
    private String deposit2Number;
    private BigDecimal balance2;
    private double rate2;
    private int term2Months;
    private LocalDate startDate2;
    private LocalDate endDate2;
    private BigDecimal accruedInterest2;

    // Кредит
    private Credit credit;                          // текущий кредит (может быть null)
    private BigDecimal creditAccountBalance;        // отдельный кредитный счёт
    private boolean blocked;                        // блокировка счёта

    public Account(String accountId, String deposit1Number, String deposit2Number,
                   BigDecimal balance1, BigDecimal balance2,
                   double rate1, int term1Months, double rate2, int term2Months)
    {
        this.accountId = accountId;
        this.deposit1Number = deposit1Number;
        this.deposit2Number = deposit2Number;
        this.balance1 = balance1 != null ? balance1 : BigDecimal.ZERO;
        this.balance2 = balance2 != null ? balance2 : BigDecimal.ZERO;

        this.rate1 = rate1;
        this.term1Months = term1Months;
        this.startDate1 = LocalDate.now();
        this.endDate1 = this.startDate1.plusMonths(term1Months);
        this.accruedInterest1 = BigDecimal.ZERO;

        this.rate2 = rate2;
        this.term2Months = term2Months;
        this.startDate2 = LocalDate.now();
        this.endDate2 = this.startDate2.plusMonths(term2Months);
        this.accruedInterest2 = BigDecimal.ZERO;

        this.credit = null;
        this.creditAccountBalance = BigDecimal.ZERO;
        this.blocked = false;
    }

    // Геттеры и сеттеры
    public String getAccountId() { return accountId; }

    public BigDecimal getBalance1() { return balance1; }
    public void setBalance1(BigDecimal balance1) { this.balance1 = balance1; }

    public BigDecimal getBalance2() { return balance2; }
    public void setBalance2(BigDecimal balance2) { this.balance2 = balance2; }

    public double getRate1() { return rate1; }
    public int getTerm1Months() { return term1Months; }
    public LocalDate getStartDate1() { return startDate1; }
    public LocalDate getEndDate1() { return endDate1; }
    public BigDecimal getAccruedInterest1() { return accruedInterest1; }
    public void setAccruedInterest1(BigDecimal accruedInterest1) { this.accruedInterest1 = accruedInterest1; }

    public double getRate2() { return rate2; }
    public int getTerm2Months() { return term2Months; }
    public LocalDate getStartDate2() { return startDate2; }
    public LocalDate getEndDate2() { return endDate2; }
    public BigDecimal getAccruedInterest2() { return accruedInterest2; }
    public void setAccruedInterest2(BigDecimal accruedInterest2) { this.accruedInterest2 = accruedInterest2; }

    public Credit getCredit() { return credit; }
    public void setCredit(Credit credit) { this.credit = credit; }

    public BigDecimal getCreditAccountBalance() { return creditAccountBalance; }
    public void setCreditAccountBalance(BigDecimal creditAccountBalance) { this.creditAccountBalance = creditAccountBalance; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}