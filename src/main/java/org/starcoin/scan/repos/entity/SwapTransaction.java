package org.starcoin.scan.repos.entity;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "swap_transaction", indexes = {
        @Index(name = "txn_hash_unq", columnList = "transaction_hash", unique = true)
})
@Entity
public class SwapTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swap_seq", nullable = false)
    @JSONField(name = "swap_seq")
    private Integer id;

    @Column(name = "transaction_hash", nullable = false, length = 66)
    @JSONField(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "total_value", precision = 131089)
    @JSONField(name = "total_value")
    private BigDecimal totalValue;

    @Column(name = "token_a", nullable = false, length = 512)
    @JSONField(name = "token_x")
    private String tokenA;

    @Column(name = "amount_a", nullable = false, precision = 131089)
    @JSONField(name = "x_amount")
    private BigDecimal amountA;

    @Column(name = "token_b", nullable = false, length = 512)
    @JSONField(name = "token_y")
    private String tokenB;

    @Column(name = "amount_b", nullable = false, precision = 131089)
    @JSONField(name = "y_amount")
    private BigDecimal amountB;

    @Column(name = "account", nullable = false, length = 34)
    @JSONField(name = "account")
    private String account;

    @Column(name = "ts", nullable = false)
    private Long ts;

    @Column(name = "swap_type", nullable = false)
    @JSONField(name = "swap_type")
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

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}