package org.starcoin.scan.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.starcoin.api.Result;
import org.starcoin.scan.bean.TokenHolderInfo;
import org.starcoin.scan.bean.TokenStatistic;
import org.starcoin.scan.service.TokenService;

import java.io.IOException;

@Api(tags = "token")
@RestController
@RequestMapping("v2/token")
public class TokenController {
    @Autowired
    private TokenService tokenService;

    @ApiOperation("get token aggregate stat list")
    @GetMapping("/{network}/stats/{page}")
    public Result<TokenStatistic> getAggregate(@PathVariable("network") String network, @PathVariable("page") int page,
                                               @RequestParam(value = "count", required = false, defaultValue = "20") int count) {
        return tokenService.tokenAggregateList(network, page, count);
    }

    @ApiOperation("get token aggregate info")
    @GetMapping("/{network}/info/{token}")
    public Result<TokenStatistic> tokenInfoAggregate(@PathVariable("network") String network, @PathVariable(value = "token", required = true) String token) {
        return tokenService.tokenInfoAggregate(network, token);
    }

    @ApiOperation("get token holders list")
    @GetMapping("/{network}/holders/page/{page}")
    public Result<TokenHolderInfo> getHoldersByToken(@PathVariable("network") String network, @PathVariable("page") int page,
                                                     @RequestParam(value = "count", required = false, defaultValue = "20") int count,
                                                     @RequestParam("token_type") String tokenType) throws IOException {
        return tokenService.getHoldersByToken(network, page, count, tokenType);
    }

}
