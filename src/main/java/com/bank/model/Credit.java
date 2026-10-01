package com.bank.model;

import java.math.BigDecimal;

public class Credit {
    private BigDecimal totalAmount;      // Сумма кредита (макс. 1 000 000)
    private BigDecimal monthlyPayment;   // Ежемесячный платеж (например, 50 000 тенге)
    private int termMonths;              // Срок в месяцах (2 года = 24 месяца)
    private boolean isBlocked;           // Флаг блокировки счета

    public Credit(BigDecimal totalAmount, BigDecimal monthlyPayment, int termMonths) {
        this.totalAmount = totalAmount;
        this.monthlyPayment = monthlyPayment;
        this.termMonths = termMonths;
        this.isBlocked = false;
    }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public int getTermMonths() { return termMonths; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
}