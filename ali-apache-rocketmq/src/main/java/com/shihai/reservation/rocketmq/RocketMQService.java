package com.shihai.reservation.rocketmq;

import com.shihai.reservation.rocketmq.base.BaseMQ;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/18 15:37
 * ${TAGS}
 */
public interface RocketMQService extends BaseMQ<SendResult,DefaultMQPushConsumer,MessageListener> {
}
