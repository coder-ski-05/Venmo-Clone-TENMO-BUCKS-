package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.InsufficientResourcesException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class AccountJdbcDAO implements AccountDAO {
    private JdbcTemplate jdbcTemplate;
    private User currentUser;
    private Transfer transfer;
    private Balance balance;

    public AccountJdbcDAO(DataSource ds){
        this.jdbcTemplate = new JdbcTemplate(ds);
    }



    @Override
    public Balance getBalance (String user) {
        Balance balance = null;
        String sql = "SELECT balance FROM accounts join users on accounts.user_id = users.user_id where username = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user);
        if(rowSet.next()){
            BigDecimal balances = new BigDecimal(rowSet.getString("balance"));
            balance= new Balance();
            balance.setBalance(balances); //set the bigDecimal to it
        }
        return balance;
    }




    @Override
    public int getAccountId (String user) {
        String sql = "SELECT account_id FROM accounts JOIN users ON accounts.user.id = users.user_id " +
                "WHERE username ILIKE ?";
        Account account = new Account();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user);
        while(rowSet.next()) {
            account.setAccountId(rowSet.getInt("accounts"));
        }
        return account.getAccountId();
    }

    @Override
    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while(rowSet.next()){
            accounts.add(mapRowToAccount(rowSet));
        }
        return accounts;
    }

    @Override
    public Balance setBalance(Balance balance, int id) {
        Balance updatedBalanceAfterQuery =  null;
        String getUsernameQuery = "SELECT username FROM users WHERE user_id = ?";
        String UpdateQuery = "UPDATE accounts SET balance = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(UpdateQuery, balance.getBalance(), id);
        SqlRowSet results = jdbcTemplate.queryForRowSet(getUsernameQuery,id);
        if(results.next()){
            String username = results.getString("username");
            updatedBalanceAfterQuery = getBalance(username);
        }
       return  updatedBalanceAfterQuery;
    }

    @Override
    public BigDecimal getBalanceForAccount(int accountId) {
        BigDecimal amount = new BigDecimal(0);
        String sql = "SELECT balance FROM accounts" +
                " WHERE account_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if(rowSet.next()) {
            amount = rowSet.getBigDecimal("balance");
        }
        System.out.println(amount);
        return amount;
    }

    @Override
    public void sendersBalance (int accountId, Transfer transfer) {

        System.out.println("Calling getBalanceForAccount with " + accountId);
        BigDecimal senderBalance = getBalanceForAccount(accountId);
        BigDecimal transferAmount = transfer.getAmount();
        BigDecimal newBalance = senderBalance.subtract(transferAmount);
        String sql = "UPDATE accounts SET balance = ? " +
                " WHERE account_id = ?";
        jdbcTemplate.update(sql, newBalance, accountId);
    }

    @Override
    public Balance receiversBalance(int accountId, Transfer transfer) {

        BigDecimal receiverBalance = getBalanceForAccount(accountId);
        BigDecimal transferAmount = transfer.getAmount();
        BigDecimal newBalance = receiverBalance.add(transferAmount);
        String sql = "UPDATE accounts SET balance = ? " +
                " WHERE account_id = ?";
        jdbcTemplate.update(sql, newBalance, accountId);
        Account retrievedAccount = getAccountByAccountId(accountId);
        Balance balance = new Balance();
        balance.setBalance(retrievedAccount.getBalance());

        return balance;

    }

    @Override
    public Account getAccountByAccountId(int accountId) {
        Account accountByAccountId = new Account();
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE account_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId);
        if(rowSet.next()) {
            accountByAccountId = new Account();
            accountByAccountId.setAccountId(rowSet.getInt("account_id"));
            accountByAccountId.setUserId(rowSet.getInt("user_id"));
            accountByAccountId.setBalance(rowSet.getBigDecimal("balance"));
        }
        return accountByAccountId;
    }

    @Override
    public Account getAccountByUserId(int userId) {
        Account accountByAccountId = new Account();
        String sql = "SELECT account_id, user_id, balance FROM accounts WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if(rowSet.next()) {
            accountByAccountId.setAccountId(rowSet.getInt("account_id"));
            accountByAccountId.setUserId(rowSet.getInt("user_id"));
        //    accountByAccountId.setBalance(rowSet.getBigDecimal("balance"));
        }
        return accountByAccountId;
    }


    private Account mapRowToAccount(SqlRowSet rs) {
       Account account = new Account();
       account.setAccountId(rs.getInt("account_id"));
       account.setUserId(rs.getInt("user_id"));
       account.setBalance(rs.getBigDecimal("balance"));
       return account;
   }
}
