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

        // Создаем тестового пользователя и счет (с дефолтным балансом ZERO)
        User user = new User("1", "Алексей", "Смирнов", "alex@mail.com");
        Account account = new Account("ACC-001", "DEP-101", "DEP-102", BigDecimal.ZERO, BigDecimal.ZERO);

        InMemoryDatabase.users.add(user);
        InMemoryDatabase.accounts.add(account);

        while (true)
        {
            System.out.println("\n=== БАНКОВСКОЕ МЕНЮ ===");
            System.out.println("1. Посмотреть баланс");
            System.out.println("2. Пополнить Депозит 1 (пополнение/снятие)");
            System.out.println("3. Снять с Депозита 1");
            System.out.println("4. Пополнить Депозит 2 (только пополнение)");
            System.out.println("5. Перевод с Депозита 1 на Депозит 2");
            System.out.println("6. История транзакций");
            System.out.println("0. Выход");
            System.out.print("Выберите пункт: ");

            int choice = scanner.nextInt();
            switch (choice)
            {
                case 1:
                    System.out.println("Баланс Депозита 1: " + account.getBalance1());
                    System.out.println("Баланс Депозита 2: " + account.getBalance2());
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