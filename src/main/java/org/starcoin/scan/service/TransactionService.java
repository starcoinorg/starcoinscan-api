package org.starcoin.scan.service;

import com.alibaba.fastjson.JSON;
import com.novi.serde.DeserializationError;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.api.Result;
import org.starcoin.bean.Event;
import org.starcoin.bean.PendingTransaction;
import org.starcoin.scan.constant.Constant;
import org.starcoin.types.AccountAddress;
import org.starcoin.types.event.ProposalCreatedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.starcoin.scan.service.ServiceUtils.ELASTICSEARCH_MAX_HITS;
import static org.starcoin.scan.service.ServiceUtils.getSearchUnescapeResult;
import static org.starcoin.scan.utils.CommonUtils.hexToByteArray;


@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private RestHighLevelClient client;

    public TransactionWithEvent get(String network, String id) throws IOException {
        GetRequest getRequest = new GetRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX), id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            TransactionWithEvent transaction = JSON.parseObject(sourceAsString, TransactionWithEvent.class);
            //get events
            List<String> txnHashes = new ArrayList<>();
            txnHashes.add(transaction.getTransactionHash());
            Result<Event> events = getEventsByTransaction(network, txnHashes);
            transaction.setEvents(events.getContents());
            return transaction;
        } else {
            logger.error("not found transaction, id: {}", id);
            return null;
        }
    }

    public TransactionWithEvent getTransactionByHash(String network, String hash) throws IOException {
        return get(network, hash);
    }

    public Result<TransactionWithEvent> getRange(String network, int page, int count, int start_height, int txnType) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (txnType == 0)//
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
            } else {
                searchSourceBuilder.from(offset);
            }
        }
        searchSourceBuilder.from(offset);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return ServiceUtils.getSearchResult(searchResponse, TransactionWithEvent.class);
    }

    public Result<PendingTransaction> getRangePendingTransaction(String network, int page, int count, int start_height) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.PendingTxnIndex));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        //page size
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
            if (offset >= ELASTICSEARCH_MAX_HITS && start_height > 0) {
                offset = start_height - (page - 1) * count;
                searchSourceBuilder.searchAfter(new Object[]{offset});
            } else {
                searchSourceBuilder.from(offset);
            }
        }
        searchSourceBuilder.from(offset);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return ServiceUtils.getSearchResult(searchResponse, PendingTransaction.class);
    }

    public PendingTransaction getPending(String network, String id) throws IOException {
        GetRequest getRequest = new GetRequest(ServiceUtils.getIndex(network, Constant.PendingTxnIndex), id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            return JSON.parseObject(sourceAsString, PendingTransaction.class);
        } else {
            logger.error("not found transaction, id: {}", id);
            return null;
        }
    }

    public Result<TransactionWithEvent> getRangeByAddress(String network, String address, int page, int count) throws IOException {
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
        return ServiceUtils.getSearchResult(searchResponse, TransactionWithEvent.class);
    }

    public Result<Event> getProposalEvents(String network, String eventAddress) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_EVENT_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(ELASTICSEARCH_MAX_HITS);
        searchSourceBuilder.from(0);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.matchQuery("tag_name", ServiceUtils.proposalCreatedEvent));
        boolQuery.must(QueryBuilders.rangeQuery("transaction_index").gt(0));

        searchSourceBuilder.query(boolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Event> events = getSearchUnescapeResult(searchResponse, Event.class);
        List<Event> proposalEvents = new ArrayList<>();
        byte[] addressBytes = hexToByteArray(eventAddress);
        AccountAddress proposer = null;
        try {
            proposer = AccountAddress.bcsDeserialize(addressBytes);
        } catch (DeserializationError deserializationError) {
            deserializationError.printStackTrace();
        }
        for (Event event : events.getContents()) {
            byte[] proposalBytes = hexToByteArray(event.getData());
            try {
                ProposalCreatedEvent payload = ProposalCreatedEvent.bcsDeserialize(proposalBytes);

                if (payload.proposer.equals(proposer)) {
                    proposalEvents.add(event);
                }
            } catch (DeserializationError deserializationError) {
                deserializationError.printStackTrace();
            }
        }
        events.setContents(proposalEvents);
        events.setTotal(proposalEvents.size());
        return events;
    }

    public Result<Event> getEventsByAddress(String network, String address, int page, int count) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_EVENT_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
        }
        searchSourceBuilder.from(offset);

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.should(QueryBuilders.termQuery("tag_name", ServiceUtils.depositEvent));
        boolQuery.should(QueryBuilders.termQuery("tag_name", ServiceUtils.withdrawEvent));
        boolQuery.must(QueryBuilders.termQuery("event_address", address));
        boolQuery.must(QueryBuilders.rangeQuery("transaction_index").gt(0));

        searchSourceBuilder.query(boolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchUnescapeResult(searchResponse, Event.class);
    }

    public Result<TransactionWithEvent> getRangeByAddressAll(String network, String address, int page, int count) throws IOException {
        Result<Event> events = getEventsByAddress(network, address, page, count);
        Result<Event> proposalEvents = getProposalEvents(network, address);
        long total = events.getTotal() + proposalEvents.getTotal();
        if (total == 0) {
            return Result.EmptyResult;
        }
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);

        BoolQueryBuilder exersiceBoolQuery = QueryBuilders.boolQuery();
        List<String> termHashes = new ArrayList<>();
        for (Event event : events.getContents()) {
            termHashes.add(event.getTransactionHash());
        }
        for (Event event : proposalEvents.getContents()) {
            termHashes.add(event.getTransactionHash());
        }
        exersiceBoolQuery.should(QueryBuilders.termsQuery("transaction_hash", termHashes));

        searchSourceBuilder.query(exersiceBoolQuery);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<TransactionWithEvent> result = ServiceUtils.getSearchResult(searchResponse, TransactionWithEvent.class);
        result.setTotal(total);
        return result;
    }

    public Result<TransactionWithEvent> getByBlockHash(String network, String blockHash) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_hash", blockHash);

        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.sort("transaction_index", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return ServiceUtils.getSearchResult(searchResponse, TransactionWithEvent.class);
    }

    public Result<TransactionWithEvent> getByBlockHeight(String network, int blockHeight) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_number", blockHeight);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<TransactionWithEvent> result = ServiceUtils.getSearchResult(searchResponse, TransactionWithEvent.class);
        //get events
        List<TransactionWithEvent> transactions = result.getContents();
        List<String> txnHashes = new ArrayList<>();
        Map<String, List<Event>> txnEvents = new HashMap<>();
        for (TransactionWithEvent txn: transactions) {
            txnHashes.add(txn.getTransactionHash());
            txnEvents.put(txn.getTransactionHash(), new ArrayList<>());
        }
        if (txnHashes.size() > 0) {
            Result<Event> events = getEventsByTransaction(network, txnHashes);
            for(Event event: events.getContents()) {
                txnEvents.get(event.getTransactionHash()).add(event);
            }
            //set events
            for (TransactionWithEvent txn: transactions) {
                txn.setEvents(txnEvents.get(txn.getTransactionHash()));
            }
        }
        return result;
    }
    public Result<Event> getEventsByTransaction(String network, List<String> txnHashes) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_EVENT_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(ELASTICSEARCH_MAX_HITS);
        //begin offset
        int offset = 0;
        searchSourceBuilder.from(0);

        BoolQueryBuilder exersiceBoolQuery = QueryBuilders.boolQuery();
        exersiceBoolQuery.should(QueryBuilders.termsQuery("transaction_hash", txnHashes));
        searchSourceBuilder.query(exersiceBoolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchUnescapeResult(searchResponse, Event.class);
    }


    public Result<Event> getEvents(String network, String tag_name, int page, int count) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_EVENT_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
        }
        searchSourceBuilder.from(offset);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("tag_name.keyword", tag_name);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchUnescapeResult(searchResponse, Event.class);
    }
}
