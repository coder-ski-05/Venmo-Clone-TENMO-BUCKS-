package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    // id
    private int accountId;
    // user_id
    private int userId;
    // balance
    private BigDecimal balance;





    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
