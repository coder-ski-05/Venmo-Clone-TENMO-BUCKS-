package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;


    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out),
                new AuthenticationService(API_BASE_URL), new TenmoService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TenmoService tenmoService) {
        this.console = console;
        this.authenticationService = authenticationService;

        this.tenmoService = tenmoService;

    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }


    private void viewCurrentBalance() {
        // TODO Auto-generated method stub

        BigDecimal balance = tenmoService.getBalance(currentUser.getToken());

        System.out.println("Your current account balance is: " + "$" + balance); // to see the balance instead of token*/

    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub
        Transfer[] transfers = tenmoService.getAllTransfers(currentUser.getToken());
        for (Transfer transfer: transfers){
            System.out.println("ID: " + transfer.getTransferId()
            + " ACCOUNT FROM: " + transfer.getAccountFrom()
                    + " ACCOUNT TO: " + transfer.getAccountTo()
                    + " PRICE: $" + transfer.getAmount());
        }

//        BigDecimal amount = tenmoService.getTransferHistory(currentUser.getToken());
//
//        System.out.println("Your account transfer history is: " + "$" + amount); // to see the balance instead of token*/


    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub


    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        //need something to work as a flag to say if an amount is over the balance in the acocunt then it won't pass through
        boolean sendBucks = true; //<- what we're saying is if the amount is less than the balance then continue to go through
        while (sendBucks) { // since we don't know how many times its gonna through the transfer once we pass in an amount
            //we put a while loop
            User[] allUsers = tenmoService.getAllUsers(currentUser.getToken());
            System.out.println("-------------------------------------------");
            System.out.println("Users");
            System.out.println("ID         NAME");
            System.out.println("-------------------------------------------");
            for (User user : allUsers) {
                System.out.println(user.getId() + "           " + user.getUsername());
            }
            System.out.println("-----------------");
            int recipient = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
            //double check all users array that response is a valid user ID
            //valid user ID
            //ask them amount
            if (recipient == 0) {
                sendBucks = false; //<- what we're saying is if the amount is higher than the balance in the acocunt
                //you will have to start from the beginning to enter a balance to transfer that is under the account balance itself
                break;
            }
            boolean amountRequested = true; //<- we're declaring a boolean to say if the amount requested is valid, then
            //go through the transfer object that was already established in the class
            BigDecimal theAmount = null; //<-- this doesn't refer to any object

            theAmount = new BigDecimal(
                    console.getUserInputInteger("Enter the amount")).setScale(2, RoundingMode.HALF_UP);
            Account fromUserAccountId = tenmoService.getAccountByUserId(currentUser.getToken(), currentUser.getUser().getId());
            Account toUserAccountId = tenmoService.getAccountByUserId(currentUser.getToken(), recipient);
            Transfer transfer = new Transfer(fromUserAccountId.getAccountId(), toUserAccountId.getAccountId(), theAmount);
            tenmoService.sendBucks(currentUser.getToken(), transfer);
//            tenmoService.receiverBalance(currentUser.getToken(), transfer);
//            tenmoService.senderBalance(currentUser.getToken(), transfer);
            //need to update sender balance after transfer
            //need to update recipient balance after transfer
            //system print of new balance

        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        boolean requestBucks = true;
        while (requestBucks) {
            User[] allUsers = tenmoService.getAllUsers(currentUser.getToken());
            System.out.println("-------------------------------------------");
            System.out.println("Users");
            System.out.println("ID         NAME");
            System.out.println("-------------------------------------------");
            for (User user : allUsers) {
                System.out.println(user.getId() + "           " + user.getUsername());
            }
            System.out.println("-----------------");
            int recipient = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");

            if (recipient == 0) {
                requestBucks = false;
                break;
            }
            boolean amountRequested = true;
            BigDecimal theAmount = null;

            theAmount = new BigDecimal(
                    console.getUserInputInteger("Enter the amount")).setScale(2, RoundingMode.HALF_UP);
            Transfer transfer = new Transfer(currentUser.getUser().getId(), recipient, theAmount);
            tenmoService.requestBucks(currentUser.getToken(), transfer);
            tenmoService.receiverBalance(currentUser.getToken(), transfer);
            tenmoService.senderBalance(currentUser.getToken(), transfer);


        }

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
