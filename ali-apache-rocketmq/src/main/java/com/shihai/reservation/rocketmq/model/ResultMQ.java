package com.shihai.reservation.rocketmq.model;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/13 16:12
 * ${TAGS}
 */

public class ResultMQ<SendResult>{
    private SendResult sendResult;
    private String sendLog;

    public SendResult getSendResult() {
        return sendResult;
    }

    public void setSendResult(SendResult sendResult) {
        this.sendResult = sendResult;
    }

    public String getSendLog() {
        return sendLog;
    }

    public void setSendLog(String sendLog) {
        this.sendLog = sendLog;
    }
}
