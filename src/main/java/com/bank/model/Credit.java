package com.bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Credit
{
    public enum Status { ACTIVE, OVERDUE, CLOSED }

    private String creditId;
    private BigDecimal amount;          // сумма кредита
    private BigDecimal rate;            // годовая ставка, %
    private int termMonths;             // срок в месяцах
    private BigDecimal monthlyPayment;  // аннуитетный платёж
    private BigDecimal remainingDebt;   // остаток долга (с процентами)
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    public Credit(String creditId, BigDecimal amount, BigDecimal rate, int termMonths)
    {
        this.creditId = creditId;
        this.amount = amount;
        this.rate = rate;
        this.termMonths = termMonths;
        this.monthlyPayment = calculateMonthlyPayment(amount, rate, termMonths);
        this.remainingDebt = monthlyPayment.multiply(BigDecimal.valueOf(termMonths));
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusMonths(termMonths);
        this.status = Status.ACTIVE;
    }

    /**
     * Аннуитетный платёж:
     *   P = S * r * (1+r)^n / ((1+r)^n - 1)
     * где r — месячная ставка (годовая/100/12), n — число месяцев.
     */
    private static BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal annualRate, int months)
    {
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusR.pow(months);
        BigDecimal numerator = amount.multiply(monthlyRate).multiply(pow);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    public String getCreditId() { return creditId; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getRate() { return rate; }
    public int getTermMonths() { return termMonths; }
    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public BigDecimal getRemainingDebt() { return remainingDebt; }
    public void setRemainingDebt(BigDecimal remainingDebt) { this.remainingDebt = remainingDebt; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}