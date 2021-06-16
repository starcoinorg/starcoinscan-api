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
import org.starcoin.scan.bean.Block;
import org.starcoin.scan.bean.UncleBlock;
import org.starcoin.scan.constant.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.starcoin.scan.service.ServiceUtils.ELASTICSEARCH_MAX_HITS;

@Service
public class BlockService {
    private static final Logger logger = LoggerFactory.getLogger(BlockService.class);

    @Autowired
    private RestHighLevelClient client;

    public Block getBlock(String network, String id) throws IOException {
        GetRequest getRequest = new GetRequest(ServiceUtils.getIndex(network, Constant.BLOCK_INDEX), id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Block block = new Block();
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            block = JSON.parseObject(sourceAsString, Block.class);
        } else {
            logger.error("not found block by id: {}", id);
        }
        return block;
    }

    public Block getBlockByHash(String network, String hash) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.BLOCK_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("header.block_hash", hash);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Block> result = getSearchResult(searchResponse, Block.class);
        List<Block> blocks = result.getContents();
        if (blocks.size() == 1) {
            return blocks.get(0);
        } else {
            logger.warn("get block by hash is null, network: {}, : {}", network,hash);
        }
        return null;
    }

    public Block getBlockByHeight(String network, long height) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.BLOCK_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("metadata.number", height);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<Block> result = getSearchResult(searchResponse, Block.class);
        List<Block> blocks = result.getContents();
        if (blocks.size() == 1) {
            return blocks.get(0);
        } else {
            logger.warn("get block by height is null, network: {}, : {}", network, height);
        }
        return null;
    }

    public Result<Block> getRange(String network, int page, int count, int start_height) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.BLOCK_INDEX));
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
            }else {
                searchSourceBuilder.from(offset);
            }
        }
        searchSourceBuilder.sort("header.number", SortOrder.DESC);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse, Block.class);
    }

    public UncleBlock getUncleBlockByHeight(String network, long height) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.UNCLE_BLOCK_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("header.number", height);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<UncleBlock> result = getSearchResult(searchResponse, UncleBlock.class);
        List<UncleBlock> blocks = result.getContents();
        if (blocks.size() == 1) {
            return blocks.get(0);
        } else {
            logger.warn("get uncle block by height is null, network: {}, : {}", network, height);
        }
        return null;
    }

    public Result<UncleBlock> getUnclesRange(String network, int page, int count, int start_height) throws IOException {
        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.UNCLE_BLOCK_INDEX));
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
            }else {
                searchSourceBuilder.from(offset);
            }
        }
        searchSourceBuilder.sort("parent_block_number", SortOrder.DESC);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return getSearchResult(searchResponse, UncleBlock.class);
    }

    private <T> Result<T> getSearchResult(SearchResponse searchResponse, Class<T> object) {
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        Result<T> result = new Result<>();
        result.setTotal(searchResponse.getHits().getTotalHits().value);
        List<T> blocks = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            blocks.add(JSON.parseObject(hit.getSourceAsString(), object));
        }
        result.setContents(blocks);
        return result;
    }


}
