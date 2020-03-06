package com.viewshine.exportexcel.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoClients;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public class MongoDbInsertTest {

    MongoOperations mongoOperations;

    @Before
    public void before() {
        mongoOperations = new MongoTemplate(MongoClients.create(), "example");
    }

    @Test
    public void insertSingle() {
        AddressInChina addressInChina = new AddressInChina("河南", "濮阳");
        PeopleInEarth peopleInEarth = new PeopleInEarth();
        peopleInEarth.setAddress(addressInChina);
        peopleInEarth.setName("常");
        peopleInEarth.setMoney(50.25);
        System.out.println("插入前的ID：" + peopleInEarth.getId());
        mongoOperations.insert(peopleInEarth);
        System.out.println("插入后的ID：" + peopleInEarth.getId());
        //使用ID进行搜索的时候，只要传递对应的ID字段
        PeopleInEarth byId = mongoOperations.findById(peopleInEarth.getId(), PeopleInEarth.class);
        System.out.println(JSONObject.toJSONString(byId));
    }


//    @After
//    public void after() {
//        mongoOperations.dropCollection(PeopleInEarth.class);
//    }

}



