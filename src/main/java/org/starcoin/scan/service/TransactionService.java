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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.base.AccountAddress;
import org.starcoin.base.VoteChangedEvent;
import org.starcoin.scan.bean.Event;
import org.starcoin.scan.bean.PendingTransaction;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.constant.Constant;
import org.starcoin.scan.utils.CommonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.starcoin.scan.service.ServiceUtils.ELASTICSEARCH_MAX_HITS;


@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private RestHighLevelClient client;

    public Transaction get(String network, String id) throws IOException {
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
        return get(network, hash);
    }

    public Result<Transaction> getRange(String network, int page, int count, int start_height, int txnType) throws IOException {
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
        return ServiceUtils.getSearchResult(searchResponse, Transaction.class);
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

    public Result<Transaction> getRangeByAddress(String network, String address, int page, int count) throws IOException {
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
        return ServiceUtils.getSearchResult(searchResponse, Transaction.class);
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

        BoolQueryBuilder exersiceBoolQuery = QueryBuilders.boolQuery();
        exersiceBoolQuery.should(QueryBuilders.termQuery("type_tag", ServiceUtils.depositEvent));
        exersiceBoolQuery.should(QueryBuilders.termQuery("type_tag", ServiceUtils.withdrawEvent));
        exersiceBoolQuery.must(QueryBuilders.termQuery("event_address", address));
        exersiceBoolQuery.must(QueryBuilders.rangeQuery("transaction_index").gt(0));


        searchSourceBuilder.query(exersiceBoolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Event> events = ServiceUtils.getSearchResult(searchResponse, Event.class);
        return events;
    }

    public Result<Transaction> getRangeByAddressAll(String network, String address, int page, int count) throws IOException {
        Result<Event> events = getEventsByAddress(network, address, page, count);
        if (events.getContents().size() == 0) {
            return Result.EmptyResult;
        }
        //logger.info("events is "+events.getContents().stream().map(e -> e.getTransactionHash()).collect(Collectors.joining(",")));
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);

        BoolQueryBuilder exersiceBoolQuery = QueryBuilders.boolQuery();
        for (Event event : events.getContents()) {
            exersiceBoolQuery.should(QueryBuilders.termQuery("transaction_hash", event.getTransactionHash()));
        }

        searchSourceBuilder.query(exersiceBoolQuery);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Transaction> result = ServiceUtils.getSearchResult(searchResponse, Transaction.class);
        result.setTotal(events.getTotal());

        return result;
    }

    public Result<Transaction> getByBlockHash(String network, String blockHash) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_hash", blockHash);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return ServiceUtils.getSearchResult(searchResponse, Transaction.class);
    }

    public Result<Transaction> getByBlockHeight(String network, int blockHeight) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("block_number", blockHeight);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return ServiceUtils.getSearchResult(searchResponse, Transaction.class);
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
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        Result<Event> result = new Result<>();
        result.setTotal(searchResponse.getHits().getTotalHits().value);
        List<Event> events = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            events.add(JSON.parseObject(hit.getSourceAsString(), Event.class));
        }
        result.setContents(events);
        return result;
    }

    public Result<Event> getEventsByProposalIdAndProposer(String network, Long proposalId, String proposer, int page, int count) throws IOException, DeserializationError {
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
        exersiceBoolQuery.must(QueryBuilders.matchQuery("tag_name", "VoteChangedEvent"));

        searchSourceBuilder.query(exersiceBoolQuery);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchSourceBuilder.sort("timestamp", SortOrder.DESC);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResultFilter(searchResponse, proposalId, proposer);
    }

    private Result<Event> getSearchResultFilter(SearchResponse searchResponse, Long proposalId, String proposerStr) throws DeserializationError {
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        Result<Event> result = new Result<>();
        result.setTotal(searchResponse.getHits().getTotalHits().value);
        List<Event> transactions = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            Event event = JSON.parseObject(hit.getSourceAsString(), Event.class);

            byte[] voteBytes = CommonUtils.hexToByteArray(event.getData());
            VoteChangedEvent data = VoteChangedEvent.bcsDeserialize(voteBytes);

            byte[] proposerBytes = CommonUtils.hexToByteArray(proposerStr);
            AccountAddress proposer = AccountAddress.bcsDeserialize(proposerBytes);
            if (data.proposal_id != proposalId || !data.proposer.equals(proposer)) {
                continue;
            }
            transactions.add(event);
        }
        result.setContents(transactions);
        return result;
    }
}
