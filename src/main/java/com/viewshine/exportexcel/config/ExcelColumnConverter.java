package com.viewshine.exportexcel.config;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.ExcelColumnDTO;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * 表示转化器，用于将一个JSON字符创转化为List对象内容
 * @author ChangWei[changwei@viewshine.cn]
 */
public class ExcelColumnConverter implements Converter<String, List<ExcelColumnDTO>> {

    @Override
    public List<ExcelColumnDTO> convert(String source) {
        return JSON.parseArray(source, ExcelColumnDTO.class);
    }
}
