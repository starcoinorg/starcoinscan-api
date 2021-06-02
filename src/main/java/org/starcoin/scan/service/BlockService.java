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
import org.starcoin.scan.bean.Block;
import org.starcoin.scan.constant.Constant;

import java.io.IOException;

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
            logger.error("not found id doc");
        }
        return block;
    }

}
