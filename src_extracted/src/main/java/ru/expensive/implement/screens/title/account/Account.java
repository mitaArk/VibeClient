package ru.expensive.implement.screens.title.account;

public class Account {
    private final String username;
    private final long createdAt;

    public Account(String username) {
        this.username = username;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
