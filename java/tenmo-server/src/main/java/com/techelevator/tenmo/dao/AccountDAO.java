package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    public List<Account> getAllAccounts();

    //Get account by id

    Balance getBalance(String id);

    public Balance setBalance(Balance balance, int id);

    public int getAccountId (String user);

    public void sendersBalance (int accountId, Transfer transfer);

    public BigDecimal getBalanceForAccount(int accountId);

    public Balance receiversBalance(int accountId, Transfer transfer);

    public Account getAccountByAccountId(int accountId);
    public Account  getAccountByUserId (int userId);
}

