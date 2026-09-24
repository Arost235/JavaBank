package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.InMemoryDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class BankService
{

    public void depositToDep1(Account account, BigDecimal amount)
    {
        account.setBalance1(account.getBalance1().add(amount));
        saveTransaction(amount, "Пополнение Депозита 1");
        System.out.println("Депозит 1 успешно пополнен на " + amount);
    }

    public void withdrawFromDep1(Account account, BigDecimal amount)
    {
        if (account.getBalance1().compareTo(amount) >= 0)
        {
            account.setBalance1(account.getBalance1().subtract(amount));
            saveTransaction(amount.negate(), "Снятие с Депозита 1");
            System.out.println("Снято с Депозита 1: " + amount);
        } else
        {
            System.out.println("Ошибка: недостаточно средств на Депозите 1.");
        }
    }

    public void depositToDep2(Account account, BigDecimal amount)
    {
        account.setBalance2(account.getBalance2().add(amount));
        saveTransaction(amount, "Пополнение Депозита 2");
        System.out.println("Депозит 2 успешно пополнен на " + amount);
    }

    public void transferDep1ToDep2(Account account, BigDecimal amount)
    {
        if (account.getBalance1().compareTo(amount) >= 0) {
            account.setBalance1(account.getBalance1().subtract(amount));
            account.setBalance2(account.getBalance2().add(amount));
            saveTransaction(amount, "Перевод с Д1 на Д2");
            System.out.println("Перевод с Депозита 1 на Депозит 2 выполнен успешно.");
        }
        else
        {
            System.out.println("Ошибка: недостаточно средств на Депозите 1 для перевода.");
        }
    }

    /**
     * Симуляция прогона депозитов по месяцам.
     * Каждое начисление вознаграждения идет 1 раз в месяц и оформляется как отдельная транзакция с уникальным ID.
     * (Баланс * Ставка / 100) / 12
     */
    public void simulateMonths(Account account, int monthsToSimulate, int depositChoice)
    {
        if (depositChoice == 1)
        {
            BigDecimal totalNewInterest = BigDecimal.ZERO;
            for (int i = 1; i <= monthsToSimulate; i++)
            {
                BigDecimal monthlyRate = BigDecimal.valueOf(account.getRate1()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal monthlyInterest = account.getBalance1().multiply(monthlyRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

                totalNewInterest = totalNewInterest.add(monthlyInterest);
                saveTransaction(monthlyInterest, "Вознаграждение за месяц " + i + " (Депозит 1)");
            }
            // ОБЯЗАТЕЛЬНО обновляем через сеттер!
            account.setAccruedInterest1(account.getAccruedInterest1().add(totalNewInterest));
            System.out.println("Симуляция для Депозита 1 за " + monthsToSimulate + " мес. завершена. Начислено вознаграждения: " + totalNewInterest);
        }
        else if (depositChoice == 2)
        {
            BigDecimal totalNewInterest = BigDecimal.ZERO;
            for (int i = 1; i <= monthsToSimulate; i++)
            {
                BigDecimal monthlyRate = BigDecimal.valueOf(account.getRate2()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal monthlyInterest = account.getBalance2().multiply(monthlyRate).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

                totalNewInterest = totalNewInterest.add(monthlyInterest);
                saveTransaction(monthlyInterest, "Вознаграждение за месяц " + i + " (Депозит 2)");
            }
            // ОБЯЗАТЕЛЬНО обновляем через сеттер!
            account.setAccruedInterest2(account.getAccruedInterest2().add(totalNewInterest));
            System.out.println("Симуляция для Депозита 2 за " + monthsToSimulate + " мес. завершена. Начислено вознаграждения: " + totalNewInterest);
        }
        else
        {
            System.out.println("Неверный выбор депозита для симуляции.");
        }
    }

    /**
     * Досрочное закрытие депозита: все накопленное вознаграждение сгорает.
     */
    public void closeEarly(Account account, int depositChoice)
    {
        if (depositChoice == 1)
        {
            System.out.println("Досрочное закрытие Депозита 1. Накопленное вознаграждение в размере " + account.getAccruedInterest1() + " сгорело.");
            account.setAccruedInterest1(BigDecimal.ZERO);
            account.setBalance1(BigDecimal.ZERO);
            saveTransaction(BigDecimal.ZERO, "Досрочное закрытие Депозита 1 (вознаграждение сгорело)");
        }
        else if (depositChoice == 2)
        {
            System.out.println("Досрочное закрытие Депозита 2. Накопленное вознаграждение в размере " + account.getAccruedInterest2() + " сгорело.");
            account.setAccruedInterest2(BigDecimal.ZERO);
            account.setBalance2(BigDecimal.ZERO);
            saveTransaction(BigDecimal.ZERO, "Досрочное закрытие Депозита 2 (вознаграждение сгорело)");
        }
        else
        {
            System.out.println("Неверный номер депозита.");
        }
    }

    private void saveTransaction(BigDecimal amount, String description)
    {
        String txId = UUID.randomUUID().toString().substring(0, 6);
        Transaction tx = new Transaction(txId, amount, description);
        InMemoryDatabase.transactions.add(tx);
    }
}