package com.viewshine.exportexcel.mongodb;

import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author changWei[changwei@viewshine.cn]
 * 这个表示Mongodb但数据源的测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoDBSingleTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void notNull() {
        assertThat(mongoTemplate).isNotNull();
        assertThat(mongoTemplate.getDb().getName()).isEqualTo("example");

    }

    @Test
    public void count() {
        Query query = new Query();
        query.fields().exclude("_id");
        query.addCriteria(where("userName").is("chang"));
        //使用具体类
        List<User> userList = mongoTemplate.find(query, User.class, "test");
        assertThat(userList.size()).isEqualTo(1);
        assertThat(userList.get(0).getUserName()).isEqualTo("chang");

        //使用JSON字符串的形式返回
        List<String> stringList = mongoTemplate.find(query, String.class, "test");
        System.out.println(stringList);

        //使用MAP，会将每一个属性设置到MAP中的Key中
        List<Map> test = mongoTemplate.find(query, Map.class, "test");
        assertThat(test).isNotNull();
        assertThat(test.size()).isEqualTo(1);
        assertThat(test.get(0).containsKey("address")).isTrue();
        assertThat(test.get(0).get("address")).isEqualTo("河南濮阳");


        //使用JSONObject
        List<JSONObject> jsonObjectList = mongoTemplate.find(query, JSONObject.class, "test");
        assertThat(jsonObjectList.size()).isEqualTo(1);
        assertThat(jsonObjectList.get(0).containsKey("userName")).isTrue();
        assertThat(jsonObjectList.get(0).getString("age")).isEqualTo("12");
        System.out.println(jsonObjectList.get(0).toJSONString());

    }

}

//可以使用@Document来设置收集器的名称
//@Document("users")
@Data
@Builder
class User {
    private String userName;

    private String address;

    private Integer age;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
