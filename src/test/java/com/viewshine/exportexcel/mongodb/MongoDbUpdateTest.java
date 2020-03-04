package com.viewshine.exportexcel.mongodb;

import com.mongodb.client.MongoClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public class MongoDbUpdateTest {

    MongoOperations mongoOperations;

    @Before
    public void before() {
        mongoOperations = new MongoTemplate(MongoClients.create(), "example");
    }

    //修改之后，我们必须重新获取，才能得到最新的结果
    @Test
    public void insertSingle() {
        AddressInChina addressInChina = new AddressInChina("河南", "濮阳");
        PeopleInEarth peopleInEarth = new PeopleInEarth();
        peopleInEarth.setAddress(addressInChina);
        peopleInEarth.setName("常");
        peopleInEarth.setMoney(50.25);
        peopleInEarth.setSaving(BigDecimal.valueOf(50.25));
        mongoOperations.insert(peopleInEarth);
        System.out.println("修改前的金额：" + peopleInEarth.getMoney());

        mongoOperations.updateFirst(query(where("name").is("常")), new Update().inc("money", new Double(50.222)).inc(
                "saving"
                , BigDecimal.valueOf(50.222)),
                PeopleInEarth.class);

        System.out.println(peopleInEarth.getMoney());
        PeopleInEarth byId = mongoOperations.findById(peopleInEarth.getId(), PeopleInEarth.class);
        System.out.println(byId.getMoney());
        System.out.println(byId.getSaving());
    }


    @After
    public void after() {
        mongoOperations.dropCollection(PeopleInEarth.class);
    }

}
