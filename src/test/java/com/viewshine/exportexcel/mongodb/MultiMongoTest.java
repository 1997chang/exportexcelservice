package com.viewshine.exportexcel.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.viewshine.exportexcel.datasource.MongoDataSourceRouting;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.viewshine.exportexcel.constants.DataSourceConstants.*;

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

    /**
     * 用于将Query对象转化为JSON字符串之后，再将字符串抓换为Query对象
     * 必须使用BasicQuery才可以。
     */
    @Test
    public void parse() {
        MongoOperations one = mongoDataSource.getDatasources().get("one");

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("name").is("常");
        criteria.and("money").gt(1).lte(1000);
        query.addCriteria(criteria);
        Sort sort = Sort.by(Sort.Direction.DESC, "money");
        query.limit(2);
        query.skip(0);
        query.with(sort);
        List<PeopleInEarth> peopleInEarths = one.find(query, PeopleInEarth.class);
        System.out.println(JSONObject.toJSONString(peopleInEarths));

        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(query));
        BasicQuery basicQuery = new BasicQuery(Document.parse(jsonObject.getString(MONGO_QUERY)),
                Document.parse(jsonObject.getString(MONGO_FIELD)));
        basicQuery.setSortObject(Document.parse(jsonObject.getString(MONGO_SORT)));
        basicQuery.limit(jsonObject.getInteger("limit"));
        basicQuery.skip(jsonObject.getLong("skip"));
        List<PeopleInEarth> peopleInEarths1 = one.find(basicQuery, PeopleInEarth.class);
        System.out.println(JSONObject.toJSONString(peopleInEarths1));
    }

}
