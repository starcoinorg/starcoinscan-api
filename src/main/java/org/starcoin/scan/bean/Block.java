package org.starcoin.scan.bean;

public class Block {
    public BlockHeader getHeader() {
        return header;
    }

    public void setHeader(BlockHeader header) {
        this.header = header;
    }

    private BlockHeader header;

}
