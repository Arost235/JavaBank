package com.bank.model;

public class User
{
    private String id;
    private String name;
    private String surname;
    private String email; // Уникальный код (выбрана почта)

    public User(String id, String name, String surname, String email)
    {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
}