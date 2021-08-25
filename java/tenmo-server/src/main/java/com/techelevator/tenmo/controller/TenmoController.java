package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.security.UserNotActivatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

    @Autowired
    private TransferDao transferDao;
    @Autowired
    private AccountDAO dao;
    @Autowired
    private UserDao userDao;


//    @RequestMapping(path="/transfers", method = RequestMethod.GET)
//    public List<Transfer> getAlltransfers() {
//        return transferDao.getAllTransfers();
//    }

    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public Balance getBalance (Principal principal ){
        //user has to log in first to obtain a token
        System.out.println(principal.getName());
        return dao.getBalance(principal.getName());
    }

    @RequestMapping(path= "/users", method = RequestMethod.GET)
    public List<User> getAllUsers(Principal principal) {
        List<User> filteredList = new ArrayList<>();
        for (User user: userDao.findAll()) {
            if (!user.getUsername().toLowerCase().equals(principal.getName().toLowerCase())) {
                filteredList.add(user);
            }
        }
        return filteredList;
    }

    //TO DO
    @RequestMapping(path= "/accounts", method = RequestMethod.GET)
    public List<Account> getAllAccounts() {
        return dao.getAllAccounts();
    }


    @RequestMapping(path="/account", method = RequestMethod.GET)
    public Account getAccount(@RequestParam int userId){
        return dao.getAccountByUserId(userId);
    }
    @RequestMapping(path= "/transfers", method = RequestMethod.POST)
    public Transfer createTransfer(@RequestBody Transfer transfer) {
        Transfer transfers = transferDao.createTransfer(transfer);
        //this will implement the add and subtract method for update balance
        updateBalance(transfer);
        //this updates will update the receivers and senders balance
        System.out.println(transfer.getAccountFrom());
        System.out.println(transfer.getAccountTo());
        System.out.println(transfer.getAmount());
        return transfers;
    }
    public void updateBalance(Transfer transfer) {
        dao.receiversBalance(transfer.getAccountTo(), transfer);
        dao.sendersBalance(transfer.getAccountFrom(), transfer);
    }

    @RequestMapping(path= "/balance", method = RequestMethod.POST)
    public void setBalance(@RequestBody Balance balance, @PathVariable int id) {
        dao.setBalance(balance, id);
    }

    @RequestMapping(path= "/singleTransfer/{id}", method = RequestMethod.GET)
    public Transfer transfersById(int id) {
        return transferDao.getTransfer(id);
    }
    //As an authenticated user of the system, I need to be able to retrieve the details
    // of any transfer based upon the transfer ID.

    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<Transfer> viewTransferHistory(Principal principal){
        return transferDao.getAllTransferById(dao.getAccountByUserId(userDao.findIdByUsername(principal.getName())).getAccountId());
    }


}
