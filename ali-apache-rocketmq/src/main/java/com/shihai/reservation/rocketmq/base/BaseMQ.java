package com.shihai.reservation.rocketmq.base;

import com.shihai.reservation.rocketmq.enums.JmsEnum;
import com.shihai.reservation.rocketmq.listener.RedAliMessageListener;
import com.shihai.reservation.rocketmq.model.ResultMQ;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/11 16:01
 * ${TAGS}
 */
public interface BaseMQ<SendResult,Consumer,MessageListener> {
    public interface Call<Message>{
        void call(Message msg);
    }
    Consumer startDefaultMQPushConsumer(String groupName, String topic, String tags, MessageListener call);
    ResultMQ<SendResult> sendResultOrder(String groupName, String topic, String tags, String keys, String result,RedAliMessageListener.CallLog callLog);
    <T>ResultMQ<SendResult> sendResult(T t, JmsEnum jmsEnum, RedAliMessageListener.CallLog callLog);
    <T>ResultMQ<SendResult> sendResult(T t, JmsEnum jmsEnum, boolean order, String tags, String keys, RedAliMessageListener.CallLog callLog);
    ResultMQ<SendResult> sendResult(String groupName,String topic,String tags,String result, RedAliMessageListener.CallLog callLog);
}
