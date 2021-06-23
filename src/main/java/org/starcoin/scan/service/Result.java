package org.starcoin.scan.service;

import java.util.List;

public class Result<T> {

    public static final Result EmptyResult = new Result<>();
    private List<T> contents;
    private long total;

    public List<T> getContents() {
        return contents;
    }

    public void setContents(List<T> contents) {
        this.contents = contents;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}
