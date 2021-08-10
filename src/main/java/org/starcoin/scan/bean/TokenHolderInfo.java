package org.starcoin.scan.bean;

import java.math.BigInteger;

public class TokenHolderInfo {

    private String address;
    private BigInteger supply;
    private BigInteger holdAmount;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getSupply() {
        return supply;
    }

    public void setSupply(BigInteger supply) {
        this.supply = supply;
    }

    public BigInteger getHoldAmount() {
        return holdAmount;
    }

    public void setHoldAmount(BigInteger holdAmount) {
        this.holdAmount = holdAmount;
    }
}
