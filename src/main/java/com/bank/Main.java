package com.bank;

import com.bank.model.Account;
import com.bank.model.Credit;
import com.bank.model.Currency;
import com.bank.model.CurrentAccount;
import com.bank.model.User;
import com.bank.repository.InMemoryDatabase;
import com.bank.service.BankService;
import com.bank.util.ExchangeRates;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main
{
    private static final Scanner scanner = new Scanner(System.in);
    private static final BankService service = new BankService();
    private static Account account;

    public static void main(String[] args)
    {
        User user = new User("1", "Алексей", "Смирнов", "alex@mail.com");
        account = new Account("ACC-001", "DEP-101", "DEP-102",
                BigDecimal.ZERO, BigDecimal.ZERO,
                17.5, 6, Currency.KZT,     // Депозит 1 — тенге
                21.0, 12, Currency.USD);   // Депозит 2 — доллары

        InMemoryDatabase.users.add(user);
        InMemoryDatabase.accounts.add(account);

        // Три текущих счёта (KZT, USD, EUR)
        account.getCurrentAccounts().add(new CurrentAccount("CUR-KZT-0001", Currency.KZT));
        account.getCurrentAccounts().add(new CurrentAccount("CUR-USD-0002", Currency.USD));
        account.getCurrentAccounts().add(new CurrentAccount("CUR-EUR-0003", Currency.EUR));

        while (true)
        {
            if (account.isBlocked())
            {
                System.out.println("\n⚠️  СЧЁТ ЗАБЛОКИРОВАН ИЗ-ЗА ПРОСРОЧКИ ПО КРЕДИТУ ⚠️");
                System.out.println("Доступны только: Кредиты, Информация, История.");
            }

            System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
            System.out.println("1. Депозиты");
            System.out.println("2. Кредиты");
            System.out.println("3. Текущие счета");
            System.out.println("4. Информация о счёте");
            System.out.println("5. История транзакций");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт: ");

            int choice = readInt();

            switch (choice)
            {
                case 1:
                    if (account.isBlocked()) { System.out.println("Счёт заблокирован."); break; }
                    menuDeposits();
                    break;
                case 2:
                    menuCredits();
                    break;
                case 3:
                    if (account.isBlocked()) { System.out.println("Счёт заблокирован."); break; }
                    menuCurrent();
                    break;
                case 4: showAccountInfo(); break;
                case 5: showTransactions(); break;
                case 0:
                    System.out.println("Выход из программы...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный пункт меню.");
            }
        }
    }

    // ============================================================
    //  ПОДМЕНЮ: ДЕПОЗИТЫ
    // ============================================================
    private static void menuDeposits()
    {
        while (true)
        {
            System.out.println("\n=== ДЕПОЗИТЫ ===");
            System.out.println("1. Пополнить Депозит 1 (" + account.getCurrency1() + ")");
            System.out.println("2. Снять с Депозита 1");
            System.out.println("3. Пополнить Депозит 2 (" + account.getCurrency2() + ")");
            System.out.println("4. Перевод Депозит 1 <-> Депозит 2 (с конвертацией)");
            System.out.println("5. Симуляция начисления процентов");
            System.out.println("6. Досрочное закрытие депозита");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            int c = readInt();
            switch (c)
            {
                case 1:
                    System.out.print("Сумма пополнения Депозита 1: ");
                    service.depositToDep1(account, readBigDecimal());
                    break;
                case 2:
                    System.out.print("Сумма снятия: ");
                    service.withdrawFromDep1(account, readBigDecimal());
                    break;
                case 3:
                    System.out.print("Сумма пополнения Депозита 2: ");
                    service.depositToDep2(account, readBigDecimal());
                    break;
                case 4:
                    System.out.print("Откуда (1 или 2): ");
                    int fromDep = readInt();
                    System.out.print("Куда (1 или 2): ");
                    int toDep = readInt();
                    System.out.print("Сумма: ");
                    BigDecimal amount = readBigDecimal();
                    service.transferBetweenDeposits(account, fromDep, toDep, amount);
                    break;
                case 5:
                    System.out.print("Какой депозит (1 или 2): ");
                    int depNum = readInt();
                    System.out.print("Количество месяцев: ");
                    int months = readInt();
                    service.simulateMonths(account, months, depNum);
                    break;
                case 6:
                    System.out.print("Какой депозит закрыть (1 или 2): ");
                    service.closeEarly(account, readInt());
                    break;
                case 0: return;
                default: System.out.println("Неверный пункт.");
            }
        }
    }

    // ============================================================
    //  ПОДМЕНЮ: КРЕДИТЫ
    // ============================================================
    private static void menuCredits()
    {
        while (true)
        {
            System.out.println("\n=== КРЕДИТЫ ===");
            System.out.println("1. Взять кредит");
            System.out.println("2. Информация о кредите");
            System.out.println("3. Симуляция погашения");
            System.out.println("4. Досрочное (ручное) погашение");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            int c = readInt();
            switch (c)
            {
                case 1:
                    System.out.print("Сумма кредита (до 1 000 000 KZT): ");
                    BigDecimal amount = readBigDecimal();
                    System.out.print("Срок в месяцах: ");
                    int term = readInt();
                    service.issueCredit(account, amount, term);
                    break;
                case 2: showCreditInfo(); break;
                case 3: service.simulateCreditRepayment(account); break;
                case 4:
                    System.out.print("Сумма для внесения: ");
                    service.payCredit(account, readBigDecimal());
                    break;
                case 0: return;
                default: System.out.println("Неверный пункт.");
            }
        }
    }

    // ============================================================
    //  ПОДМЕНЮ: ТЕКУЩИЕ СЧЕТА
    // ============================================================
    private static void menuCurrent()
    {
        while (true)
        {
            System.out.println("\n=== ТЕКУЩИЕ СЧЕТА ===");
            System.out.println("1. Показать все счета");
            System.out.println("2. Пополнить счёт");
            System.out.println("3. Снять со счёта");
            System.out.println("4. Перевод между текущими счетами (с конвертацией)");
            System.out.println("5. Перевод Депозит -> Текущий");
            System.out.println("6. Перевод Текущий -> Депозит");
            System.out.println("7. Курсы валют");
            System.out.println("0. Назад");
            System.out.print("Выберите пункт: ");

            int c = readInt();
            switch (c)
            {
                case 1: showCurrentAccounts(); break;
                case 2:
                    System.out.print("Валюта (KZT/USD/EUR): ");
                    Currency cur = readCurrency();
                    if (cur == null) break;
                    System.out.print("Сумма: ");
                    service.depositToCurrent(account, cur, readBigDecimal());
                    break;
                case 3:
                    System.out.print("Валюта (KZT/USD/EUR): ");
                    cur = readCurrency();
                    if (cur == null) break;
                    System.out.print("Сумма: ");
                    service.withdrawFromCurrent(account, cur, readBigDecimal());
                    break;
                case 4:
                    System.out.print("Из валюты (KZT/USD/EUR): ");
                    Currency fromCur = readCurrency();
                    System.out.print("В валюту (KZT/USD/EUR): ");
                    Currency toCur = readCurrency();
                    if (fromCur == null || toCur == null) break;
                    System.out.print("Сумма: ");
                    service.transferBetweenCurrent(account, fromCur, toCur, readBigDecimal());
                    break;
                case 5:
                    System.out.print("С какого депозита (1 или 2): ");
                    int depFrom = readInt();
                    System.out.print("На какую валюту (KZT/USD/EUR): ");
                    Currency toCurrency = readCurrency();
                    if (toCurrency == null) break;
                    System.out.print("Сумма: ");
                    service.transferDepToCurrent(account, depFrom, toCurrency, readBigDecimal());
                    break;
                case 6:
                    System.out.print("С какой валюты (KZT/USD/EUR): ");
                    Currency curFrom = readCurrency();
                    if (curFrom == null) break;
                    System.out.print("На какой депозит (1 или 2): ");
                    int depTo = readInt();
                    System.out.print("Сумма: ");
                    service.transferCurrentToDep(account, curFrom, depTo, readBigDecimal());
                    break;
                case 7: showRates(); break;
                case 0: return;
                default: System.out.println("Неверный пункт.");
            }
        }
    }

    // ============================================================
    //  ИНФОРМАЦИОННЫЕ МЕТОДЫ
    // ============================================================

    private static void showAccountInfo()
    {
        System.out.println("\n--- ИНФОРМАЦИЯ О СЧЁТЕ ---");
        System.out.println("Депозит 1 [" + account.getCurrency1() + "]: Баланс = " + account.getBalance1() +
                ", Ставка = " + account.getRate1() + "%" +
                ", Срок = " + account.getTerm1Months() + " мес." +
                ", Открыт: " + account.getStartDate1() + " -> " + account.getEndDate1() +
                ", Накоплено = " + account.getAccruedInterest1());
        System.out.println("Депозит 2 [" + account.getCurrency2() + "]: Баланс = " + account.getBalance2() +
                ", Ставка = " + account.getRate2() + "%" +
                ", Срок = " + account.getTerm2Months() + " мес." +
                ", Открыт: " + account.getStartDate2() + " -> " + account.getEndDate2() +
                ", Накоплено = " + account.getAccruedInterest2());
        System.out.println("Кредитный счёт: " + account.getCreditAccountBalance() + " KZT");
        System.out.println("Текущие счета:");
        if (account.getCurrentAccounts().isEmpty())
            System.out.println("  (нет открытых)");
        else
            for (CurrentAccount ca : account.getCurrentAccounts())
                System.out.println("  " + ca);
        System.out.println("Статус счёта:   " + (account.isBlocked() ? "ЗАБЛОКИРОВАН" : "активен"));
    }

    private static void showCurrentAccounts()
    {
        System.out.println("\n--- ТЕКУЩИЕ СЧЕТА ---");
        if (account.getCurrentAccounts().isEmpty())
        {
            System.out.println("Счета не открыты.");
            return;
        }
        for (CurrentAccount ca : account.getCurrentAccounts())
            System.out.println("  " + ca);
    }

    private static void showCreditInfo()
    {
        Credit credit = account.getCredit();
        if (credit == null) { System.out.println("Кредит не оформлен."); return; }
        System.out.println("\n--- ИНФОРМАЦИЯ О КРЕДИТЕ ---");
        System.out.println("ID:                " + credit.getCreditId());
        System.out.println("Сумма кредита:     " + credit.getAmount() + " KZT");
        System.out.println("Ставка:            " + credit.getRate() + "%");
        System.out.println("Срок:              " + credit.getTermMonths() + " мес.");
        System.out.println("Ежемес. платёж:    " + credit.getMonthlyPayment());
        System.out.println("Остаток долга:     " + credit.getRemainingDebt());
        System.out.println("Дата выдачи:       " + credit.getStartDate());
        System.out.println("Плановая дата:     " + credit.getEndDate());
        System.out.println("Статус:            " + credit.getStatus());
    }

    private static void showTransactions()
    {
        System.out.println("\n--- СПИСОК ТРАНЗАКЦИЙ ---");
        if (InMemoryDatabase.transactions.isEmpty())
            System.out.println("Транзакций пока не было.");
        else
            InMemoryDatabase.transactions.forEach(System.out::println);
    }

    private static void showRates()
    {
        System.out.println("\n--- ФИКСИРОВАННЫЕ КУРСЫ ---");
        System.out.println("1 USD = 500.0000 KZT");
        System.out.println("1 EUR = 550.0000 KZT");
        System.out.println("1 EUR = " + ExchangeRates.getRate(Currency.EUR, Currency.USD) + " USD");
    }

    // ============================================================
    //  ВВОД (с защитой от некорректных данных)
    // ============================================================

    private static int readInt()
    {
        while (true)
        {
            try
            {
                int v = scanner.nextInt();
                scanner.nextLine();
                return v;
            }
            catch (Exception e)
            {
                System.out.print("Некорректный ввод. Повторите: ");
                scanner.nextLine();
            }
        }
    }

    private static BigDecimal readBigDecimal()
    {
        while (true)
        {
            try
            {
                BigDecimal v = scanner.nextBigDecimal();
                scanner.nextLine();
                return v;
            }
            catch (Exception e)
            {
                System.out.print("Некорректный ввод. Повторите: ");
                scanner.nextLine();
            }
        }
    }

    private static Currency readCurrency()
    {
        System.out.print("(KZT/USD/EUR): ");
        String s = scanner.nextLine().trim().toUpperCase();
        try { return Currency.valueOf(s); }
        catch (Exception e) { System.out.println("Неизвестная валюта."); return null; }
    }
}