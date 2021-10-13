package com.cr.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.cr.elasticsearch.data.LoadData;
import com.cr.elasticsearch.entity.Goods;
import com.cr.elasticsearch.entity.User;
import com.sun.tools.javac.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.AssertTrue;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.cr.elasticsearch.constant.ESIndexConstant.GOODS_INDEX;
import static com.cr.elasticsearch.constant.ESIndexConstant.USER_INDEX;

@Slf4j
@SpringBootTest
class EsStudyApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    LoadData loadData;

    @Test
    void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(USER_INDEX);
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        Assert.check(response.isAcknowledged());
    }

    @Test
    void testExistsIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(USER_INDEX);
        boolean response = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        Assert.check(response);
    }

    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(USER_INDEX);
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        Assert.check(response.isAcknowledged());
    }

    @Test
    void testCreateDocument() throws IOException {
        User user = new User(1L, "CR", 27);
        IndexRequest request = new IndexRequest(USER_INDEX);
        request.id(String.valueOf(user.getUid()));
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        Assert.check("CREATED".equals(response.status()));
    }

    @Test
    void testExistsDocument() throws IOException {
        GetRequest request = new GetRequest(USER_INDEX, "1");
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        Assert.check(exists);
    }

    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest(USER_INDEX, "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
    }

    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest(USER_INDEX, "1");
        request.doc("name", "cr27");
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response.toString());
    }

    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest(USER_INDEX, "1");
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    void testBulkDocument() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest(USER_INDEX).id(String.valueOf(2)).source(JSON.toJSONString(new User(2L, "zj", 18)), XContentType.JSON));
        request.add(new IndexRequest(USER_INDEX).id(String.valueOf(3)).source(JSON.toJSONString(new User(3L, "zgy", 12)), XContentType.JSON));
        request.add(new IndexRequest(USER_INDEX).id(String.valueOf(4)).source(JSON.toJSONString(new User(4L, "zzm", 1)), XContentType.JSON));
        BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    @Test
    void testSearch() throws IOException {
        SearchRequest request = new SearchRequest(USER_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder query = QueryBuilders.matchQuery("name", "zj");
        searchSourceBuilder.query(query);
        request.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));
        response.getHits().forEach(hit -> {
            Map<String, Object> map = hit.getSourceAsMap();
            for (Map.Entry<String, Object> entry : map.entrySet()){
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }
        });
    }

    @Test
    void createJD() throws InterruptedException, IOException {
        //京东手机列表页
        String JD_LIST = "https://search.jd.com/Search?keyword=手机&page=%d";
        //10页
        int pageSize = 20;
        BulkRequest request = new BulkRequest();
        for (int i = 1; i <= pageSize; i++) {
            List<Goods> goodsList = loadData.pullJDGoods(String.format(JD_LIST, i));
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
