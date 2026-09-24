package com.bank;

import com.bank.model.Account;
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

        // Создаем пользователя и счет с новыми параметрами (ставка, срок в месяцах)
        // Ставки по ТЗ: 17, 17.5, 19, 21, 22.3
        User user = new User("1", "Алексей", "Смирнов", "alex@mail.com");
        Account account = new Account("ACC-001", "DEP-101", "DEP-102",
                BigDecimal.ZERO, BigDecimal.ZERO,
                17.5, 6,   // Депозит 1: ставка 17.5%, срок 6 месяцев
                21.0, 12); // Депозит 2: ставка 21.0%, срок 12 месяцев

        InMemoryDatabase.users.add(user);
        InMemoryDatabase.accounts.add(account);

        while (true)
        {
            System.out.println("\n=== БАНКОВСКОЕ МЕНЮ ===");
            System.out.println("1. Посмотреть баланс и детали депозитов");
            System.out.println("2. Пополнить Депозит 1");
            System.out.println("3. Снять с Депозита 1");
            System.out.println("4. Пополнить Депозит 2");
            System.out.println("5. Перевод с Депозита 1 на Депозит 2");
            System.out.println("6. Симуляция прогона месяцев (начисление процентов)");
            System.out.println("7. Досрочное закрытие депозита (проценты сгорят)");
            System.out.println("8. История транзакций");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт: ");

            int choice = scanner.nextInt();
            switch (choice)
            {
                case 1:
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
                    break;
                case 2:
                    System.out.print("Сумма пополнения Депозита 1: ");
                    service.depositToDep1(account, scanner.nextBigDecimal());
                    break;
                case 3:
                    System.out.print("Сумма снятия с Депозита 1: ");
                    service.withdrawFromDep1(account, scanner.nextBigDecimal());
                    break;
                case 4:
                    System.out.print("Сумма пополнения Депозита 2: ");
                    service.depositToDep2(account, scanner.nextBigDecimal());
                    break;
                case 5:
                    System.out.print("Сумма перевода с Депозита 1 на Депозит 2: ");
                    service.transferDep1ToDep2(account, scanner.nextBigDecimal());
                    break;
                case 6:
                    System.out.print("Какой депозит симулировать (1 или 2): ");
                    int depNum = scanner.nextInt();
                    System.out.print("Введите количество месяцев для симуляции (например, 2, 3, 6): ");
                    int months = scanner.nextInt();
                    service.simulateMonths(account, months, depNum);
                    break;
                case 7:
                    System.out.print("Какой депозит закрыть досрочно (1 или 2): ");
                    int earlyDepNum = scanner.nextInt();
                    service.closeEarly(account, earlyDepNum);
                    break;
                case 8:
                    System.out.println("\n--- СПИСОК ТРАНЗАКЦИЙ ---");
                    if (InMemoryDatabase.transactions.isEmpty()) {
                        System.out.println("Транзакций пока не было.");
                    }
                    else
                    {
                        InMemoryDatabase.transactions.forEach(System.out::println);
                    }
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
}