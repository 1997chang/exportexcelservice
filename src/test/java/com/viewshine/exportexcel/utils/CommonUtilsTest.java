package com.viewshine.exportexcel.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public class CommonUtilsTest{

    public void operationInit(Map<String, String> values) {
        values.put("chinese", "12");
        values.put("math", "13");
        values.put("english", "0");
    }

    /**
     * 表示式的计算
     */
    @Test
    public void operationTest() {
        Map<String, String> values = new HashMap<>();
        operationInit(values);
        assertThat(CommonUtils.computeFormula("chinese + math + english", values).toPlainString())
                .isEqualTo("25");
        assertThat(CommonUtils.computeFormula("chinese - math + english", values).toPlainString())
                .isEqualTo("-1");
        assertThat(CommonUtils.computeFormula("chinese * math + english", values).toPlainString())
                .isEqualTo("156");
        assertThat(CommonUtils.computeFormula("math / english", values).toPlainString())
                .isEqualTo("0");

    }

}
