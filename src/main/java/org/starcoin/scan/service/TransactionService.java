package org.starcoin.scan.service;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.constant.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private RestHighLevelClient client;

    public Transaction get(String network,String id) throws IOException {
        GetRequest getRequest = new GetRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX), id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            return JSON.parseObject(sourceAsString, Transaction.class);
        } else {
            logger.error("not found id doc");
            return null;
        }
    }

    public List<Transaction> getRange(String network,int page, int count) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //page size
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
        }
        searchSourceBuilder.from(offset);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse);
    }

    public List<Transaction> getRangeByAddress(String network,String address, int page, int count) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
        }
        searchSourceBuilder.from(offset);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("user_transaction.raw_txn.sender", address);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Transaction> result = getSearchResult(searchResponse);
        return result;
    }

    public List<Transaction> getByBlockHash(String network,String blockHash) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_hash", blockHash);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Transaction> result = getSearchResult(searchResponse);
        return result;
    }

    public List<Transaction> getByBlockHeight(String network,int blockHeight) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_number", blockHeight);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Transaction> result = getSearchResult(searchResponse);
        return result;
    }

    private List<Transaction> getSearchResult(SearchResponse searchResponse) {
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        List<Transaction> transactions = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            transactions.add(JSON.parseObject(hit.getSourceAsString(), Transaction.class));
        }
        return transactions;
    }

}
