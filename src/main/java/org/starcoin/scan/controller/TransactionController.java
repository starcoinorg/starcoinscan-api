package org.starcoin.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.scan.bean.PendingTransaction;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.service.Result;
import org.starcoin.scan.service.TransactionService;

import java.io.IOException;

@RestController
@RequestMapping("v1/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/get/{network}/{id}")
    public Transaction getTransaction(@PathVariable("network") String network, @PathVariable("id") String id) throws IOException {
        return transactionService.get(network,id);
    }

    @GetMapping("/{network}/hash/{hash}")
    public Transaction getTransactionByHash(@PathVariable("network") String network, @PathVariable("hash") String hash) throws IOException {
        return transactionService.getTransactionByHash(network,hash);
    }

    @GetMapping("/list/{network}/page/{page}")
    public Result<Transaction> getRangeTransactions(@PathVariable("network") String network, @PathVariable("page") int page,
                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                  @RequestParam(value = "after", required = false, defaultValue = "0") int startHeight,
                                                    @RequestParam(value = "txn_type", required = false, defaultValue = "1") int txnType) throws Exception {
        return transactionService.getRange(network, page, count,startHeight,txnType);
    }

    @GetMapping("/pending_txns/{network}/page/{page}")
    public Result<PendingTransaction> getRangePendingTransactions(@PathVariable("network") String network, @PathVariable("page") int page,
                                                                  @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                                  @RequestParam(value = "after", required = false, defaultValue = "0") int startHeight) throws Exception {
        return transactionService.getRangePendingTransaction(network, page, count,startHeight);
    }

    @GetMapping("/pending_txn/get/{network}/{id}")
    public PendingTransaction getPendingTransaction(@PathVariable("network") String network, @PathVariable("id") String id) throws IOException {
        return transactionService.getPending(network,id);
    }

    @GetMapping("{network}/byAddress/{address}")
    public Result<Transaction> getRangeByAddressAlias(@PathVariable("network") String network,@PathVariable("address") String address,
                                                 @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws IOException {
        return transactionService.getRangeByAddressAll(network,address,1,count);
    }

    @GetMapping("/address/{network}/{address}/page/{page}")
    public Result<Transaction> getRangeByAddress(@PathVariable("network") String network,@PathVariable("address") String address, @PathVariable(value = "page", required = false) int page,
                                               @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws IOException {
        return transactionService.getRangeByAddressAll(network,address,page,count);
    }

    @GetMapping("/{network}/byBlock/{block_hash}")
    public Result<Transaction> getByBlockHash(@PathVariable("network") String network,@PathVariable("block_hash") String  blockHash) throws IOException {
        return  transactionService.getByBlockHash(network,blockHash);
    }

    @GetMapping("/{network}/byBlockHeight/{block_height}")
    public Result<Transaction> getByBlockHeight(@PathVariable("network") String network, @PathVariable("block_height") int blockHeight) throws IOException {
        return transactionService.getByBlockHeight(network,blockHeight);
    }
}