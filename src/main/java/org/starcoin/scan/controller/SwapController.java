package org.starcoin.scan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.api.Result;
import org.starcoin.scan.bean.SwapStat;
import org.starcoin.scan.bean.TokenPoolStat;
import org.starcoin.scan.bean.TokenStat;
import org.starcoin.scan.repos.entity.PoolSwapDayStat;
import org.starcoin.scan.repos.entity.SwapDayStat;
import org.starcoin.scan.repos.entity.SwapTransaction;
import org.starcoin.scan.repos.entity.TokenSwapDayStat;
import org.starcoin.scan.service.SwapService;
import org.starcoin.scan.service.TransactionWithEvent;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Api(tags = "swap")
@RestController
@RequestMapping("v2/swap")
public class SwapController {

    @Autowired
    private SwapService swapService;

    @ApiOperation("get swap transaction list")
    @GetMapping("/transactions/{network}")
    public List<SwapTransaction> swapTransactionsList(@PathVariable("network") String network,
                                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                      @RequestParam(value = "start_height", required = false, defaultValue = "0") int startHeight,
                                                      @RequestParam("filter_type") String filterType) throws IOException {
        return swapService.swapTransactionsList(network,count,startHeight,filterType);
    }

    @ApiOperation("get swap transaction list")
    @GetMapping("/transactions/token/{network}")
    public List<SwapTransaction> swapTransactionsListByToken(@PathVariable("network") String network,
                                                      @RequestParam(value = "token_name",required = true)String tokenName,
                                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                      @RequestParam(value = "start_height", required = false, defaultValue = "0") int startHeight,
                                                      @RequestParam("filter_type") String filterType) throws IOException {
        return swapService.swapTransactionsListByTokenName(network,tokenName,count,startHeight,filterType);
    }

    @ApiOperation("get swap transaction list")
    @GetMapping("/transactions/pool/{network}")
    public List<SwapTransaction> swapTransactionsListByPool(@PathVariable("network") String network,
                                                            @RequestParam(value = "pool_name",required = true)String poolName,
                                                             @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                             @RequestParam(value = "start_height", required = false, defaultValue = "0") int startHeight,
                                                             @RequestParam("filter_type") String filterType) throws IOException {
        return swapService.swapTransactionsListByPoolName(network,poolName,count,startHeight,filterType);
    }

    @ApiOperation("get token stat list")
    @GetMapping("/token_stats/{network}/page/{page}")
    public List<TokenSwapDayStat> getTokenStatList(@PathVariable("network") String network, @PathVariable("page") int page,
                                                     @RequestParam(value = "count", required = false, defaultValue = "20") int count){
        return swapService.getTokenStatList(network,page,count);
    }

    @ApiOperation("get token stat by token name")
    @GetMapping("/token_stats/{network}/{token_name}")
    public TokenSwapDayStat getTokenStat(@PathVariable("network") String network, @PathVariable("token_name")String  tokenName){
        return swapService.getTokenStat(network,tokenName);
    }


    @ApiOperation("get token pool stat list")
    @GetMapping("/token_pool_stats/{network}/page/{page}")
    public List<PoolSwapDayStat> getTokenPoolStatList(@PathVariable("network") String network, @PathVariable("page") int page,
                                                        @RequestParam(value = "count", required = false, defaultValue = "20") int count){
        return swapService.getTokenPoolStatList(network,page,count);
    }

    @ApiOperation("get token pool stat list by token name")
    @GetMapping("/token_pool_stats/{network}/{token_name}/page/{page}")
    public List<PoolSwapDayStat> getTokenPoolStatListByTokenName(@PathVariable("network") String network, @PathVariable("token_name")String  tokenName,
                                                                 @PathVariable("page") int page,
                                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count){
        return swapService.getTokenPoolStatListByTokenName(network,tokenName,page,count);
    }

    @ApiOperation("get token pool stat by name")
    @GetMapping("/token_pool_stats/{network}/{pool_name}")
    public PoolSwapDayStat getTokenPoolStat(@PathVariable("network") String network,
                                            @PathVariable("pool_name")String  poolName){
        return swapService.getTokenPoolStat(network,poolName);
    }

    @ApiOperation("get swap stat list")
    @GetMapping("/swap_stats/{network}/page/{page}")
    public List<SwapDayStat> getSwapStatList(@PathVariable("network") String network, @PathVariable("page") int page,
                                             @RequestParam(value = "count", required = false, defaultValue = "20") int count){
        return swapService.getSwapStatList(network,page,count);
    }
}

