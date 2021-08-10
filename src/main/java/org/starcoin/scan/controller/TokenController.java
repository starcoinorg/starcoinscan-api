package org.starcoin.scan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.api.Result;
import org.starcoin.bean.Transaction;
import org.starcoin.scan.bean.TokenHolderInfo;
import org.starcoin.scan.bean.TokenStatistic;
import org.starcoin.scan.service.TokenService;

@Api(tags = "token")
@RestController
@RequestMapping("v2/token")
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @ApiOperation("get token aggregate stat list")
    @GetMapping("/{network}/token/stats/{page}")
    public Result<TokenStatistic> getAggregate(@PathVariable("network") String network, @PathVariable("page") int page,
                                               @RequestParam(value = "count", required = false, defaultValue = "20") int count) {
        return tokenService.tokenAggregateList(network, page, count);
    }

    @ApiOperation("get token holders list")
    @GetMapping("/{network}/holders/page/{page}")
    public Result<TokenHolderInfo> getHoldersByToken(@PathVariable("network") String network, @PathVariable("page") int page,
                                                     @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                     @RequestParam("token_type") String tokenType) {
        return null;
    }

    @ApiOperation("get token volume list")
    @GetMapping("/{network}/volume/page/{page}")
    public Result<Transaction> getTransferByToken(@PathVariable("network") String network, @PathVariable("page") int page,
                                                  @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                  @RequestParam("token_type") String tokenType) {
        return null;
    }

}
