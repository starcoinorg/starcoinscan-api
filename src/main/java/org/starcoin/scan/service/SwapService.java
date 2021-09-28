package org.starcoin.scan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.api.Result;
import org.starcoin.scan.bean.SwapStat;
import org.starcoin.scan.repos.PoolSwapDayStatRepository;
import org.starcoin.scan.repos.SwapDayStatRepository;
import org.starcoin.scan.repos.SwapTransactionRepository;
import org.starcoin.scan.repos.TokenSwapDayStatRepository;
import org.starcoin.scan.repos.entity.PoolSwapDayStat;
import org.starcoin.scan.repos.entity.SwapDayStat;
import org.starcoin.scan.repos.entity.SwapTransaction;
import org.starcoin.scan.repos.entity.TokenSwapDayStat;
import org.starcoin.scan.utils.CommonUtils;

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
    private SwapTransactionRepository swapTransactionRepository;

    @Autowired
    private PoolSwapDayStatRepository poolSwapDayStatRepository;

    @Autowired
    private TokenSwapDayStatRepository tokenSwapDayStatRepository;

    @Autowired
    private SwapDayStatRepository swapDayStatRepository;

    public List<SwapTransaction> swapTransactionsList(String network, int count, int startId, String filterType) throws IOException {
        if(filterType.equals("all")){
            return swapTransactionRepository.find(network,startId,count);
        }else{
            int swapType = filterMap.get(filterType);
            return swapTransactionRepository.findByType(network,swapType,startId,count);
        }
    }

    public List<SwapTransaction> swapTransactionsListByTokenName(String network,String tokenName, int count, int startId, String filterType) throws IOException {
        if(filterType.equals("all")){
            return swapTransactionRepository.findByTokenName(network,tokenName,startId,count);
        }else{
            int swapType = filterMap.get(filterType);
            return swapTransactionRepository.findByTypeAndTokenName(network,tokenName,swapType,startId,count);
        }
    }

    public List<SwapTransaction> swapTransactionsListByPoolName(String network,String poolName ,int count, int startId, String filterType) throws IOException {
        String[] tokens = poolName.split("/");
        if (tokens== null || tokens.length!=2){
            return null;
        }

        if(filterType.equals("all")){
            return swapTransactionRepository.findByTokenPair(network,tokens[0].trim(),tokens[1].trim(),startId,count);
        }else{
            int swapType = filterMap.get(filterType);
            return swapTransactionRepository.findByTypeAndTokenPair(network,tokens[0].trim(),tokens[1].trim(),swapType,startId,count);
        }
    }

    public List<TokenSwapDayStat> getTokenStatList(String network, int page, int count){
        return tokenSwapDayStatRepository.findAll(CommonUtils.getOffset(page,count),count,network);
    }

    public List<PoolSwapDayStat> getTokenPoolStatList(String network, int page, int count){
        return poolSwapDayStatRepository.findAll(CommonUtils.getOffset(page,count),count,network);
    }

    public List<SwapDayStat> getSwapStatList(String network, int page, int count){
        return swapDayStatRepository.findAll(CommonUtils.getOffset(page,count),count,network);
    }

    public PoolSwapDayStat getTokenPoolStat(String network, String poolName) {
        String[] tokens = poolName.split("/");
        if(tokens== null || tokens.length!=2){
            return null;
        }

        return poolSwapDayStatRepository.find(network,tokens[0].trim(),tokens[1].trim());
    }

    public TokenSwapDayStat getTokenStat(String network, String tokenName) {
        return tokenSwapDayStatRepository.find(network,tokenName);
    }

    public List<PoolSwapDayStat> getTokenPoolStatListByTokenName(String network, String tokenName, int page, int count) {
        return poolSwapDayStatRepository.findAll(CommonUtils.getOffset(page,count),count,network,tokenName);
    }
}
