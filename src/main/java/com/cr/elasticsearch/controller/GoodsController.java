package com.cr.elasticsearch.controller;

import com.cr.elasticsearch.entity.Goods;
import com.cr.elasticsearch.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/goods/{page}/{keyword}")
    public List<Goods> search(@PathVariable("page") int page, @PathVariable("keyword") String keyword){
        return goodsService.search(keyword, page);
    }

}
