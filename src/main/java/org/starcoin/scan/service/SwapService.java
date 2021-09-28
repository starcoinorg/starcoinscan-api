package org.starcoin.scan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.api.Result;
import org.starcoin.scan.bean.SwapStat;
import org.starcoin.scan.bean.TokenPair;
import org.starcoin.scan.bean.TokenPoolStat;
import org.starcoin.scan.bean.TokenStat;
import org.starcoin.scan.repos.PoolSwapDayStatRepository;
import org.starcoin.scan.repos.SwapTransactionRepository;
import org.starcoin.scan.repos.TokenSwapDayStatRepository;
import org.starcoin.scan.repos.entity.SwapTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class SwapService {

    private static final Map<String,Integer> filterMap = new HashMap<String,Integer>(){{
        put("remove", 1);
        put("add",2);
    }};;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SwapTransactionRepository swapTransactionRepository;

    @Autowired
    private PoolSwapDayStatRepository poolSwapDayStatRepository;

    @Autowired
    private TokenSwapDayStatRepository tokenSwapDayStatRepository;

    public List<SwapTransaction> swapTransactionsList(String network, int count, int startId, String filterType) throws IOException {
        if(filterType.equals("all")){
            return swapTransactionRepository.find(network,startId,count);
        }else{
            int swapType = filterMap.get(filterType);
            return swapTransactionRepository.findByType(network,swapType,startId,count);
        }
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

    public Result<SwapStat> getSwapStatList(String network, Date startDate, Date endDate){
        List<SwapStat> swapStats = new ArrayList<>();
        swapStats.add(new SwapStat(new Date(2021,9,10),BigDecimal.ZERO,BigDecimal.ZERO));
        swapStats.add(new SwapStat(new Date(2021,9,11),BigDecimal.ZERO,BigDecimal.ZERO));
        swapStats.add(new SwapStat(new Date(2021,9,12),BigDecimal.ZERO,BigDecimal.ZERO));

        Result<SwapStat> tokenStatResult = new Result<>();
        tokenStatResult.setContents(swapStats);
        tokenStatResult.setTotal(swapStats.size());

        return tokenStatResult;
    }
}
