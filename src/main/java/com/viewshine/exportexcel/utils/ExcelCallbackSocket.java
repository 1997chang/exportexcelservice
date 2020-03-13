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
 * Excel下载完成的回调通知器
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

    /**
     * 调用这个方法将会通知客户端Excel文件下载完成。进行Excel下载。
     *     1.创建一个Tsocket的客户端。
     *     2.通知客户端文件下载完成。
     * @param host 客户端的主机
     * @param port 客户端的端口
     * @param message 通知的消息
     * @return 是否通知到
     */
    public static boolean executeExcelBack(String host, int port, Message message) {
        logger.info("Excel文件下载完成，准备通知客户端进行下载。客户端的HOST：[{}]，PORT：[{}]，Excel的ID：[{}]",
                host, port, message.excelId);
        ExcelCallbackSocket excelCallbackSocket = new ExcelCallbackSocket(host, port);
        try {
            excelCallbackSocket.tTransport.open();
            ResultCallback callback = excelCallbackSocket.client.callbacl(message);
            if (callback.getCode() == 200) {
                logger.info("Excel文件下载通知客户端成功。");
                return true;
            }
            logger.warn("消息通知客户端出现错误，没有返回200，返回的内容为：{}", JSON.toJSONString(callback));
        } catch (TException e) {
            logger.warn("执行消息通知出现错误。host:[{}]，port:[{}]", host, port);
            logger.warn(e.getMessage(), e);
        } finally {
            if (Objects.nonNull(excelCallbackSocket.tTransport)) {
                excelCallbackSocket.tTransport.close();
            }
        }
        return false;
    }

}
