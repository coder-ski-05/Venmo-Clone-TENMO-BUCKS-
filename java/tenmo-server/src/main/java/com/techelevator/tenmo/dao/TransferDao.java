package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    Transfer createTransfer(Transfer transfer);
    public List<Transfer> getAllTransfers();
    public Transfer updateTransfer(Transfer transfer);
    public Transfer getTransfer(long id);
    public List<Transfer> getAllTransferById(int accountFrom);

}
