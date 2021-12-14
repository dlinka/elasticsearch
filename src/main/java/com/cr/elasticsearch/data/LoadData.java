package com.cr.elasticsearch.data;

import com.alibaba.fastjson.JSON;
import com.cr.elasticsearch.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cr.elasticsearch.constant.ESIndexConstant.GOODS_INDEX;

/**
 * 拉取京东列表页数据
 */
@Slf4j
@Component
public class LoadData {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public List<Goods> pullJDGoods(String url) {
        List<Goods> result = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements goodsListEle = document.select("#J_goodsList ul.gl-warp li.gl-item");
            for (Element goodsEle : goodsListEle) {
                Element nameEle = goodsEle.select(".gl-i-wrap .p-name em").first();
                Element imgEle = goodsEle.select(".gl-i-wrap .p-img img").first();
                Element priceEle = goodsEle.select(".gl-i-wrap .p-price i").first();

                Long goodsId = Long.parseLong(goodsEle.attr("data-sku"));
                //new Whitelist()表示去除一切标签
                String name = Jsoup.clean(nameEle.html(), new Whitelist());
                String img = "https" + imgEle.attr("data-lazy-img");
                Double price = Double.parseDouble(priceEle.text());

                Goods goods = new Goods();
                goods.setGoodsId(goodsId);
                goods.setName(name);
                goods.setImg(img);
                goods.setPrice(price);
                result.add(goods);
            }
            log.info("goods list - {}", result);
        } catch (IOException e) {
            log.error("pull jd goods error - ", e);
        }
        return result;
    }

    public void createJD() throws InterruptedException, IOException {
        //京东手机列表页
        String JD_LIST = "https://search.jd.com/Search?keyword=手机&page=%d";
        //10页
        int pageSize = 20;
        BulkRequest request = new BulkRequest();
        for (int i = 1; i <= pageSize; i++) {
            List<Goods> goodsList = pullJDGoods(String.format(JD_LIST, i));
            goodsList.forEach(goods ->{
                request.add(new IndexRequest(GOODS_INDEX).id(String.valueOf(goods.getGoodsId())).source(JSON.toJSONString(goods), XContentType.JSON));
            });
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            System.out.println(response.status());
            //每次sleep5秒
            TimeUnit.SECONDS.sleep(5);
        }
    }

}
