package org.starcoin.scan.bean;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void getHeader() {
    }

    @Test
    void setHeader() {
    }

    @Test
    void propertyTest() {
        Block block = new Block();
        BlockHeader header = new BlockHeader();
        header.setBlockHash("0xaa4a4f35742cd8b5f4b5bc2bd81f0f44def676e251a64e3bdb2ce8b78352230d");
        header.setAuthor("0x928b3032a3d78914b4e920627b93c62a");
        block.setHeader(header);
        assertEquals("{\"header\":{\"author\":\"0x928b3032a3d78914b4e920627b93c62a\",\"block_hash\":\"0xaa4a4f35742cd8b5f4b5bc2bd81f0f44def676e251a64e3bdb2ce8b78352230d\",\"chain_id\":0,\"difficulty_number\":0,\"gas_used\":0,\"nonce\":0,\"number\":0}}", JSON.toJSONString(block));
    }
}