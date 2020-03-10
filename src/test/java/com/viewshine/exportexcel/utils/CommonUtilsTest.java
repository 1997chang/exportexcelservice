package com.viewshine.exportexcel.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public class CommonUtilsTest{

    public void operationInit(Map<String, Object> values) {
        values.put("chinese", "12");
        values.put("math", "13");
        values.put("english", "2");
    }

    /**
     * 表示式的计算
     */
    @Test
    public void operationTest() {
        Map<String, Object> values = new HashMap<>();
        operationInit(values);
        assertThat(CommonUtils.computeFormula("((chinese + math) + english) + 1", values).toPlainString())
                .isEqualTo("28");
        assertThat(CommonUtils.computeFormula("(chinese - (math + english)) * (chinese / english)", values).toPlainString())
                .isEqualTo("-18");
        assertThat(CommonUtils.computeFormula("chinese * ( math * ( english * (english + math) + (math + english) + math ) + chinese )", values).toPlainString())
                .isEqualTo("9192");
        assertThat(CommonUtils.computeFormula("chinese / english", values).toPlainString())
                .isEqualTo("6");

    }

    @Test
    public void test() {
        Map<String, String> collect = Stream.<String>empty().collect(Collectors.toMap(s -> s, s -> s));
        Assertions.assertThat(collect).isNotNull();
    }

}
