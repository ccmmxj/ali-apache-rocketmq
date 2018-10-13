package com.shihai.reservation.rocketmq;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import com.shihai.reservation.rocketmq.base.BaseMQ;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/18 15:37
 * ${TAGS}
 */
public interface AliMQService extends BaseMQ<SendResult,Consumer,MessageListener> {
    OrderConsumer startDefaultMQPushConsumer(String groupName, String topic, String tags, MessageOrderListener call) throws MQClientException;
}
