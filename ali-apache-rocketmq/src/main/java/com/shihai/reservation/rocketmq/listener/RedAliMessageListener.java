package com.shihai.reservation.rocketmq.listener;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.shihai.reservation.rocketmq.base.BaseMQListenerImpl;
import com.shihai.reservation.rocketmq.model.ResultMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/10 14:36
 * ${TAGS}
 */
@Component
public class RedAliMessageListener extends BaseMQListenerImpl<MessageOrderListener,MessageListener,Message>{
    private static final Logger logger = LoggerFactory.getLogger(RedAliMessageListener.class);

    @Override
    public MessageOrderListener newInstaceOrderly(Call call,CallLog callLog){
        MessageOrderListener messageOrderListener = (msg, context) -> {
            callLog.saveLog(after(msg, call));
            return OrderAction.Success;
        };
        return messageOrderListener;
    }
    @Override
    public MessageListener newInstaceConcurrently(Call call,CallLog callLog){
        MessageListener messageListener = (msg, context) -> {
            callLog.saveLog(after(msg, call));
            return Action.CommitMessage;
        };
        return messageListener;
    }
}
