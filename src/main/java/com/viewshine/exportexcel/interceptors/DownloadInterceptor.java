package com.viewshine.exportexcel.interceptors;

import com.viewshine.exportexcel.entity.vo.ExportExcelVo;
import com.viewshine.exportexcel.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.viewshine.exportexcel.constants.DataSourceConstants.*;

/**
 * 下载文件请求的拦截器。
 *      如果请求是/downloadExcel/开头的请求，就会进行请求重定向，重定向到/download，从而从虚拟目录中下载文件
 * @author changWei[changwei@viewshine.cn]
 */
@Component
public class DownloadInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DownloadInterceptor.class);

    @Autowired
    private RedisUtils redisUtils;

    @Value("${export.excel.docFilePath}")
    private String docFilePath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String parameter = request.getParameter(EXCELPARAM);
        logger.info("接收到下载文件请求，准备下载ExcelID：[{}]的文件", parameter);
        if (redisUtils.exits(EXPORT_EXCEL_REDIS_PREFIX + parameter)) {
            ExportExcelVo exportExcelVo = redisUtils.get(EXPORT_EXCEL_REDIS_PREFIX + parameter, ExportExcelVo.class);
            logger.info("下载ExcelID：[{}]的Excel文件，文件所在的目录：[{}/{}]",
                    parameter, docFilePath , exportExcelVo.getUri());
            response.sendRedirect(DOWNLOAD_FILE_URL + "/" + exportExcelVo.getUri());
            return false;
        }
        logger.error("下载的Excel文件已经删除，搜索的ExcelID为：{}", parameter);
        return true;
    }
}
