package org.starcoin.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.scan.bean.Block;
import org.starcoin.scan.service.BlockService;

import java.util.List;

@RestController
@RequestMapping("/block")
public class BlockController {
    @Autowired
    private BlockService blockService;

    @GetMapping("/{network}/")
    public Block getBlock(@PathVariable("network") String network, @RequestParam String id) throws Exception {
        return blockService.getBlock(network, id);
    }

    @GetMapping("/{network}/{height}")
    public Block getBlockByHeight(@PathVariable("network") String network, @PathVariable("height") long height) throws Exception {
        return blockService.getBlockByHeight(network, height);
    }

    @GetMapping("/{network}/page/{page}")
    public List<Block> getRangeBlocks(@PathVariable("network") String network, @PathVariable("page") int page,
                                      @RequestParam(value = "count", required = false, defaultValue = "20") int count) throws Exception {
        return blockService.getRange(network, page, count);
    }
}
