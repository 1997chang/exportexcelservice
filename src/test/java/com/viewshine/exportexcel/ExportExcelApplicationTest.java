package com.viewshine.exportexcel;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;
import java.util.Optional;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ExportExcelApplicationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void selectOneDataSource() {
        DataSourceNameHolder.setActiveDataSource("two");
        System.out.println(JSON.toJSONString(jdbcTemplate.queryForList("select * from user")));
    }

    @Test
    public void selectTwoDataSource() {
        DataSourceNameHolder.setActiveDataSource("two");
        System.out.println(JSON.toJSONString(jdbcTemplate.queryForList("select * from user")));
    }

    @Test
    public void optionalTest() {
        Optional<Integer> optionalInteger = Optional.ofNullable(1);
        String s = optionalInteger.map(Objects::toString).orElse("");
        Assertions.assertThat(s).isEqualTo("1");
    }

}
