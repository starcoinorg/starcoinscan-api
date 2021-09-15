package org.starcoin.scan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.api.Result;
import org.starcoin.bean.Transaction;
import org.starcoin.scan.bean.TokenPair;
import org.starcoin.scan.bean.TokenPoolStat;
import org.starcoin.scan.bean.TokenStat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SwapService {

    @Autowired
    private TransactionService transactionService;

    public Result<TransactionWithEvent> swapTransactionsList(String network, int page, int count, int startHeight, String filterType) throws IOException {
        return transactionService.getRange(network,page,count,startHeight,0);
    }

    public Result<TokenStat> getTokenStatList(String network, int page, int count){
        List<TokenStat> tokenStats = new ArrayList<>();
        TokenStat tokenStat = new TokenStat("STC", BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
        tokenStats.add(tokenStat);
        Result<TokenStat> tokenStatResult = new Result<>();
        tokenStatResult.setContents(tokenStats);
        tokenStatResult.setTotal(tokenStats.size());
        return tokenStatResult;
    }

    public Result<TokenPoolStat> getTokenPoolStatList(String network, int page, int count){
        List<TokenPoolStat> tokenStats = new ArrayList<>();
        TokenPoolStat tokenStat = new TokenPoolStat(new TokenPair("STC","USDT"), BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
        tokenStats.add(tokenStat);
        Result<TokenPoolStat> tokenStatResult = new Result<>();
        tokenStatResult.setContents(tokenStats);
        tokenStatResult.setTotal(tokenStats.size());
        return tokenStatResult;
    }
}
