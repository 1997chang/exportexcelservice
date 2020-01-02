package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.entity.vo.ExportExcelVo;
import com.viewshine.exportexcel.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.viewshine.exportexcel.constants.DataSourceConstants.*;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Component
public class DownloadInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DownloadInterceptor.class);

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String parameter = request.getParameter(EXCELPARAM);
        if (redisUtils.exits(EXPORT_EXCEL_REDIS_PREFIX + parameter)) {
            ExportExcelVo exportExcelVo = redisUtils.get(EXPORT_EXCEL_REDIS_PREFIX + parameter, ExportExcelVo.class);
            response.sendRedirect(DOWNLOAD_FILE_URL + "/" + exportExcelVo.getUri());
            return false;
        }
        logger.error("下载的资源文件已经删除，搜索的ID为：{}", parameter);
        return true;
    }
}
