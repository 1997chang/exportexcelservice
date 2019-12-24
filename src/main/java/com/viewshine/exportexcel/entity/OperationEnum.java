package com.viewshine.exportexcel.entity;

import com.viewshine.exportexcel.exceptions.CommonRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BinaryOperator;

/**
 * @author changWei[changwei@viewshine.cn]
 */
public enum OperationEnum {

    ADD(2, '+', BigDecimal::add),
    SUBTRACT(2, '-', BigDecimal::subtract),
    MULTI(1, '*', BigDecimal::multiply),
    DIVIDE(1, '/', BigDecimal::divide);
//    LEFT_PARENTHESIS(3, '(', BigDecimal::divide),
//    RIGHT_PARENTHESIS(0, ')', BigDecimal::divide);

    private static final Logger logger = LoggerFactory.getLogger(OperationEnum.class);

    /**
     * 表示优先级
     */
    private final int order;

    /**
     * 表示对应的操作符
     */
    private final char operation;

    /**
     * 表示进行二元计算
     */
    private final BinaryOperator<BigDecimal> binaryOperator;

    OperationEnum(int order, char operation, BinaryOperator<BigDecimal> binaryOperator) {
        this.order = order;
        this.operation = operation;
        this.binaryOperator = binaryOperator;
    }

    public char getOperation() {
        return operation;
    }

    /**
     * 表示上一个操作符是否弹出计算
     * @param preOperation 表示上一个操作符
     * @return true表示弹出计算，否则，不用弹出计算
     */
    public boolean isCompute(OperationEnum preOperation) {
        if (preOperation == null) {
            return false;
        }
        return this.order >= preOperation.order;
    }

    /**
     * 计算两个值
     * 注意：number2要放在第一个位置，number1放在第二个位置，不能颠倒
     * @param number1 操作数1
     * @param number2 操作数2
     * @return 返回计算结果
     */
    public BigDecimal compute(BigDecimal number1, BigDecimal number2) {
        if (this.equals(DIVIDE)) {
            if (number1.equals(BigDecimal.ZERO)) {
                logger.warn("在计算表达式的时候，除数为0，请检查，返回操作数0。被除数：{}，除数：{}", number2, number1);
                return BigDecimal.ZERO;
            }
        }
        return binaryOperator.apply(number2, number1);
    }

    /**
     * 根据某一个操作符获取对应的操作符
     * @param operation 操作符号
     * @return 操作枚举
     */
    public static OperationEnum getInstance(char operation) {
        return Arrays.stream(values()).filter(operationEnum -> Objects.equals(operationEnum.operation, operation)).
                findFirst().orElseThrow(CommonRuntimeException::new);
    }
}