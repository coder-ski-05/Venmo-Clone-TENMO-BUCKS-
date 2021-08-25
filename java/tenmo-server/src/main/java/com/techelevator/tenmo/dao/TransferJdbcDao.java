package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferJdbcDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;
     TransferDao transferDao;
    private AccountDAO accountDAO;
    UserDao userDao;

    public TransferJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer getTransfer(long id) {
        String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        Transfer transfer = null;
        if(results.next()){
            transfer=mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getAllTransferById(int accountFrom) {
        List<Transfer> transfer = new ArrayList<>();
        String sql = "SELECT * FROM transfers WHERE account_from = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountFrom);
        while(results.next()){
            transfer.add(mapRowToTransfer(results));
        }
        return transfer;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        //select all transfers
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfers";
        //Transfer transfer = new Transfer();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while(rowSet.next()){
            transfers.add(mapRowToTransfer(rowSet));
        }
        return transfers;
    }

    @Override
    public Transfer createTransfer (Transfer transfer) {

        System.out.println("DEBUG");
        System.out.println(transfer.getAccountFrom());
        System.out.println(transfer.getAccountTo());

        String sql = "INSERT INTO transfers ( transfer_type_id, transfer_status_id, account_from, " +
                "account_to, amount) " + "VALUES (?, ?, ?, ?, ?)" +
                "RETURNING transfer_id";
        //Log transfer to the transfer table
        int id = jdbcTemplate.queryForObject(sql, Integer.class,
                 transfer.getTransferTypeId(), transfer.getTransferStatusId(),
                transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return getTransfer(id);
    }

    @Override
    public Transfer updateTransfer(Transfer transfer){
        String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?" +
                "RETURNING transfer_id";
        int id = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferStatusId(),
                transfer.getTransferId());
        return getTransfer(id);
    }


    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
