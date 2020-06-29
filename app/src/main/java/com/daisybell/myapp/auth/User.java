package com.daisybell.myapp.auth;

public class User {
        public String id, name, surname, email, password;
        public boolean admin;

    public User() {
    }
    // Конструктор для вызова и заполнения нужными нам данными
    public User(String id, String name, String surname, String email, String password, boolean admin) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }
}
