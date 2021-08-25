package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TenmoService {
   // public static String aut_token = ""; //ANYONE HAS ACCESS TO IT
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();


    public TenmoService(String url) {
        BASE_URL = url;
    }

    public BigDecimal getBalance(String token) {
        Balance accountBalance = new Balance();
        try {
            accountBalance = restTemplate.exchange(BASE_URL + "balance" , HttpMethod.GET,
                    makeAuthEntity(token), Balance.class).getBody();
        } catch (RestClientException e) {
            System.out.println("Cannot find user balance.");
        }
        return accountBalance.getBalance();
    }


    public BigDecimal getTransferHistory(String token) {
        TransferHistory transferHistory = new TransferHistory();
        try {
            transferHistory = restTemplate.exchange(BASE_URL + "amount" , HttpMethod.GET,
                    makeAuthEntity(token), TransferHistory.class).getBody();
        } catch (RestClientException e) {
            System.out.println("Cannot find account transfer history.");
        }
        return transferHistory.getTransferHistory();
    }


  public Transfer senderBalance(String token, Transfer transfer) {
        Transfer transfers = new Transfer();
        try {
            transfers = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.POST,
                    makeAuthEntity(token, transfer), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Cannot update senders Balance");
        }
        return transfers;
    }

    public Transfer receiverBalance(String token, Transfer transfer) {
        Transfer transfers = new Transfer();
        try {
            transfers = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.POST,
                    makeAuthEntity(token, transfer), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Cannot update receivers Balance");
        }
        return transfers;
    }

    private HttpEntity makeAuthEntity(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        HttpEntity entity = new HttpEntity<>(h);
        return entity;
    }
    public User[] getAllUsers(String token) {
        return restTemplate.exchange(BASE_URL + "users", HttpMethod.GET, makeAuthEntity(token),
                User[].class).getBody();
    }

    public Transfer[] getAllTransfers(String token) {
        return restTemplate.exchange(BASE_URL + "transfers", HttpMethod.GET, makeAuthEntity(token),
                Transfer[].class).getBody();
    }

    public Transfer getSingleTransfer(String token, Transfer transfer) {
        Transfer singleTransfer = null;
        try {
            singleTransfer = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.GET,
                    makeAuthEntity(token, transfer), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
                System.out.println("Cannot find user transfer.");
        }
        return singleTransfer;
    }


    public Account getAccountByUserId(String token, int userId){
        return restTemplate.exchange(BASE_URL+"account?userId="+userId,HttpMethod.GET, makeAuthEntity(token),Account.class).getBody();
    }
    public Transfer sendBucks(String token, Transfer transfer) {
        Transfer singleTransfer = null;
        try {
            singleTransfer = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.POST,
                    makeAuthEntity(token, transfer), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Cannot send bucks to user.");
        }
        return singleTransfer;
    }

    public Transfer requestBucks(String token, Transfer transfer) {
        Transfer singleTransfer = null;
        try {
            singleTransfer = restTemplate.exchange(BASE_URL + "transfers", HttpMethod.POST,
                    makeAuthEntity(token, transfer), Transfer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Cannot request bucks from user.");
        }
        return singleTransfer;
    }


    private HttpEntity<Transfer> makeAuthEntity(String token, Transfer transfer) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, h);
        return entity;
    }

}