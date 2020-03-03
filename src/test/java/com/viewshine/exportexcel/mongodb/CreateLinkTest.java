package com.viewshine.exportexcel.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;
import static org.springframework.data.domain.ExampleMatcher.matching;
import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public class CreateLinkTest {

    /**
     * 手动创建MongoDB的连接，从而完成查询数据内容
     * 主要使用Client对象
     */
    MongoOperations mongoOps;

    User skyler, walter, flynn, marie, hank;


    @Before
    public void before() {
        //默认：以无密码的形式连接本地的MongoDB数据（mongodb://localhost:27017），如果没有数据源，则自己添加
        mongoOps = new MongoTemplate(MongoClients.create(), "example1");
        this.skyler = new User("Skyler", "White", 45);
        this.walter = new User("Walter", "White", 50);
        this.flynn = new User("Walter Jr. (Flynn)", "White", 17);
        this.marie = new User("Marie", "Schrader", 38);
        this.hank = new User("Hank", "Schrader", 43);
        mongoOps.insert(skyler);
        mongoOps.insert(walter);
        mongoOps.insert(flynn);
        mongoOps.insert(marie);
        mongoOps.insert(hank);
    }

    @Test
    public void ignoreNullProperties() {
        Query query = query(byExample(new User(null, null, 17)));
        assertThat(mongoOps.find(query, User.class)).contains(flynn);
    }

    @Test
    public void subStringMatching() {
        Example<User> userExample = Example.of(new User("er", null, null), matching().
                withStringMatcher(ExampleMatcher.StringMatcher.ENDING));
        assertThat(mongoOps.find(query(byExample(userExample)), User.class)).contains(skyler, walter);
    }

    @Test
    public void regexMatching() {
        Example<User> userExample = Example.of(new User("(Skyl|Walt)er", null, null), matching().
                withMatcher("userName", ExampleMatcher.GenericPropertyMatcher::regex));
        assertThat(mongoOps.find(query(byExample(userExample)), User.class)).contains(skyler, walter);
    }

    @Test
    public void matchStartingStringsIgnoreCase() {
        Example<User> userExample = Example.of(new User("Walter", "WHITE", null), matching().
                withMatcher("userName", startsWith()).
                withIgnorePaths("age").
                withMatcher("address", ExampleMatcher.GenericPropertyMatchers.ignoreCase()));

        assertThat(mongoOps.find(query(byExample(userExample)), User.class)).contains(flynn, walter);
    }

    @Test
    public void configuringMatchersUsingLambdas() {
        Example<User> userExample = Example.of(new User("Walter", "WHITE", null), matching().
                withIgnorePaths("age").
                withMatcher("userName", matcher -> matcher.startsWith()).
                withMatcher("address", matcher -> matcher.ignoreCase()));
        assertThat(mongoOps.find(query(byExample(userExample)), User.class)).contains(flynn, walter);
    }

    @Test
    public void valueTransformer() {
        Example<User> userExample = Example.of(new User(null, "White", 99), matching().
                withMatcher("age", matcher -> matcher.transform(value -> Optional.of(50))));
        Query query = query(byExample(userExample));
        assertThat(mongoOps.find(query, User.class)).contains(walter);
        String queryJson = JSONObject.toJSONString(query);
        System.out.println(queryJson);
        Query query1 = JSONObject.parseObject(queryJson, Query.class);
        assertThat(mongoOps.find(query1, User.class)).contains(walter);
    }


    @After
    public void after() {
        mongoOps.dropCollection("user");
    }

}
