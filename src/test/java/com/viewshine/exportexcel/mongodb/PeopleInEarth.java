package com.viewshine.exportexcel.mongodb;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author changWei[changwei@viewshine.cn]
 */
//默认的集合名称为：类名称首字母小写
@Data
public class PeopleInEarth {

    private BigInteger id;

    private String name;

    private AddressInChina address;

    //BigDecimal到MongoDB就对变成String(默认)
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal saving;

    private Double money;

    //javaType                                      MongoDBType
    //int, Integer,short,Short,Byte,byte            int32
    //long, Long                                    int32
    //float,doule                                   double        （注意：可能存在精度的问题，存储12.1111，到mongo中就可能为12.111000000000000000011），最好使用Decimal128
    //boolean                                       Boolean
    //localDateTime,localDate,localTime,date        Date           (他们存储的都是+0的时区，但是取出来的数据又是对的了)
    //BigDecimal                                    String          我们必须自己强制将设置为DECIMAL128。

}
