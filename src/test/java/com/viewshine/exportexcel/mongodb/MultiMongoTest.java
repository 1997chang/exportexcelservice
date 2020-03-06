package com.viewshine.exportexcel.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.viewshine.exportexcel.datasource.MongoDataSourceRouting;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Profile("mongosource")
public class MultiMongoTest {

    @Autowired
    private MongoDataSourceRouting mongoDataSource;

    @Test
    public void test() {
        MongoOperations one = mongoDataSource.getDatasources().get("one");
        Assertions.assertThat(one).isNotNull();
        List<PeopleInEarth> all = one.findAll(PeopleInEarth.class);
        System.out.println(JSONObject.toJSONString(all));
        MongoOperations two = mongoDataSource.getDatasources().get("two");
        Query query = new Query();
        query.limit(100);
        List<JSONObject> od_up_gas_rtu = two.find(query, JSONObject.class, "od_up_gas_rtu");
        System.out.println(JSONObject.toJSONString(od_up_gas_rtu));
    }

}
