package org.starcoin.scan.service;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.constant.Constant;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private RestHighLevelClient client;

    public Transaction get(String id) throws IOException {
        logger.info("transaction id is " + id);
        GetRequest getRequest = new GetRequest(Constant.TRANSACTION_INDEX, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            logger.info(sourceAsString);
            return JSON.parseObject(sourceAsString, Transaction.class);
        } else {
            logger.error("not found id doc");
            return null;
        }
    }

    public Map<String, Transaction> multiGet(List<String> ids) {
        return null;
    }

    public List<Transaction> getRange(int page, int count) {
        return null;
    }

    public List<Transaction> getRangeByAddress(String address, int page, int count) {
        return null;
    }

    public List<Transaction> getByBlockHash(String blockHash) {
        return null;
    }

    public List<Transaction> getByBlockHeight(int blockHeight) {
        return null;
    }
}
