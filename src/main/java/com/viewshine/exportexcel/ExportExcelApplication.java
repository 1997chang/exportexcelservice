package com.viewshine.exportexcel;

import com.viewshine.exportexcel.config.MultiDataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@Import(MultiDataSourceAutoConfiguration.class)
public class ExportExcelApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExportExcelApplication.class, args);
    }
}
