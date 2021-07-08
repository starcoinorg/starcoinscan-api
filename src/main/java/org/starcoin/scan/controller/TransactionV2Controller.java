package org.starcoin.scan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.scan.bean.Event;
import org.starcoin.scan.bean.PendingTransaction;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.service.Result;
import org.starcoin.scan.service.TransactionService;

import java.io.IOException;

@Api(tags = "transaction")
@RestController
@RequestMapping("v2/transaction")
public class TransactionV2Controller {
    @Autowired
    private TransactionService transactionService;

    @ApiOperation("get transaction by ID")
    @GetMapping("/{network}/{id}")
    public Transaction getTransaction(@PathVariable("network") String network, @PathVariable("id") String id) throws IOException {
        return transactionService.get(network, id);
    }

    @ApiOperation("get transaction by hash")
    @GetMapping("/{network}/hash/{hash}")
    public Transaction getTransactionByHash(@PathVariable("network") String network, @PathVariable("hash") String hash) throws IOException {
        return transactionService.getTransactionByHash(network, hash);
    }

    @ApiOperation("get transaction list")
    @GetMapping("/{network}/page/{page}")
    public Result<Transaction> getRangeTransactions(@PathVariable("network") String network, @PathVariable("page") int page,
                                                    @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                    @RequestParam(value = "start_height", required = false, defaultValue = "0") int startHeight,
                                                    @RequestParam(value = "txn_type", required = false, defaultValue = "1") int txnType) throws Exception {
        return transactionService.getRange(network, page, count, startHeight, txnType);
    }

    @ApiOperation("get pending transaction list")
    @GetMapping("/pending_txns/{network}/page/{page}")
    public Result<PendingTransaction> getRangePendingTransactions(@PathVariable("network") String network, @PathVariable("page") int page,
                                                                  @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                                  @RequestParam(value = "start_height", required = false, defaultValue = "0") int startHeight) throws Exception {
        return transactionService.getRangePendingTransaction(network, page, count, startHeight);
    }

    @ApiOperation("get pending transaction by ID")
    @GetMapping("/pending_txn/get/{network}/{id}")
    public PendingTransaction getPendingTransaction(@PathVariable("network") String network, @PathVariable("id") String id) throws IOException {
        return transactionService.getPending(network, id);
    }

    @ApiOperation("get transaction list by address")
    @GetMapping("{network}/byAddress/{address}")
    public Result<Transaction> getRangeByAddressAlias(@PathVariable("network") String network, @PathVariable("address") String address,
                                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws IOException {
        return transactionService.getRangeByAddressAll(network, address, 1, count);
    }

    @ApiOperation("get transaction list of page range by address")
    @GetMapping("/address/{network}/{address}/page/{page}")
    public Result<Transaction> getRangeByAddress(@PathVariable("network") String network, @PathVariable("address") String address, @PathVariable(value = "page", required = false) int page,
                                                 @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws IOException {
        return transactionService.getRangeByAddressAll(network, address, page, count);
    }

    @ApiOperation("get transaction by block")
    @GetMapping("/{network}/byBlock/{block_hash}")
    public Result<Transaction> getByBlockHash(@PathVariable("network") String network, @PathVariable("block_hash") String blockHash) throws IOException {
        return transactionService.getByBlockHash(network, blockHash);
    }

    @ApiOperation("get transaction by block height")
    @GetMapping("/{network}/byBlockHeight/{block_height}")
    public Result<Transaction> getByBlockHeight(@PathVariable("network") String network, @PathVariable("block_height") int blockHeight) throws IOException {
        return transactionService.getByBlockHeight(network, blockHeight);
    }

    @ApiOperation("get transaction events by tag")
    @GetMapping("{network}/events/byTag/{tag_name}/page/{page}")
    public Result<Event> getEventsByTag(@PathVariable("network") String network, @PathVariable("tag_name") String tag_name,
                                        @PathVariable(value = "page", required = false) int page,
                                        @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws IOException {
        return transactionService.getEvents(network, tag_name, page, count);
    }
}