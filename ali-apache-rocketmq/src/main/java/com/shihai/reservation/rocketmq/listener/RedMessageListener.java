package com.shihai.reservation.rocketmq.listener;

import com.shihai.reservation.rocketmq.base.BaseMQListenerImpl;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/10 14:36
 * ${TAGS}
 */
@Component
public class RedMessageListener extends BaseMQListenerImpl<MessageListenerOrderly,MessageListenerConcurrently,Message> {
    private static final Logger logger = LoggerFactory.getLogger(RedMessageListener.class);
    @Override
    public MessageListenerOrderly newInstaceOrderly(Call call,CallLog callLog){
        MessageListenerOrderly messageListenerOrderly = (msgs, context) -> {
            msgs.forEach(v-> {
                callLog.saveLog(after(v, call));
            });
            return ConsumeOrderlyStatus.SUCCESS;
        };
        return messageListenerOrderly;
    }
    @Override
    public MessageListenerConcurrently newInstaceConcurrently(Call call,CallLog callLog){
        MessageListenerConcurrently messageListenerConcurrently = (msgs, context) -> {
            msgs.forEach(v-> {
                callLog.saveLog(after(v, call));
            });
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
        return messageListenerConcurrently;
    }

}
