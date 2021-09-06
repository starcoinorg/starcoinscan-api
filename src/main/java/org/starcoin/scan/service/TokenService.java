package org.starcoin.scan.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.pipeline.BucketSortPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.api.Result;
import org.starcoin.scan.bean.TokenHolderInfo;
import org.starcoin.scan.bean.TokenStatistic;
import org.starcoin.scan.constant.Constant;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.starcoin.scan.service.ServiceUtils.ELASTICSEARCH_MAX_HITS;

@Service
public class TokenService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private static final String STC_TYPE_TAG = "0x00000000000000000000000000000001::STC::STC";

    @Autowired
    private RestHighLevelClient client;

    public Result<TokenStatistic> tokenAggregateList(String network, int page, int count) {
        //get token holder
        Result<TokenStatistic> holders = tokenHolderList(network, page, count);
        List<TokenStatistic> holderContents = holders.getContents();
        if (holderContents.isEmpty()) {
            return holders;
        }
        //get volume
        Result<TokenStatistic> volumes = tokenVolumeList(network, page, count);
        Map<String, Long> volumeMap = getVolumeMap(volumes);
        //get market cap
        Result<TokenStatistic> market = tokenMarketCap(network, page, count);
        Map<String, Double> marketMap = getMarketMap(market);
        for (TokenStatistic tokenStatistic : holderContents) {
            String typeTag = tokenStatistic.getTypeTag();
            Long volume = volumeMap.get(typeTag);
            if (volume != null) {
                tokenStatistic.setVolume(volume);
            }
            Double marketCap = marketMap.get(typeTag);
            if (marketCap != null) {
                tokenStatistic.setMarketCap(marketCap);
            }
        }
        return holders;
    }

    private Map<String, Double> getMarketMap(Result<TokenStatistic> markets) {
        Map<String, Double> marketMap = new HashMap<>();
        List<TokenStatistic> volumeContents = markets.getContents();
        if (volumeContents.isEmpty()) {
            return marketMap;
        }
        for (TokenStatistic statistic : volumeContents) {
            marketMap.put(statistic.getTypeTag(), statistic.getMarketCap());
        }
        return marketMap;
    }

    private Map<String, Long> getVolumeMap(Result<TokenStatistic> volumes) {
        Map<String, Long> volumeMap = new HashMap<>();
        List<TokenStatistic> volumeContents = volumes.getContents();
        if (volumeContents.isEmpty()) {
            return volumeMap;
        }
        String typeTag;
        for (TokenStatistic statistic : volumeContents) {
            typeTag = statistic.getTypeTag();
            if(typeTag.equals(STC_TYPE_TAG)) {
                volumeMap.put(typeTag, statistic.getVolume() / 1000000000);
            }else {
                volumeMap.put(statistic.getTypeTag(), statistic.getVolume());
            }
        }
        return volumeMap;
    }

    public Result<TokenStatistic> tokenInfoAggregate(String network, String token) {
        if (token == null || token.length() == 0) {
            return null;
        }
        Result<TokenStatistic> result = new Result<>();
        //get volume info
        SearchRequest searchRequest = new SearchRequest(getIndex(network, Constant.TRANSFER_JOURNAL_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .must(QueryBuilders.rangeQuery("amount").gt(0))
                .must(QueryBuilders.termQuery("type_tag.keyword", token));
        searchSourceBuilder.query(queryBuilder);
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("token_stat")
                .field("type_tag.keyword")
                .order(BucketOrder.aggregation("amounts", false))
                .subAggregation(AggregationBuilders.dateRange("date_range").field("timestamp").addRange("now/d-1d", "now/d"))
                .subAggregation(AggregationBuilders.sum("amounts").field("amount"));

        searchSourceBuilder.from(0);
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.timeout(new TimeValue(20, TimeUnit.SECONDS));

        try {
            result = searchStatistic(client.search(searchRequest, RequestOptions.DEFAULT), StatisticType.Volumes);
        } catch (IOException e) {
            logger.error("get token volume error:", e);
        }
        // get market cap
        Result<TokenStatistic> result2 = new Result<>();
        searchRequest = new SearchRequest(getIndex(network, Constant.MARKET_CAP_INDEX));
        searchSourceBuilder = new SearchSourceBuilder();
        queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .must(QueryBuilders.termsQuery("type_tag.keyword", token));
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.timeout(new TimeValue(20, TimeUnit.SECONDS));

        try {
            result2 = ServiceUtils.getSearchResult(client.search(searchRequest, RequestOptions.DEFAULT), TokenStatistic.class);
        } catch (IOException e) {
            logger.error("get token market cap error:", e);
        }
        // get holder
        TokenStatistic tokenStatistic3 = new TokenStatistic();
        searchRequest = new SearchRequest(getIndex(network, Constant.ADDRESS_INDEX));
        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("type_tag.keyword", token));
        searchSourceBuilder.aggregation(AggregationBuilders.count("address_holders").field("address.keyword"));
        searchSourceBuilder.timeout(new TimeValue(10, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            tokenStatistic3.setAddressHolder(searchResponse.getHits().getTotalHits().value);
        } catch (IOException e) {
            logger.error("get token holder error:", e);
        }
        //aggregate result
        TokenStatistic tokenStatistic1 =  result.getContents().get(0);
        if (!result2.getContents().isEmpty()) {
            TokenStatistic tokenStatistic2 = result2.getContents().get(0);
            tokenStatistic1.setMarketCap(tokenStatistic2.getMarketCap());
        }
        tokenStatistic1.setAddressHolder(tokenStatistic3.getAddressHolder());
        if(STC_TYPE_TAG.equals(tokenStatistic1.getTypeTag())) {
            tokenStatistic1.setVolume(tokenStatistic1.getVolume()/1000000000);
        }
        result.getContents().set(0, tokenStatistic1);
        return result;
    }

    public Result<TokenStatistic> tokenHolderList(String network, int page, int count) {
        SearchRequest searchRequest = new SearchRequest(getIndex(network, Constant.ADDRESS_INDEX));
        int offset = (page - 1) * count;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("holders")
                .field("type_tag.keyword")
                .order(BucketOrder.aggregation("address_holders", false))
//                .size(count)
                .subAggregation(AggregationBuilders.count("address_holders").field("address.keyword"))
                .subAggregation(new BucketSortPipelineAggregationBuilder("bucket_field",null).from(offset).size(count));

        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.timeout(new TimeValue(20, TimeUnit.SECONDS));

        try {
            return searchStatistic(client.search(searchRequest, RequestOptions.DEFAULT), StatisticType.AddressHolder);
        } catch (IOException e) {
            logger.error("get token stat error:", e);
            return null;
        }

    }

    public Result<TokenStatistic> tokenMarketCap(String network, int page, int count) {
        SearchRequest searchRequest = new SearchRequest(getIndex(network, Constant.MARKET_CAP_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .must(QueryBuilders.matchAllQuery());
        searchSourceBuilder.query(queryBuilder);
        //page size
        int offset = 0;
        searchSourceBuilder.size(count);
        if (page > 1) {
            offset = (page - 1) * count;
            if (offset >= ELASTICSEARCH_MAX_HITS) {
                searchSourceBuilder.searchAfter(new Object[]{offset});
            }
        }
        //begin offset
        searchSourceBuilder.from(offset);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.timeout(new TimeValue(20, TimeUnit.SECONDS));

        try {
            return ServiceUtils.getSearchResult(client.search(searchRequest, RequestOptions.DEFAULT), TokenStatistic.class);
        } catch (IOException e) {
            logger.error("get token market cap error:", e);
            return null;
        }
    }

    public Result<TokenStatistic> tokenVolumeList(String network, int page, int count) {
        SearchRequest searchRequest = new SearchRequest(getIndex(network, Constant.TRANSFER_JOURNAL_INDEX));
        int offset = (page - 1) * count;
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .must(QueryBuilders.rangeQuery("amount").gt(0));
        searchSourceBuilder.query(queryBuilder);
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("token_stat")
                .field("type_tag.keyword")
                .order(BucketOrder.aggregation("amounts", false))
                .subAggregation(AggregationBuilders.dateRange("date_range").field("timestamp").addRange("now/d-1d", "now/d"))
                .subAggregation(AggregationBuilders.sum("amounts").field("amount"))
                .subAggregation(new BucketSortPipelineAggregationBuilder("bucket_field",null).from(offset).size(count));;

        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.trackTotalHits(true);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.timeout(new TimeValue(20, TimeUnit.SECONDS));

        try {
            return searchStatistic(client.search(searchRequest, RequestOptions.DEFAULT), StatisticType.Volumes);
        } catch (IOException e) {
            logger.error("get token volume error:", e);
            return null;
        }

    }

    private Result<TokenStatistic> searchStatistic(SearchResponse searchResponse, StatisticType statisticType) {
        List<Aggregation> aggregationList = searchResponse.getAggregations().asList();
        if (aggregationList.isEmpty()) {
            return Result.EmptyResult;
        }
        Result<TokenStatistic> result = new Result<>();
        List<TokenStatistic> statistics = new ArrayList<>();
        for (Aggregation agg : aggregationList) {
            List<? extends Terms.Bucket> backets = ((Terms) agg).getBuckets();
            for (Terms.Bucket elasticBucket : backets) {
                TokenStatistic statistic = new TokenStatistic();
                statistic.setTypeTag(elasticBucket.getKeyAsString());
                if (statisticType == StatisticType.AddressHolder) {
                    statistic.setAddressHolder(elasticBucket.getDocCount());
                } else if (statisticType == StatisticType.Volumes) {
                    Aggregation amountAgg = ((ParsedStringTerms.ParsedBucket) elasticBucket).getAggregations().get("amounts");
                    if (amountAgg instanceof ParsedSum) {
                        Double value = ((ParsedSum) amountAgg).getValue();
                        statistic.setVolume(value.longValue());
                    }
                }
                statistics.add(statistic);
            }
        }
        result.setContents(statistics);
        return result;
    }

    public Result<TokenHolderInfo> getHoldersByToken(String network, int page, int count, String tokenType) throws IOException {
        SearchRequest searchRequest = new SearchRequest(getIndex(network, Constant.ADDRESS_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(count);
        //begin offset
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * count;
        }
        searchSourceBuilder.from(offset);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("type_tag.keyword", tokenType);

        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.sort("amount", SortOrder.DESC);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Result<TokenHolderInfo> result = ServiceUtils.getSearchResult(searchResponse, TokenHolderInfo.class);

        Result<TokenStatistic> tokenStatisticResult = this.tokenMarketCap(network, tokenType);
        if (tokenStatisticResult.getContents() != null && tokenStatisticResult.getContents().size() > 0) {
            TokenStatistic tokenStatistic = tokenStatisticResult.getContents().get(0);
            BigInteger totalSupply = BigInteger.valueOf((new Double(tokenStatistic.getMarketCap())).longValue());
            for (TokenHolderInfo info : result.getContents()) {
                info.setSupply(totalSupply);
            }
        }
        return result;
    }

    public Result<TokenStatistic> tokenMarketCap(String network, String tokenType) {
        SearchRequest searchRequest = new SearchRequest(getIndex(network, Constant.MARKET_CAP_INDEX));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder
                .must(QueryBuilders.matchAllQuery());
        searchSourceBuilder.query(queryBuilder);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("type_tag.keyword", tokenType);

        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        searchSourceBuilder.trackTotalHits(true);

        try {
            return ServiceUtils.getSearchResult(client.search(searchRequest, RequestOptions.DEFAULT), TokenStatistic.class);
        } catch (IOException e) {
            logger.error("get token market cap error:", e);
            return null;
        }
    }

    enum StatisticType {
        AddressHolder,
        Volumes,
        MarketCap,
    }
}
