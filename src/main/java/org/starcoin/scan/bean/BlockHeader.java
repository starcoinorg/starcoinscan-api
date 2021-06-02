package org.starcoin.scan.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class BlockHeader {
    private String block_hash;
    @JSONField(name = "number")
    private long height;
    private String author;
    private int gas_used;
    private String parentHash;

    public void setBlock_hash(String block_hash) {
        this.block_hash = block_hash;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGas_used(int gas_used) {
        this.gas_used = gas_used;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public String getBlock_hash() {
        return block_hash;
    }

    public long getHeight() {
        return height;
    }

    public String getAuthor() {
        return author;
    }

    public int getGas_used() {
        return gas_used;
    }

    public String getParentHash() {
        return parentHash;
    }
}
