package org.starcoin.scan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.scan.bean.Block;
import org.starcoin.scan.bean.UncleBlock;
import org.starcoin.scan.service.BlockService;
import org.starcoin.scan.service.Result;

import java.util.List;

@RestController
@RequestMapping("v1/block")
public class BlockController {
    @Autowired
    private BlockService blockService;

    @GetMapping("/{network}/")
    public Block getBlock(@PathVariable("network") String network, @RequestParam String id) throws Exception {
        return blockService.getBlock(network, id);
    }

    @GetMapping("/{network}/hash/{hash}")
    public Block getBlockByHash(@PathVariable("network") String network, @PathVariable("hash") String hash) throws Exception {
        return blockService.getBlockByHash(network, hash);
    }

    @GetMapping("/{network}/height/{height}")
    public Block getBlockByHeight(@PathVariable("network") String network, @PathVariable("height") long height) throws Exception {
        return blockService.getBlockByHeight(network, height);
    }

    @GetMapping("/{network}/page/{page}")
    public Result<Block> getRangeBlocks(@PathVariable("network") String network, @PathVariable("page") int page,
                                        @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                        @RequestParam(value = "total", required = false, defaultValue = "0") int start_height) throws Exception {
        return blockService.getRange(network, page, count, start_height);
    }

    @GetMapping("/{network}/uncle/page/{page}")
    public Result<UncleBlock> getRangeUncleBlocks(@PathVariable("network") String network, @PathVariable("page") int page,
                                                  @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                  @RequestParam(value = "total", required = false, defaultValue = "0") int start_height) throws Exception {
        return blockService.getUnclesRange(network, page, count, start_height);
    }

    @GetMapping("/{network}/uncle/height/{height}")
    public UncleBlock getUncleBlockByHeight(@PathVariable("network") String network, @PathVariable("height") long height) throws Exception {
        return blockService.getUncleBlockByHeight(network, height);
    }
}
