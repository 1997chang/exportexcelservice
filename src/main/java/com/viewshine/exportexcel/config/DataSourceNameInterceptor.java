package com.viewshine.exportexcel.config;

import com.viewshine.exportexcel.properties.DataSourceNameHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Objects;
import java.util.Optional;

import static com.viewshine.exportexcel.constants.DataSourceConstants.HTTP_DATASOURCE_NAME;

/**
 * 这个表示对外部Web请求进行拦截，用于设置要选择的数据源名称
 * @author ChangWei[changwei@viewshine.cn]
 */
@Order(-1)
@Component
public class DataSourceNameInterceptor extends HandlerInterceptorAdapter {

    private final static Logger logger = LoggerFactory.getLogger(DataSourceNameInterceptor.class);

    /**
     * 对外部的请求进行拦截，用于将选择的数据库的名称进行设置
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("请求地址为：【{}】，准备导出或下载Excel数据内容", request.getRequestURL());
        String selectDataSourceName = request.getHeader(HTTP_DATASOURCE_NAME);
        logger.info("首先从请求Head中获取要选择的数据源信息，参数名：{}，传递的参数为：[{}]",
                HTTP_DATASOURCE_NAME, selectDataSourceName);
        if (StringUtils.isBlank(selectDataSourceName)) {
            selectDataSourceName = request.getParameter(HTTP_DATASOURCE_NAME);
            logger.info("请求头中没有指定选择的数据源，然后从请求参数中获取的要选择的数据源，参数名：{}，传递的参数为" +
                            "：[{}]", HTTP_DATASOURCE_NAME, selectDataSourceName);
        }
        if (StringUtils.isBlank(selectDataSourceName)) {
            HttpSession session = request.getSession(false);
            Optional<HttpSession> dataSourceOptional = Optional.ofNullable(session);
            selectDataSourceName = dataSourceOptional.map(httpSession -> httpSession.getAttribute(HTTP_DATASOURCE_NAME))
                    .map(Objects::toString).orElse("");
            logger.info("请求参数中没有指定选择的数据源，然后准备从请求会话中获取选择的数据源，参数名：{}，" +
                            "参数的参数值为：[{}]", HTTP_DATASOURCE_NAME, selectDataSourceName);
        }
        if (StringUtils.isNotBlank(selectDataSourceName)) {
            //将选择的数据源设置到Session中
            Optional<HttpSession> sessionOptional = Optional.ofNullable(request.getSession(false));
            String finalDataSource = selectDataSourceName;
            sessionOptional.ifPresent(httpSession -> httpSession.setAttribute(HTTP_DATASOURCE_NAME, finalDataSource));
            DataSourceNameHolder.setActiveDataSource(finalDataSource);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        logger.info("准备清除选择的数据源的信息。");
        DataSourceNameHolder.clearDataSource();
    }
}
