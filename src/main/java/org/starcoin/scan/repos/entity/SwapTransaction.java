package org.starcoin.scan.repos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name = "swap_transaction")
@Entity
public class SwapTransaction {
    @Id
    @Column(name = "transaction_hash", nullable = false, length = 66)
    private String id;

    @Column(name = "total_value", precision = 131089)
    private BigDecimal totalValue;

    @Column(name = "token_a", nullable = false, length = 512)
    private String tokenA;

    @Column(name = "amount_a", nullable = false, precision = 131089)
    private BigDecimal amountA;

    @Column(name = "token_b", nullable = false, length = 512)
    private String tokenB;

    @Column(name = "amount_b", nullable = false, precision = 131089)
    private BigDecimal amountB;

    @Column(name = "account", nullable = false, length = 34)
    private String account;

    @Column(name = "ts", nullable = false)
    private Long ts;

    @Column(name = "swap_type", nullable = false)
    private Integer swapType;

    public Integer getSwapType() {
        return swapType;
    }

    public void setSwapType(Integer swapType) {
        this.swapType = swapType;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getAmountB() {
        return amountB;
    }

    public void setAmountB(BigDecimal amountB) {
        this.amountB = amountB;
    }

    public String getTokenB() {
        return tokenB;
    }

    public void setTokenB(String tokenB) {
        this.tokenB = tokenB;
    }

    public BigDecimal getAmountA() {
        return amountA;
    }

    public void setAmountA(BigDecimal amountA) {
        this.amountA = amountA;
    }

    public String getTokenA() {
        return tokenA;
    }

    public void setTokenA(String tokenA) {
        this.tokenA = tokenA;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}