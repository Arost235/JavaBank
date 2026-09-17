package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.InMemoryDatabase;

import java.math.BigDecimal;
import java.util.UUID;

public class BankService
{

    // Депозит 1: пополнение и снятие
    public void depositToDep1(Account account, BigDecimal amount)
    {
        account.setBalance1(account.getBalance1().add(amount));
        saveTransaction(amount);
        System.out.println("Депозит 1 успешно пополнен на " + amount);
    }

    public void withdrawFromDep1(Account account, BigDecimal amount)
    {
        if (account.getBalance1().compareTo(amount) >= 0)
        {
            account.setBalance1(account.getBalance1().subtract(amount));
            saveTransaction(amount.negate());
            System.out.println("Снято с Депозита 1: " + amount);
        } else
        {
            System.out.println("Ошибка: недостаточно средств на Депозите 1.");
        }
    }

    // Депозит 2: только пополнение
    public void depositToDep2(Account account, BigDecimal amount)
    {
        account.setBalance2(account.getBalance2().add(amount));
        saveTransaction(amount);
        System.out.println("Депозит 2 успешно пополнен на " + amount);
    }

    // Перевод с Депозита 1 на Депозит 2
    public void transferDep1ToDep2(Account account, BigDecimal amount)
    {
        if (account.getBalance1().compareTo(amount) >= 0) {
            account.setBalance1(account.getBalance1().subtract(amount));
            account.setBalance2(account.getBalance2().add(amount));
            saveTransaction(amount);
            System.out.println("Перевод с Депозита 1 на Депозит 2 выполнен успешно.");
        }
        else
        {
            System.out.println("Ошибка: недостаточно средств на Депозите 1 для перевода.");
        }
    }

    private void saveTransaction(BigDecimal amount)
    {
        String txId = UUID.randomUUID().toString().substring(0, 6);
        Transaction tx = new Transaction(txId, amount);
        InMemoryDatabase.transactions.add(tx);
    }
}