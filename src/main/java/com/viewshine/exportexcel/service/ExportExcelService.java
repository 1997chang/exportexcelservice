package com.viewshine.exportexcel.service;

import com.viewshine.exportexcel.entity.RequestExcelDTO;
import com.viewshine.exportexcel.entity.vo.ExportExcelVo;
import com.viewshine.exportexcel.entity.vo.QueryExcelVo;
import com.viewshine.exportexcel.entity.vo.ResultVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public interface ExportExcelService {

    /**
     * 在指定的dataBase数据源下执行执行的SQL语句，并保存到本地磁盘文件fileName中
     * @param requestExcelDTO 表示执行的SQL语句，选择的数据源，以及各个导出列的属性内容
     * @param httpServletRequest 表示当前请求
     */
    ResultVO<ExportExcelVo> exportExcelToDisk(RequestExcelDTO requestExcelDTO, HttpServletRequest httpServletRequest);

    /**
     * 表示根据UUID获取对应文件的基本信息
     * @param uuid excelId
     * @return 基本信息
     */
    ResultVO<ExportExcelVo> queryByExcelId(String uuid);

    /**
     * 根据Excel的UUID获取对应文件的下载状态
     * @param excelId Excel的唯一标识
     * @return 下载状态
     */
    ResultVO<QueryExcelVo> queryStatusByExcelId(String excelId);
}
