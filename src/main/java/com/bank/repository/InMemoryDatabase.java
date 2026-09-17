package com.bank.repository;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDatabase
{
    // Списки играют роль таблиц во внутренней памяти
    public static List<User> users = new ArrayList<>();
    public static List<Account> accounts = new ArrayList<>();
    public static List<Transaction> transactions = new ArrayList<>();
}