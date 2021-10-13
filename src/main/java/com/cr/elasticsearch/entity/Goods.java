package com.cr.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    private Long goodsId;
    private String name;
    private String img;
    private Double price;

}

