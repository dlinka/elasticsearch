package com.cr.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.cr.elasticsearch.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cr.elasticsearch.constant.ESIndexConstant.GOODS_INDEX;
import static com.cr.elasticsearch.constant.ESIndexConstant.USER_INDEX;

@Slf4j
@Service
public class SearchService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    public List<Goods> search(String keyword, Integer page) {
        List<Goods> result = new ArrayList<>();

        SearchRequest request = new SearchRequest(GOODS_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("name", keyword));
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");
        sourceBuilder.highlighter(highlightBuilder);

        request.source(sourceBuilder);

        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("search error - ", e);
        }
        log.info("search result - {}", response.toString());

        response.getHits().forEach(hit -> {
            Goods goods = new Goods();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            Map<String, Object> map = hit.getSourceAsMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key.equals("name")) {
                    goods.setName(highlightFields.get("name").fragments()[0].string());
                }else if(key.equals("goodsId")){
                    goods.setGoodsId((Long) entry.getValue());
                }else if(key.equals("price")){
                    goods.setPrice((Double) entry.getValue());
                }else if(key.equals("img")){
                    goods.setImg((String) entry.getValue());
                }
            }
            result.add(goods);
        });
        return result;
    }

}
