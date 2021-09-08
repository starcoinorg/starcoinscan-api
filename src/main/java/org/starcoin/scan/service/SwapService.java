package org.starcoin.scan.service;

import org.springframework.stereotype.Service;
import org.starcoin.api.Result;
import org.starcoin.bean.Transaction;

@Service
public class SwapService {

    public Result<Transaction> swapTransactionsList(String network, int page, int count, int startHeight, String filterType){
        return null;
    }
}
