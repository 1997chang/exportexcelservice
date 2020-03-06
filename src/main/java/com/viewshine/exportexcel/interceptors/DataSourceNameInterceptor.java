package com.viewshine.exportexcel.interceptors;

import com.viewshine.exportexcel.entity.enums.DataSourceType;
import com.viewshine.exportexcel.exceptions.MultiDataSourceException;
import com.viewshine.exportexcel.utils.DataSourceHolder;
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

import static com.viewshine.exportexcel.constants.DataSourceConstants.*;

/**
 * 这个表示对外部Web请求进行拦截，用于设置要选择的数据源名称以及数据源的类型
 * @author ChangWei[changwei@viewshine.cn]
 */
@Order(-1)
@Component
public class DataSourceNameInterceptor extends HandlerInterceptorAdapter {

    private final static Logger logger = LoggerFactory.getLogger(DataSourceNameInterceptor.class);

    /**
     * 对外部的请求进行拦截
     *      1.获取连接数据库的类型，MONGO，MYSQL
     *      2.用于将选择的数据库的名称进行设置
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("请求地址为：【{}】，准备导出Excel数据内容", request.getRequestURL());
        setDatasourceType(request);
        setDatasourceName(request);
        return true;
    }


    /**
     * 当执行完成之后，清空当前线程中的选择的数据源的类型，数据源的名称.
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        logger.info("准备清除线程：[{}]的数据源的信息。", Thread.currentThread().getName());
        DataSourceHolder.clearDataSourceType();
        DataSourceHolder.clearDataSourceName();
    }

    /**
     * 用于从请求中获取选择的数据库的名称，动态的选择查询的的数据库。
     *  1.首先从请求中获取数据源的名称，
     *  2.将数据源的名称设置到当前线程中。
     * @param request Http请求的内容
     */
    private void setDatasourceName(final HttpServletRequest request) {
        String datasourceName = getParamFromHttpByName(request, HTTP_DATASOURCE_NAME, "数据源的名称");
        if (StringUtils.isNotBlank(datasourceName)) {
            logger.info("最终选择的数据源的名称为:[{}]", datasourceName);
            //将选择的数据源设置到Session中
            Optional<HttpSession> sessionOptional = Optional.ofNullable(request.getSession(false));
            sessionOptional.ifPresent(httpSession -> httpSession.setAttribute(HTTP_DATASOURCE_NAME, datasourceName));
            DataSourceHolder.setActiveDataSourceName(datasourceName);
            return;
        }
        logger.warn("当前请求没有提供选择的数据源的名称，使用默认的数据源的名称：[{}]", DEFAULT_DATASOURCE_NAME);
    }

    /**
     * 用于从请求中获取选择的数据库的类型，这个参数必须指定，如果没有或者错误的话，都直接返回
     * 1.首先从请求中获取数据源的类型。如果不存在就抛出异常
     * 2.将数据源的类型设置到当前线程中
     * @param request Http请求的内容
     */
    private void setDatasourceType(final HttpServletRequest request) {
        String datasourceType = getParamFromHttpByName(request, HTTP_DATASOURCE_TYPE, "数据源的类型");
        try {
            DataSourceType dataSourceType = DataSourceType.valueOf(datasourceType);
            DataSourceHolder.setActiveDataSourceType(dataSourceType);
            logger.info("最终选择的数据源的类型为：[{}]", dataSourceType.getCName());
        } catch (IllegalArgumentException e) {
            logger.error("没有指定选择的数据源的类型，不能继续执行请求。参数名为：[{}]", HTTP_DATASOURCE_TYPE);
            throw new MultiDataSourceException("没有指定选择的数据源的类型。");
        }
    }

    /**
     * 用于从Http请求中获取指定名称的参数值
     *      1.首先从Header中获取参数，如果有的话直接返回
     *      2.再从参数列表中获取指定的参数，如果有的话直接返回
     *      3.最后从HTTPSession中获取指定的参数，如果有的话直接返回
     *      4.如果都没有返回空字符串
     * @param request Http请求
     * @param paramName 参数名.
     * @param hint 提示信息
     * @return 参数值
     */
    private String getParamFromHttpByName(final HttpServletRequest request, final String paramName, final String hint) {
        String paramValue = request.getHeader(paramName);
        logger.info("首先从请求Head中获取[{}]：[{}]。", hint, paramValue);
        if (StringUtils.isBlank(paramValue)) {
            paramValue = request.getParameter(paramName);
            logger.info("请求头中没有指定[{}]，然后从请求参数中获取[{}]：[{}]", paramName, paramName, paramValue);
        }
        if (StringUtils.isBlank(paramValue)) {
            HttpSession session = request.getSession(false);
            Optional<HttpSession> httpSessionOptional = Optional.ofNullable(session);
            paramValue = httpSessionOptional.map(httpSession -> httpSession.getAttribute(paramName))
                    .map(Objects::toString).orElse("");
            logger.info("请求参数中没有指定[{}]，然后从请求会话中获取[{}]：[{}]", paramName, paramName, paramValue);
        }
        return paramValue;
    }
}
