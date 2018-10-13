package com.shihai.reservation.rocketmq.base;

import com.shihai.reservation.rocketmq.model.ResultMQ;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/11 16:01
 * ${TAGS}
 */
public interface BaseMQListener<MessageListenerOrderly,MessageListenerConcurrently,Message> {
    interface Call{
        void consumeMessage(String content) throws Exception;
    }
    interface CallLog{
        void saveLog(ResultMQ resultMQ);
    }
    ResultMQ after(Message msg, Call call);
    MessageListenerOrderly newInstaceOrderly(Call call,CallLog callLog);
    MessageListenerConcurrently newInstaceConcurrently(Call call,CallLog callLog);
}
