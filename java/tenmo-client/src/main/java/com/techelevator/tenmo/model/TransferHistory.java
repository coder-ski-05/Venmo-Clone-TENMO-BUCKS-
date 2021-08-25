package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferHistory {
    private BigDecimal amount;

    public TransferHistory() {
    }

    public BigDecimal getTransferHistory() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
