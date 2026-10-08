package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Credit;
import com.bank.model.Currency;
import com.bank.model.CurrentAccount;
import com.bank.model.Transaction;
import com.bank.repository.InMemoryDatabase;
import com.bank.util.ExchangeRates;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class BankService
{
    // ============================================================
    //  ДЕПОЗИТЫ
    // ============================================================

    public void depositToDep1(Account account, BigDecimal amount)
    {
        account.setBalance1(account.getBalance1().add(amount));
        saveTransaction(amount, "Пополнение Депозита 1 (" + account.getCurrency1() + ")");
        System.out.println("Депозит 1 успешно пополнен на " + amount + " " + account.getCurrency1());
    }

    public void withdrawFromDep1(Account account, BigDecimal amount)
    {
        if (account.getBalance1().compareTo(amount) >= 0)
        {
            account.setBalance1(account.getBalance1().subtract(amount));
            saveTransaction(amount.negate(), "Снятие с Депозита 1 (" + account.getCurrency1() + ")");
            System.out.println("Снято с Депозита 1: " + amount + " " + account.getCurrency1());
        }
        else
        {
            System.out.println("Ошибка: недостаточно средств на Депозите 1.");
        }
    }

    public void depositToDep2(Account account, BigDecimal amount)
    {
        account.setBalance2(account.getBalance2().add(amount));
        saveTransaction(amount, "Пополнение Депозита 2 (" + account.getCurrency2() + ")");
        System.out.println("Депозит 2 успешно пополнен на " + amount + " " + account.getCurrency2());
    }

    /** Перевод между депозитами с конвертацией валют. fromDep/toDep: 1 или 2. */
    public void transferBetweenDeposits(Account account, int fromDep, int toDep, BigDecimal amount)
    {
        if (fromDep == toDep)
        {
            System.out.println("Ошибка: выбран один и тот же депозит.");
            return;
        }

        Currency fromCur = (fromDep == 1) ? account.getCurrency1() : account.getCurrency2();
        Currency toCur   = (toDep == 1)   ? account.getCurrency1() : account.getCurrency2();
        BigDecimal fromBalance = (fromDep == 1) ? account.getBalance1() : account.getBalance2();

        if (fromBalance.compareTo(amount) < 0)
        {
            System.out.println("Ошибка: недостаточно средств на Депозите " + fromDep + ".");
            return;
        }

        BigDecimal converted = ExchangeRates.convert(amount, fromCur, toCur);

        if (fromDep == 1) account.setBalance1(account.getBalance1().subtract(amount));
        else              account.setBalance2(account.getBalance2().subtract(amount));

        if (toDep == 1) account.setBalance1(account.getBalance1().add(converted));
        else            account.setBalance2(account.getBalance2().add(converted));

        saveTransaction(amount.negate(),
                "Перевод Д" + fromDep + " -> Д" + toDep + ": " + amount + " " + fromCur);
        saveTransaction(converted,
                "Зачисление на Д" + toDep + " (конвертация): " + converted + " " + toCur);

        System.out.println("Перевод выполнен:");
        System.out.println("  Списано:  " + amount + " " + fromCur + " с Депозита " + fromDep);
        System.out.println("  Зачислено: " + converted + " " + toCur + " на Депозит " + toDep);
        System.out.println("  Курс: 1 " + fromCur + " = " + ExchangeRates.getRate(fromCur, toCur) + " " + toCur);
    }

    /**
     * Симуляция прогона депозитов по месяцам. Начисление — раз в месяц, в валюте депозита.
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
            account.setAccruedInterest1(account.getAccruedInterest1().add(totalNewInterest));
            System.out.println("Симуляция для Депозита 1 за " + monthsToSimulate + " мес. завершена. Начислено: " + totalNewInterest + " " + account.getCurrency1());
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
            account.setAccruedInterest2(account.getAccruedInterest2().add(totalNewInterest));
            System.out.println("Симуляция для Депозита 2 за " + monthsToSimulate + " мес. завершена. Начислено: " + totalNewInterest + " " + account.getCurrency2());
        }
        else
        {
            System.out.println("Неверный выбор депозита для симуляции.");
        }
    }

    public void closeEarly(Account account, int depositChoice)
    {
        if (depositChoice == 1)
        {
            System.out.println("Досрочное закрытие Депозита 1. Вознаграждение " + account.getAccruedInterest1() + " " + account.getCurrency1() + " сгорело.");
            account.setAccruedInterest1(BigDecimal.ZERO);
            account.setBalance1(BigDecimal.ZERO);
            saveTransaction(BigDecimal.ZERO, "Досрочное закрытие Депозита 1 (вознаграждение сгорело)");
        }
        else if (depositChoice == 2)
        {
            System.out.println("Досрочное закрытие Депозита 2. Вознаграждение " + account.getAccruedInterest2() + " " + account.getCurrency2() + " сгорело.");
            account.setAccruedInterest2(BigDecimal.ZERO);
            account.setBalance2(BigDecimal.ZERO);
            saveTransaction(BigDecimal.ZERO, "Досрочное закрытие Депозита 2 (вознаграждение сгорело)");
        }
        else
        {
            System.out.println("Неверный номер депозита.");
        }
    }

    // ============================================================
    //  ТЕКУЩИЕ СЧЕТА
    // ============================================================

    /** Открыть текущий счёт в указанной валюте. */
    public void openCurrentAccount(Account account, Currency currency)
    {
        if (account.findCurrentAccount(currency) != null)
        {
            System.out.println("Счёт в " + currency + " уже открыт.");
            return;
        }
        String number = "CUR-" + currency + "-" + UUID.randomUUID().toString().substring(0, 4);
        CurrentAccount ca = new CurrentAccount(number, currency);
        account.getCurrentAccounts().add(ca);
        saveTransaction(BigDecimal.ZERO, "Открытие текущего счёта " + currency + " (" + number + ")");
        System.out.println("Текущий счёт " + currency + " открыт: " + number);
    }

    public void depositToCurrent(Account account, Currency currency, BigDecimal amount)
    {
        CurrentAccount ca = account.findCurrentAccount(currency);
        if (ca == null) { System.out.println("Счёт в " + currency + " не открыт."); return; }
        ca.setBalance(ca.getBalance().add(amount));
        saveTransaction(amount, "Пополнение текущего счёта " + currency);
        System.out.println("Пополнено на " + amount + " " + currency);
    }

    public void withdrawFromCurrent(Account account, Currency currency, BigDecimal amount)
    {
        CurrentAccount ca = account.findCurrentAccount(currency);
        if (ca == null) { System.out.println("Счёт в " + currency + " не открыт."); return; }
        if (ca.getBalance().compareTo(amount) < 0) { System.out.println("Недостаточно средств."); return; }
        ca.setBalance(ca.getBalance().subtract(amount));
        saveTransaction(amount.negate(), "Снятие с текущего счёта " + currency);
        System.out.println("Снято с текущего счёта: " + amount + " " + currency);
    }

    /** Перевод между текущими счетами с конвертацией. */
    public void transferBetweenCurrent(Account account, Currency fromCur, Currency toCur, BigDecimal amount)
    {
        if (fromCur == toCur) { System.out.println("Выбрана одна и та же валюта."); return; }
        CurrentAccount from = account.findCurrentAccount(fromCur);
        CurrentAccount to   = account.findCurrentAccount(toCur);
        if (from == null || to == null) { System.out.println("Один из счетов не открыт."); return; }
        if (from.getBalance().compareTo(amount) < 0) { System.out.println("Недостаточно средств на счёте " + fromCur); return; }

        BigDecimal converted = ExchangeRates.convert(amount, fromCur, toCur);

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(converted));

        saveTransaction(amount.negate(), "Перевод " + fromCur + " -> " + toCur + ": " + amount + " " + fromCur);
        saveTransaction(converted,      "Зачисление на " + toCur + " (конвертация)");

        System.out.println("Перевод выполнен:");
        System.out.println("  Списано:   " + amount + " " + fromCur);
        System.out.println("  Зачислено: " + converted + " " + toCur);
        System.out.println("  Курс: 1 " + fromCur + " = " + ExchangeRates.getRate(fromCur, toCur) + " " + toCur);
    }

    /** Депозит -> Текущий счёт. dep: 1 или 2. */
    public void transferDepToCurrent(Account account, int dep, Currency toCur, BigDecimal amount)
    {
        Currency fromCur = (dep == 1) ? account.getCurrency1() : account.getCurrency2();
        BigDecimal fromBalance = (dep == 1) ? account.getBalance1() : account.getBalance2();
        CurrentAccount to = account.findCurrentAccount(toCur);
        if (to == null) { System.out.println("Счёт " + toCur + " не открыт."); return; }
        if (fromBalance.compareTo(amount) < 0) { System.out.println("Недостаточно средств на Депозите " + dep); return; }

        BigDecimal converted = ExchangeRates.convert(amount, fromCur, toCur);

        if (dep == 1) account.setBalance1(account.getBalance1().subtract(amount));
        else          account.setBalance2(account.getBalance2().subtract(amount));
        to.setBalance(to.getBalance().add(converted));

        saveTransaction(amount.negate(), "Перевод Депозит " + dep + " -> Текущий " + toCur);
        saveTransaction(converted,      "Зачисление на текущий " + toCur + " (конвертация)");

        System.out.println("Перевод выполнен: " + amount + " " + fromCur + " -> " + converted + " " + toCur);
    }

    /** Текущий счёт -> Депозит. dep: 1 или 2. */
    public void transferCurrentToDep(Account account, Currency fromCur, int dep, BigDecimal amount)
    {
        Currency toCur = (dep == 1) ? account.getCurrency1() : account.getCurrency2();
        CurrentAccount from = account.findCurrentAccount(fromCur);
        if (from == null) { System.out.println("Счёт " + fromCur + " не открыт."); return; }
        if (from.getBalance().compareTo(amount) < 0) { System.out.println("Недостаточно средств на счёте " + fromCur); return; }

        BigDecimal converted = ExchangeRates.convert(amount, fromCur, toCur);

        from.setBalance(from.getBalance().subtract(amount));
        if (dep == 1) account.setBalance1(account.getBalance1().add(converted));
        else          account.setBalance2(account.getBalance2().add(converted));

        saveTransaction(amount.negate(), "Перевод Текущий " + fromCur + " -> Депозит " + dep);
        saveTransaction(converted,      "Зачисление на Депозит " + dep + " (конвертация)");

        System.out.println("Перевод выполнен: " + amount + " " + fromCur + " -> " + converted + " " + toCur);
    }

    // ============================================================
    //  КРЕДИТЫ (валюты кредита — только KZT, как договорились)
    // ============================================================

    public void issueCredit(Account account, BigDecimal amount, int months)
    {
        if (account.getCredit() != null && account.getCredit().getStatus() != Credit.Status.CLOSED)
        {
            System.out.println("Ошибка: у вас уже есть активный кредит.");
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { System.out.println("Ошибка: сумма должна быть положительной."); return; }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) { System.out.println("Ошибка: максимум 1 000 000."); return; }
        if (months <= 0) { System.out.println("Ошибка: срок должен быть положительным."); return; }

        BigDecimal rate = new BigDecimal("30.0");
        Credit credit = new Credit(UUID.randomUUID().toString().substring(0, 6), amount, rate, months);
        account.setCredit(credit);
        account.setCreditAccountBalance(account.getCreditAccountBalance().add(amount));

        saveTransaction(amount, "Выдача кредита " + credit.getCreditId());

        System.out.println("\n=== КРЕДИТ ОДОБРЕН ===");
        System.out.println("ID:               " + credit.getCreditId());
        System.out.println("Сумма:            " + amount + " KZT");
        System.out.println("Ставка:           " + rate + "% годовых");
        System.out.println("Срок:             " + months + " мес.");
        System.out.println("Ежемес. платёж:   " + credit.getMonthlyPayment());
        System.out.println("Всего к возврату: " + credit.getRemainingDebt());
    }

    public void simulateCreditRepayment(Account account)
    {
        Credit credit = account.getCredit();
        if (credit == null) { System.out.println("Кредит не оформлен."); return; }
        if (credit.getStatus() == Credit.Status.CLOSED) { System.out.println("Кредит уже закрыт."); return; }

        System.out.println("\n=== СИМУЛЯЦИЯ ПОГАШЕНИЯ КРЕДИТА ===");
        System.out.println("Остаток долга:    " + credit.getRemainingDebt() + " KZT");
        System.out.println("Платёж в месяц:   " + credit.getMonthlyPayment() + " KZT");
        System.out.println("Кредитный счёт:   " + account.getCreditAccountBalance() + " KZT");
        System.out.println("Депозит 1:        " + account.getBalance1() + " " + account.getCurrency1());
        System.out.println("Депозит 2:        " + account.getBalance2() + " " + account.getCurrency2());
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
                System.out.println("Месяц " + month + ": оплачено только " + paid + " из " + payment);
                failed = true;
                break;
            }

            credit.setRemainingDebt(credit.getRemainingDebt().subtract(payment));
            monthsPaid++;
            System.out.println("Месяц " + month + ": оплачено " + payment + ", остаток: " + credit.getRemainingDebt());
        }

        if (failed)
        {
            System.out.println("\n!!! НЕДОСТАТОЧНО СРЕДСТВ ДЛЯ ПОГАШЕНИЯ КРЕДИТА !!!");
            System.out.println("Производится блокировка счёта и списание всех средств...");

            BigDecimal creditInKzt = account.getCreditAccountBalance();
            BigDecimal dep1InKzt = ExchangeRates.convert(account.getBalance1(), account.getCurrency1(), Currency.KZT);
            BigDecimal dep2InKzt = ExchangeRates.convert(account.getBalance2(), account.getCurrency2(), Currency.KZT);
            BigDecimal totalInKzt = creditInKzt.add(dep1InKzt).add(dep2InKzt);

            System.out.println("Списано со всех счетов (в KZT): " + totalInKzt);

            credit.setRemainingDebt(credit.getRemainingDebt().subtract(totalInKzt));
            if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) < 0)
                credit.setRemainingDebt(BigDecimal.ZERO);

            if (account.getCreditAccountBalance().compareTo(BigDecimal.ZERO) > 0)
            {
                saveTransaction(account.getCreditAccountBalance().negate(), "Списание в счёт долга (кредитный счёт)");
                account.setCreditAccountBalance(BigDecimal.ZERO);
            }
            if (account.getBalance1().compareTo(BigDecimal.ZERO) > 0)
            {
                saveTransaction(account.getBalance1().negate(), "Списание в счёт долга (Депозит 1, " + account.getCurrency1() + ")");
                account.setBalance1(BigDecimal.ZERO);
            }
            if (account.getBalance2().compareTo(BigDecimal.ZERO) > 0)
            {
                saveTransaction(account.getBalance2().negate(), "Списание в счёт долга (Депозит 2, " + account.getCurrency2() + ")");
                account.setBalance2(BigDecimal.ZERO);
            }

            if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) > 0)
            {
                account.setBlocked(true);
                credit.setStatus(Credit.Status.OVERDUE);
                saveTransaction(BigDecimal.ZERO, "СЧЁТ ЗАБЛОКИРОВАН. Остаток долга: " + credit.getRemainingDebt());
                System.out.println("Счёт ЗАБЛОКИРОВАН. Остаток долга: " + credit.getRemainingDebt());
            }
            else
            {
                credit.setStatus(Credit.Status.CLOSED);
                saveTransaction(BigDecimal.ZERO, "Кредит " + credit.getCreditId() + " закрыт (погашен списанием)");
                System.out.println("Кредит полностью погашен за счёт списания.");
            }
        }
        else if (credit.getRemainingDebt().compareTo(BigDecimal.ZERO) <= 0)
        {
            credit.setStatus(Credit.Status.CLOSED);
            account.setBlocked(false);
            saveTransaction(BigDecimal.ZERO, "Кредит " + credit.getCreditId() + " полностью погашен");
            System.out.println("\n✅ Кредит полностью погашен. Счёт разблокирован.");
        }
        else
        {
            System.out.println("\nОплачено " + monthsPaid + " мес. Симуляция прервана.");
        }
    }

    /**
     * Списывает amountKzt (в тенге) по порядку: кредитный счёт -> Депозит 1 -> Депозит 2.
     * Если валюта депозита не KZT — конвертирует.
     * Возвращает фактически списанную сумму (в KZT).
     */
    private BigDecimal tryWithdraw(Account account, BigDecimal amountKzt, String reason)
    {
        BigDecimal remaining = amountKzt;

        // 1. Кредитный счёт (KZT)
        BigDecimal fromCredit = account.getCreditAccountBalance().min(remaining);
        if (fromCredit.compareTo(BigDecimal.ZERO) > 0)
        {
            account.setCreditAccountBalance(account.getCreditAccountBalance().subtract(fromCredit));
            saveTransaction(fromCredit.negate(), reason + " (кредитный счёт)");
            remaining = remaining.subtract(fromCredit);
        }

        // 2. Депозит 1 (в своей валюте)
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && account.getBalance1().compareTo(BigDecimal.ZERO) > 0)
        {
            Currency c1 = account.getCurrency1();
            BigDecimal needed = ExchangeRates.convert(remaining, Currency.KZT, c1);
            BigDecimal actual = account.getBalance1().min(needed);
            account.setBalance1(account.getBalance1().subtract(actual));
            BigDecimal actualKzt = ExchangeRates.convert(actual, c1, Currency.KZT);
            saveTransaction(actual.negate(), reason + " (Депозит 1, " + c1 + ")");
            remaining = remaining.subtract(actualKzt);
        }

        // 3. Депозит 2 (в своей валюте)
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && account.getBalance2().compareTo(BigDecimal.ZERO) > 0)
        {
            Currency c2 = account.getCurrency2();
            BigDecimal needed = ExchangeRates.convert(remaining, Currency.KZT, c2);
            BigDecimal actual = account.getBalance2().min(needed);
            account.setBalance2(account.getBalance2().subtract(actual));
            BigDecimal actualKzt = ExchangeRates.convert(actual, c2, Currency.KZT);
            saveTransaction(actual.negate(), reason + " (Депозит 2, " + c2 + ")");
            remaining = remaining.subtract(actualKzt);
        }

        return amountKzt.subtract(remaining);
    }

    public void payCredit(Account account, BigDecimal amount)
    {
        Credit credit = account.getCredit();
        if (credit == null) { System.out.println("Кредит не оформлен."); return; }
        if (credit.getStatus() == Credit.Status.CLOSED) { System.out.println("Кредит уже закрыт."); return; }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { System.out.println("Сумма должна быть положительной."); return; }

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
            saveTransaction(BigDecimal.ZERO, "Кредит " + credit.getCreditId() + " полностью погашен (внесение)");
            if (wasBlocked) System.out.println("Счёт разблокирован.");
        }

        if (excess.compareTo(BigDecimal.ZERO) > 0)
        {
            CurrentAccount kztAccount = account.findCurrentAccount(Currency.KZT);
            if (kztAccount != null)
            {
                kztAccount.setBalance(kztAccount.getBalance().add(excess));
                System.out.println("Излишек " + excess + " зачислен на текущий счёт KZT.");
            }
            else
            {
                account.setBalance1(account.getBalance1().add(excess));
                System.out.println("Излишек " + excess + " зачислен на Депозит 1.");
            }
        }
    }

    // ============================================================
    //  ВНУТРЕННЕЕ
    // ============================================================

    private void saveTransaction(BigDecimal amount, String description)
    {
        String txId = UUID.randomUUID().toString().substring(0, 6);
        InMemoryDatabase.transactions.add(new Transaction(txId, amount, description));
    }
}