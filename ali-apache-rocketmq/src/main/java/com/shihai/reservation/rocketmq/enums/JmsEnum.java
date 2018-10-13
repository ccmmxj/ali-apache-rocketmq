package com.shihai.reservation.rocketmq.enums;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/18 17:22
 * ${TAGS}
 */
public enum JmsEnum {
    RED_LABEL("red_label","red_book","小红书标签","label"),
    RED_NOTE("red_note","red_book","小红书标签","post"),
    RED_USER("red_user","red_book","小红书标签","user"),
    RED_TEST("red_test","red_test","test","test");
    private String topic;
    private String groupName;
    private String desc;
    private String msgType;

    JmsEnum(String topic, String groupName, String desc,String msgType) {
        this.topic = topic;
        this.groupName = groupName;
        this.desc = desc;
        this.msgType = msgType;
    }

    public String msgType() {
        return msgType;
    }

    public String topic() {
        return topic;
    }

    public String groupName() {
        return groupName;
    }

    public String desc() {
        return desc;
    }
}
