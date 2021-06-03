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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.scan.bean.Block;
import org.starcoin.scan.bean.Transaction;
import org.starcoin.scan.constant.Constant;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlockService {
    private static final Logger logger = LoggerFactory.getLogger(BlockService.class);

    @Autowired
    private RestHighLevelClient client;

    public Block getBlock(String id) throws IOException {
        GetRequest getRequest = new GetRequest(Constant.BLOCK_INDEX, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        Block block = new Block();
        if (getResponse.isExists()) {
            String sourceAsString = getResponse.getSourceAsString();
            block = JSON.parseObject(sourceAsString, Block.class);
        } else {
            logger.error("not found block by id.");
        }
        return block;
    }

    public Block getBlockByHeight(long height) throws IOException {
        SearchRequest searchRequest = new SearchRequest(Constant.BLOCK_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("header.number", height);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Block> result = getSearchResult(searchResponse);
        if (result.size() ==1) {
            return result.get(0);
        }else {
            logger.warn("get block by height is null");
        }
        return null;
    }

    public List<Block> getRange(int page , int count){
        return null;
    }
        private List<Block> getSearchResult(SearchResponse searchResponse) {
            SearchHit[] searchHit = searchResponse.getHits().getHits();
            List<Block> blocks = new ArrayList<>();
            for (SearchHit hit : searchHit) {
                blocks.add(JSON.parseObject(hit.getSourceAsString(), Block.class));
            }
            return blocks;
        }
}
