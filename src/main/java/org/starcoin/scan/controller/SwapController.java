package org.starcoin.scan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.api.Result;
import org.starcoin.bean.Transaction;
import org.starcoin.scan.service.SwapService;

@Api(tags = "swap")
@RestController
@RequestMapping("v2/swap")
public class SwapController {

    @Autowired
    private SwapService swapService;

    @ApiOperation("get swap transaction list")
    @GetMapping("/transactions/{network}/page/{page}")
    public Result<Transaction> swapTransactionsList(@PathVariable("network") String network, @PathVariable("page") int page,
                                                    @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                    @RequestParam(value = "start_height", required = false, defaultValue = "0") int startHeight,
                                                    @RequestParam("filter_type") String filterType){
        return null;
    }
}

