package org.starcoin.scan.service;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.scan.bean.Event;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.constant.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.starcoin.scan.service.ServiceUtils.ELASTICSEARCH_MAX_HITS;


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
            logger.error("not found transaction, id: {}", id);
            return null;
        }
    }

    public Transaction getTransactionByHash(String network, String hash) throws IOException {
       return get(network,hash);
    }

    public Result<Transaction> getRange(String network,int page, int count,int start_height,int txnType) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if(txnType==0)//
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        else
            searchSourceBuilder.query(QueryBuilders.rangeQuery("transaction_index").gt(0));
        //page size
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
            if (offset >= ELASTICSEARCH_MAX_HITS && start_height > 0) {
                offset = start_height - (page - 1) * count;
                searchSourceBuilder.searchAfter(new Object[]{offset});
            }else {
                searchSourceBuilder.from(offset);
            }
        }
        searchSourceBuilder.from(offset);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse,Transaction.class);
    }

    public Result<Transaction> getRangeByAddress(String network,String address, int page, int count) throws IOException {
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
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse,Transaction.class);
    }

    public Result<Event> getEventsByAddress(String network,String address, int page, int count) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_EVENT_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
        }
        searchSourceBuilder.from(offset);

        BoolQueryBuilder exersiceBoolQuery = QueryBuilders.boolQuery();
        exersiceBoolQuery.should(QueryBuilders.termQuery("type_tag", ServiceUtils.depositEvent));
        exersiceBoolQuery.should(QueryBuilders.termQuery("type_tag", ServiceUtils.withdrawEvent));
        exersiceBoolQuery.must(QueryBuilders.termQuery("event_address", address));

        searchSourceBuilder.query(exersiceBoolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Event> events = getSearchResult(searchResponse,Event.class);
        return events;
    }

    public Result<Transaction> getRangeByAddressAll(String network,String address, int page, int count) throws IOException {
        Result<Event> events = getEventsByAddress(network,address,page,count);
        if(events.getContents().size()==0){
            return Result.EmptyResult;
        }
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder exersiceBoolQuery = QueryBuilders.boolQuery();
        for (Event event:events.getContents())
            exersiceBoolQuery.should(QueryBuilders.termQuery("transaction_hash", event.getTransactionHash()));

        searchSourceBuilder.query(exersiceBoolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Transaction> result = getSearchResult(searchResponse,Transaction.class);
        result.setTotal(events.getTotal());

        return result;
    }

    public Result<Transaction> getByBlockHash(String network,String blockHash) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_hash", blockHash);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse,Transaction.class);
    }

    public Result<Transaction> getByBlockHeight(String network,int blockHeight) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_number", blockHeight);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse,Transaction.class);
    }

    private <T> Result<T> getSearchResult(SearchResponse searchResponse,Class<T> clazz) {
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        Result<T> result = new Result<>();
        result.setTotal(searchResponse.getHits().getTotalHits().value);
        List<T> transactions = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            transactions.add(JSON.parseObject(hit.getSourceAsString(), clazz));
        }
        result.setContents(transactions);
        return result;
    }

    private <T> List<T> getSearchResultList(SearchResponse searchResponse,Class<T> clazz) {
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        List<T> result = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            result.add(JSON.parseObject(hit.getSourceAsString(), clazz));
        }
        return result;
    }

}
