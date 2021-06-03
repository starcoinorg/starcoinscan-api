package org.starcoin.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.starcoin.scan.bean.Block;
import org.starcoin.scan.service.BlockService;

import java.util.List;

@RestController
@RequestMapping("/block")
public class BlockController {
    @Autowired
    private BlockService blockService;

    @GetMapping("/get_block")
    public Block getBlock(@RequestParam String id) throws Exception {
        return blockService.getBlock(id);
    }

    @GetMapping("/get_block_by_height")
    public Block getBlockByHeight(@RequestParam long height) throws Exception {
        return blockService.getBlockByHeight(height);
    }

    @GetMapping("/get_range_block")
    public List<Block> getRangeBlocks(@RequestParam int page, @RequestParam int count) throws Exception {
        return blockService.getRange(page,count);
    }
}
