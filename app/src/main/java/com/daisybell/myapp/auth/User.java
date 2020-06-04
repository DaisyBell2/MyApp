package com.daisybell.myapp.auth;

public class User {
        public int id;
        public String name, surname, email, password;

    public User() {
    }
    // Конструктор для вызова и заполнения нужными нам данными
    public User(int id, String name, String surname, String email, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }
}
