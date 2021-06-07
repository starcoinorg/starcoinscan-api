package org.starcoin.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.service.Result;
import org.starcoin.scan.service.TransactionService;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("v1/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("get/{network}/{id}")
    public Transaction getTransaction(@PathVariable("network") String network, @PathVariable("id") String id) throws IOException {
        return transactionService.get(network,id);
    }

    @GetMapping("list/{network}/page/{page}")
    public Result<Transaction> getRangeTransactions(@PathVariable("network") String network, @PathVariable("page") int page,
                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                  @RequestParam(value = "after", required = false, defaultValue = "0") int start_height) throws Exception {
        return transactionService.getRange(network, page, count,start_height);
    }

    @GetMapping("address/{network}/{address}/{page}")
    public Result<Transaction> getRangeByAddress(@PathVariable("network") String network,@PathVariable("address") String address, @PathVariable("page") int page,
                                               @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws IOException {
        return transactionService.getRangeByAddress(network,address,page,count);
    }

    @GetMapping("block_hash/{network}/{block_hash}")
    public Result<Transaction> getByBlockHash(@PathVariable("network") String network,@PathVariable("block_hash") String  blockHash) throws IOException {
        return  transactionService.getByBlockHash(network,blockHash);
    }

    @GetMapping("block_height/{network}/{block_height}")
    public Result<Transaction> getByBlockHeight(@PathVariable("network") String network, @PathVariable("block_height") int blockHeight) throws IOException {
        return transactionService.getByBlockHeight(network,blockHeight);
    }
}