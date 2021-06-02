package org.starcoin.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.service.TransactionService;

import java.io.IOException;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/get")
    public Transaction getTransaction(@RequestParam String id) throws IOException {
        return transactionService.get(id);
    }

}