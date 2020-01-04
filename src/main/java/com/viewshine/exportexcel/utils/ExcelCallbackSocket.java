package com.viewshine.exportexcel.utils;

import com.alibaba.fastjson.JSON;
import com.viewshine.exportexcel.entity.thrift.AutoListenerService;
import com.viewshine.exportexcel.entity.thrift.Message;
import com.viewshine.exportexcel.entity.thrift.ResultCallback;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.viewshine.exportexcel.constants.DataSourceConstants.THRIFT_TIMEOUT;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public class ExcelCallbackSocket {

    private static final Logger logger = LoggerFactory.getLogger(ExcelCallbackSocket.class);

    /**
     * 传输对象
     */
    private TTransport tTransport;

    /**
     * 表示Client客户端对象
     */
    private AutoListenerService.Client client;

    private ExcelCallbackSocket(String host, int port) {
        TSocket tSocket = new TSocket(host, port, THRIFT_TIMEOUT);
        tTransport = new TFramedTransport(tSocket);
        TProtocol tProtocol = new TCompactProtocol(tTransport);
        client = new AutoListenerService.Client(tProtocol);
    }

    public static boolean executeExcelBack(String host, int port, Message message) {
        ExcelCallbackSocket excelCallbackSocket = new ExcelCallbackSocket(host, port);
        try {
            excelCallbackSocket.tTransport.open();
            ResultCallback callback = excelCallbackSocket.client.callbacl(message);
            if (callback.getCode() == 200) {
                return true;
            } else {
                logger.error("消息回调的客户端出现错误，没有返回200，返回的内容为：{}", JSON.toJSONString(callback));
                return false;
            }
        } catch (TException e) {
            logger.error("执行回调出现错误。host:[{}]，port:[{}]", host, port);
            logger.error(e.getMessage(), e);
        } finally {
            if (!Objects.isNull(excelCallbackSocket.tTransport)) {
                excelCallbackSocket.tTransport.close();
            }
        }
        return false;
    }

}
