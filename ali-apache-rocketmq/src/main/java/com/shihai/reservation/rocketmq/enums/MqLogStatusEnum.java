package com.shihai.reservation.rocketmq.enums;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/18 17:22
 * ${TAGS}
 */
public enum MqLogStatusEnum {
    TO_BE_CONSUMED(0,"待消费"),
    ALREADY_CONSUMED(1,"已经消费"),
    FAIL_CONSUMED(2,"消费失败"),
    SEND_FAIL(3,"发送失败"),
    ;
    private int code;
    private String desc;

    MqLogStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() {
        return code;
    }

    public String desc() {
        return desc;
    }
}
