package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.repository.InMemoryDatabase;
import com.bank.model.Credit;

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

    /**
     * Выдача кредита. Максимум 1 000 000, ставка фиксированная — 30%.
     * Одновременно может быть только один активный кредит.
     */
    public void issueCredit(Account account, BigDecimal amount, int months)
    {
        if (account.getCredit() != null && account.getCredit().getStatus() != Credit.Status.CLOSED)
        {
            System.out.println("Ошибка: у вас уже есть активный кредит.");
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            System.out.println("Ошибка: сумма должна быть положительной.");
            return;
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0)
        {
            System.out.println("Ошибка: максимальная сумма кредита — 1 000 000.");
            return;
        }
        if (months <= 0)
        {
            System.out.println("Ошибка: срок должен быть положительным.");
            return;
        }

        BigDecimal rate = new BigDecimal("30.0");
        Credit credit = new Credit(java.util.UUID.randomUUID().toString().substring(0, 6),
                amount, rate, months);
        account.setCredit(credit);

        // Деньги зачисляются на кредитный счёт
        account.setCreditAccountBalance(account.getCreditAccountBalance().add(amount));

        saveTransaction(amount, "Выдача кредита " + credit.getCreditId());

        System.out.println("\n=== КРЕДИТ ОДОБРЕН ===");
        System.out.println("ID кредита:          " + credit.getCreditId());
        System.out.println("Сумма:               " + amount);
        System.out.println("Ставка:              " + rate + "% годовых");
        System.out.println("Срок:                " + months + " мес.");
        System.out.println("Ежемесячный платёж:  " + credit.getMonthlyPayment());
        System.out.println("Всего к возврату:    " + credit.getRemainingDebt());
        System.out.println("Деньги зачислены на кредитный счёт.");
    }

    /**
     * Симуляция погашения кредита по месяцам.
     * Порядок списания: Кредитный счёт -> Депозит 1 -> Депозит 2.
     * Если на каком-то месяце денег не хватило — блокировка счёта
     * и списание ВСЕХ средств в счёт долга.
     */
    public void simulateCreditRepayment(Account account)
    {
        Credit credit = account.getCredit();
        if (credit == null)
        {
            System.out.println("Кредит не оформлен.");
            return;
        }
        if (credit.getStatus() == Credit.Status.CLOSED)
        {
            System.out.println("Кредит уже закрыт.");
            return;
        }

        System.out.println("\n=== СИМУЛЯЦИЯ ПОГАШЕНИЯ КРЕДИТА ===");
        System.out.println("Остаток долга:    " + credit.getRemainingDebt());
        System.out.println("Платёж в месяц:   " + credit.getMonthlyPayment());
        System.out.println("Кредитный счёт:   " + account.getCreditAccountBalance());
        System.out.println("Депозит 1:        " + account.getBalance1());
        System.out.println("Депозит 2:        " + account.getBalance2());
        System.out.println("-------------------------------------");

        int monthsPaid = 0;
        boolean failed = false;

        for (int month = 1; month <= credit.getTermMonths(); month++)
        {
            if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal payment = credit.getMonthlyPayment().min(credit.getRemainingDebt());
            BigDecimal paid = tryWithdraw(account, payment, "Погашение кредита, месяц " + month);

            if (paid.compareTo(payment) < 0)
            {
                credit.setRemainingDebt(credit.getRemainingDebt().subtract(paid));
                System.out.println("Месяц " + month + ": оплачено только " + paid
                        + " из " + payment);
                failed = true;
                break;
            }

            credit.setRemainingDebt(credit.getRemainingDebt().subtract(payment));
            monthsPaid++;
            System.out.println("Месяц " + month + ": оплачено " + payment
                    + ", остаток долга: " + credit.getRemainingDebt());
        }

        if (failed)
        {
            System.out.println("\n!!! НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ПОГАШЕНИЯ КРЕДИТА !!!");
            System.out.println("Производится блокировка счёта и списание всех средств...");

            // Остаток на всех счетах списываем в долг
            BigDecimal remaining = account.getCreditAccountBalance()
                    .add(account.getBalance1())
                    .add(account.getBalance2());

            System.out.println("Списано со всех счетов: " + remaining);

            credit.setRemainingDebt(credit.getRemainingDebt().subtract(remaining));
            if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) < 0)
                credit.setRemainingDebt(BigDecimal.ZERO);

            // Обнуляем всё, попутно пишем транзакции по источникам
            if (account.getCreditAccountBalance().compareTo(BigDecimal.ZERO) > 0)
            {
                saveTransaction(account.getCreditAccountBalance().negate(),
                        "Списание в счёт долга (кредитный счёт)");
                account.setCreditAccountBalance(BigDecimal.ZERO);
            }
            if (account.getBalance1().compareTo(BigDecimal.ZERO) > 0)
            {
                saveTransaction(account.getBalance1().negate(),
                        "Списание в счёт долга (Депозит 1)");
                account.setBalance1(BigDecimal.ZERO);
            }
            if (account.getBalance2().compareTo(BigDecimal.ZERO) > 0)
            {
                saveTransaction(account.getBalance2().negate(),
                        "Списание в счёт долга (Депозит 2)");
                account.setBalance2(BigDecimal.ZERO);
            }

            if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) > 0)
            {
                account.setBlocked(true);
                credit.setStatus(Credit.Status.OVERDUE);
                saveTransaction(BigDecimal.ZERO,
                        "СЧЁТ ЗАБЛОКИРОВАН. Остаток долга: " + credit.getRemainingDebt());
                System.out.println("Счёт ЗАБЛОКИРОВАН.");
                System.out.println("Остаток долга: " + credit.getRemainingDebt());
            }
            else
            {
                credit.setStatus(Credit.Status.CLOSED);
                saveTransaction(BigDecimal.ZERO,
                        "Кредит " + credit.getCreditId() + " закрыт (погашен списанием)");
                System.out.println("Кредит полностью погашен за счёт списания.");
            }
        }
        else if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) <= 0)
        {
            credit.setStatus(Credit.Status.CLOSED);
            account.setBlocked(false);
            saveTransaction(BigDecimal.ZERO,
                    "Кредит " + credit.getCreditId() + " полностью погашен");
            System.out.println("\n✅ Кредит полностью погашен. Счёт разблокирован.");
        }
        else
        {
            System.out.println("\nОплачено " + monthsPaid + " мес. Симуляция прервана.");
        }
    }

    /**
     * Вспомогательный метод: пытается списать сумму по порядку
     * Кредитный счёт -> Депозит 1 -> Депозит 2.
     * Возвращает фактически списанную сумму.
     */
    private BigDecimal tryWithdraw(Account account, BigDecimal amount, String reason)
    {
        BigDecimal remaining = amount;

        // 1. Кредитный счёт
        BigDecimal fromCredit = account.getCreditAccountBalance().min(remaining);
        if (fromCredit.compareTo(BigDecimal.ZERO) > 0)
        {
            account.setCreditAccountBalance(account.getCreditAccountBalance().subtract(fromCredit));
            saveTransaction(fromCredit.negate(), reason + " (кредитный счёт)");
            remaining = remaining.subtract(fromCredit);
        }

        // 2. Депозит 1
        if (remaining.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal fromDep1 = account.getBalance1().min(remaining);
            if (fromDep1.compareTo(BigDecimal.ZERO) > 0)
            {
                account.setBalance1(account.getBalance1().subtract(fromDep1));
                saveTransaction(fromDep1.negate(), reason + " (Депозит 1)");
                remaining = remaining.subtract(fromDep1);
            }
        }

        // 3. Депозит 2
        if (remaining.compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal fromDep2 = account.getBalance2().min(remaining);
            if (fromDep2.compareTo(BigDecimal.ZERO) > 0)
            {
                account.setBalance2(account.getBalance2().subtract(fromDep2));
                saveTransaction(fromDep2.negate(), reason + " (Депозит 2)");
                remaining = remaining.subtract(fromDep2);
            }
        }

        return amount.subtract(remaining);
    }

    /**
     * Досрочное (ручное) погашение кредита. Работает даже если счёт заблокирован.
     * Списание идёт из переданной суммы — она "приходит извне"
     * (клиент вносит деньги). Если долг закрылся полностью — счёт разблокируется.
     */
    public void payCredit(Account account, BigDecimal amount)
    {
        Credit credit = account.getCredit();
        if (credit == null)
        {
            System.out.println("Кредит не оформлен.");
            return;
        }
        if (credit.getStatus() == Credit.Status.CLOSED)
        {
            System.out.println("Кредит уже закрыт.");
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            System.out.println("Сумма должна быть положительной.");
            return;
        }

        BigDecimal toPay = amount.min(credit.getRemainingDebt());
        BigDecimal excess = amount.subtract(toPay);

        credit.setRemainingDebt(credit.getRemainingDebt().subtract(toPay));
        saveTransaction(toPay.negate(), "Погашение кредита " + credit.getCreditId());

        System.out.println("Внесено в счёт кредита: " + toPay);
        System.out.println("Остаток долга:         " + credit.getRemainingDebt());

        if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) <= 0)
        {
            credit.setStatus(Credit.Status.CLOSED);
            boolean wasBlocked = account.isBlocked();
            account.setBlocked(false);
            System.out.println("Кредит полностью погашен!");
            saveTransaction(BigDecimal.ZERO,
                    "Кредит " + credit.getCreditId() + " полностью погашен (внесение)");
            if (wasBlocked) System.out.println("Счёт разблокирован.");
        }

        if (excess.compareTo(BigDecimal.ZERO) > 0)
        {
            account.setBalance1(account.getBalance1().add(excess));
            System.out.println("Излишек " + excess + " зачислен на Депозит 1.");
        }
    }
}