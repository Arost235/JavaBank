package com.bank.util;

import com.bank.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Фиксированные курсы валют.
 *  1 USD = 500 KZT
 *  1 EUR = 550 KZT
 *  (кросс-курс 1 EUR = 1.10 USD — согласован)
 * Конвертация всегда идёт через тенге.
 */
public class ExchangeRates
{
    private static final BigDecimal USD_TO_KZT = new BigDecimal("500");
    private static final BigDecimal EUR_TO_KZT = new BigDecimal("550");

    public static BigDecimal convert(BigDecimal amount, Currency from, Currency to)
    {
        if (from == to) return amount;
        BigDecimal inKzt = toKzt(amount, from);
        return fromKzt(inKzt, to);
    }

    private static BigDecimal toKzt(BigDecimal amount, Currency from)
    {
        switch (from)
        {
            case KZT: return amount;
            case USD: return amount.multiply(USD_TO_KZT);
            case EUR: return amount.multiply(EUR_TO_KZT);
        }
        return amount;
    }

    private static BigDecimal fromKzt(BigDecimal amountKzt, Currency to)
    {
        switch (to)
        {
            case KZT: return amountKzt.setScale(2, RoundingMode.HALF_UP);
            case USD: return amountKzt.divide(USD_TO_KZT, 2, RoundingMode.HALF_UP);
            case EUR: return amountKzt.divide(EUR_TO_KZT, 2, RoundingMode.HALF_UP);
        }
        return amountKzt;
    }

    /** 1 единица from = X to (для отображения в меню) */
    public static BigDecimal getRate(Currency from, Currency to)
    {
        if (from == to) return BigDecimal.ONE;
        BigDecimal inKzt = toKzt(BigDecimal.ONE, from);
        switch (to)
        {
            case KZT: return inKzt.setScale(4, RoundingMode.HALF_UP);
            case USD: return inKzt.divide(USD_TO_KZT, 4, RoundingMode.HALF_UP);
            case EUR: return inKzt.divide(EUR_TO_KZT, 4, RoundingMode.HALF_UP);
        }
        return BigDecimal.ONE;
    }
}