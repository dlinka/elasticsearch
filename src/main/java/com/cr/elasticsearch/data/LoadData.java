package com.cr.elasticsearch.data;

import com.cr.elasticsearch.entity.Goods;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LoadData {

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

}
