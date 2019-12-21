package com.viewshine.exportexcel;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
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
        DataSourceNameHolder.setActiveDataSource("one");
//        System.out.println(JSON.toJSONString(jdbcTemplate.queryForList("select * from users")));
        jdbcTemplate.query("select id, username as userName, password pass from users", (rs, rowCount) -> {
            List<String> itemData = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("getCatalogName：" + metaData.getCatalogName(i));
                System.out.println("getColumnClassName：" + metaData.getColumnClassName(i));
                System.out.println("getColumnLabel：" + metaData.getColumnLabel(i));
                System.out.println("getColumnName：" + metaData.getColumnName(i));
                System.out.println("getColumnDisplaySize：" + metaData.getColumnDisplaySize(i));
                System.out.println("getColumnTypeName：" + metaData.getColumnTypeName(i));
                System.out.println("getColumnType：" + metaData.getColumnType(i));
                System.out.println("getSchemaName：" + metaData.getSchemaName(i));
                System.out.println("getTableName：" + metaData.getTableName(i));
                System.out.println("getPrecision：" + metaData.getPrecision(i));
                System.out.println("getScale：" + metaData.getScale(i));
                try {
                    System.out.println(JdbcUtils.getResultSetValue(rs, i, Class.forName(metaData.getColumnClassName(i))));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return itemData;
        }, null);
    }

    @Test
    public void selectTwoDataSource() {
        DataSourceNameHolder.setActiveDataSource("one");
        System.out.println(JSON.toJSONString(jdbcTemplate.queryForList("select * from countrylanguage")));
    }

    @Test
    public void optionalTest() {
        Optional<Integer> optionalInteger = Optional.ofNullable(1);
        String s = optionalInteger.map(Objects::toString).orElse("");
        Assertions.assertThat(s).isEqualTo("1");
    }

}
