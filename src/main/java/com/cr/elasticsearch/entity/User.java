package com.cr.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import static com.cr.elasticsearch.constant.ESIndexConstant.USER_INDEX;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = USER_INDEX)
public class User {

    @Id
    private Long uid;
    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
    private String name;

}
