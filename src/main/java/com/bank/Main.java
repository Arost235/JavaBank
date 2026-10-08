package com.bank;

import com.bank.model.Account;
import com.bank.model.Credit;
import com.bank.model.User;
import com.bank.repository.InMemoryDatabase;
import com.bank.service.BankService;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        BankService service = new BankService();

        User user = new User("1", "Алексей", "Смирнов", "alex@mail.com");
        Account account = new Account("ACC-001", "DEP-101", "DEP-102",
                BigDecimal.ZERO, BigDecimal.ZERO,
                17.5, 6,
                21.0, 12);

        InMemoryDatabase.users.add(user);
        InMemoryDatabase.accounts.add(account);

        while (true)
        {
            if (account.isBlocked())
            {
                System.out.println("\n⚠️  СЧЁТ ЗАБЛОКИРОВАН ИЗ-ЗА ПРОСРОЧКИ ПО КРЕДИТУ ⚠️");
                System.out.println("Доступны только операции по кредиту (пункты 1, 8, 10, 11, 12).");
            }

            System.out.println("\n=== БАНКОВСКОЕ МЕНЮ ===");
            System.out.println("1.  Посмотреть баланс и детали депозитов");
            System.out.println("2.  Пополнить Депозит 1");
            System.out.println("3.  Снять с Депозита 1");
            System.out.println("4.  Пополнить Депозит 2");
            System.out.println("5.  Перевод с Депозита 1 на Депозит 2");
            System.out.println("6.  Симуляция прогона месяцев (начисление процентов)");
            System.out.println("7.  Досрочное закрытие депозита (проценты сгорят)");
            System.out.println("8.  История транзакций");
            System.out.println("9.  Взять кредит");
            System.out.println("10. Информация о кредите");
            System.out.println("11. Симуляция погашения кредита");
            System.out.println("12. Досрочное (ручное) погашение кредита");
            System.out.println("0.  Выход");
            System.out.print("Выберите пункт: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // чистим буфер

            // ==== ПРОВЕРКА БЛОКИРОВКИ ====
            if (account.isBlocked())
            {
                switch (choice)
                {
                    case 1: showAccountInfo(account); break;
                    case 8: showTransactions(); break;
                    case 10: showCreditInfo(account); break;
                    case 11:
                        service.simulateCreditRepayment(account);
                        break;
                    case 12:
                        System.out.print("Сумма для внесения в счёт кредита: ");
                        service.payCredit(account, scanner.nextBigDecimal());
                        scanner.nextLine();
                        break;
                    case 0:
                        System.out.println("Выход из программы...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Счёт заблокирован. Эта операция недоступна.");
                }
                continue;
            }

            // ==== ОБЫЧНОЕ МЕНЮ ====
            switch (choice)
            {
                case 1: showAccountInfo(account); break;
                case 2:
                    System.out.print("Сумма пополнения Депозита 1: ");
                    service.depositToDep1(account, scanner.nextBigDecimal());
                    scanner.nextLine();
                    break;
                case 3:
                    System.out.print("Сумма снятия с Депозита 1: ");
                    service.withdrawFromDep1(account, scanner.nextBigDecimal());
                    scanner.nextLine();
                    break;
                case 4:
                    System.out.print("Сумма пополнения Депозита 2: ");
                    service.depositToDep2(account, scanner.nextBigDecimal());
                    scanner.nextLine();
                    break;
                case 5:
                    System.out.print("Сумма перевода с Депозита 1 на Депозит 2: ");
                    service.transferDep1ToDep2(account, scanner.nextBigDecimal());
                    scanner.nextLine();
                    break;
                case 6:
                    System.out.print("Какой депозит симулировать (1 или 2): ");
                    int depNum = scanner.nextInt();
                    System.out.print("Введите количество месяцев: ");
                    int months = scanner.nextInt();
                    scanner.nextLine();
                    service.simulateMonths(account, months, depNum);
                    break;
                case 7:
                    System.out.print("Какой депозит закрыть досрочно (1 или 2): ");
                    int earlyDepNum = scanner.nextInt();
                    scanner.nextLine();
                    service.closeEarly(account, earlyDepNum);
                    break;
                case 8: showTransactions(); break;
                case 9:
                    System.out.print("Сумма кредита (до 1 000 000): ");
                    BigDecimal amount = scanner.nextBigDecimal();
                    System.out.print("Срок в месяцах: ");
                    int term = scanner.nextInt();
                    scanner.nextLine();
                    service.issueCredit(account, amount, term);
                    break;
                case 10:
                    showCreditInfo(account);
                    break;
                case 11:
                    service.simulateCreditRepayment(account);
                    break;
                case 12:
                    System.out.print("Сумма для внесения в счёт кредита: ");
                    service.payCredit(account, scanner.nextBigDecimal());
                    scanner.nextLine();
                    break;
                case 0:
                    System.out.println("Выход из программы...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный пункт меню.");
            }
        }
    }

    // ====== вспомогательные методы для меню ======

    private static void showAccountInfo(Account account)
    {
        System.out.println("\n--- ИНФОРМАЦИЯ О СЧЕТЕ ---");
        System.out.println("Депозит 1: Баланс = " + account.getBalance1() +
                ", Ставка = " + account.getRate1() + "%" +
                ", Срок = " + account.getTerm1Months() + " мес." +
                ", Открыт: " + account.getStartDate1() + " -> Закрытие: " + account.getEndDate1() +
                ", Накопленные проценты = " + account.getAccruedInterest1());
        System.out.println("Депозит 2: Баланс = " + account.getBalance2() +
                ", Ставка = " + account.getRate2() + "%" +
                ", Срок = " + account.getTerm2Months() + " мес." +
                ", Открыт: " + account.getStartDate2() + " -> Закрытие: " + account.getEndDate2() +
                ", Накопленные проценты = " + account.getAccruedInterest2());
        System.out.println("Кредитный счёт: " + account.getCreditAccountBalance());
        System.out.println("Статус счёта:   " + (account.isBlocked() ? "ЗАБЛОКИРОВАН" : "активен"));
    }

    private static void showCreditInfo(Account account)
    {
        Credit credit = account.getCredit();
        if (credit == null)
        {
            System.out.println("Кредит не оформлен.");
            return;
        }
        System.out.println("\n--- ИНФОРМАЦИЯ О КРЕДИТЕ ---");
        System.out.println("ID:                " + credit.getCreditId());
        System.out.println("Сумма кредита:     " + credit.getAmount());
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
}