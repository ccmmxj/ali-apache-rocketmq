package com.shihai.reservation.rocketmq.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import com.shihai.reservation.rocketmq.AliMQService;
import com.shihai.reservation.rocketmq.config.AliMQConfig;
import com.shihai.reservation.rocketmq.enums.JmsEnum;
import com.shihai.reservation.rocketmq.enums.MqLogStatusEnum;
import com.shihai.reservation.rocketmq.factory.RedMqLogFactory;
import com.shihai.reservation.rocketmq.listener.RedAliMessageListener;
import com.shihai.reservation.rocketmq.model.MqLog;
import com.shihai.reservation.rocketmq.model.ResultMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/18 15:37
 * ${TAGS}
 */
@Service
public class AliMQServiceImpl implements AliMQService {
    private static final Logger logger = LoggerFactory.getLogger(AliMQServiceImpl.class);
//    @Autowired
//    private RedMqLogMapper redMqLogMapper;

    @Autowired
    private AliMQConfig aliMqConfig;

    @Autowired
    private Environment env;

    private String active;

    public String getActive() {
        if(active == null){
            active = env.getProperty("spring.profiles.active");
        }
        return active;
    }

    private Admin getDefaultMQProducer(boolean order,String topic) {
        Admin producer = null;
        if(order)
            producer = aliMqConfig.orderProduct("PID" + "_" + topic.toUpperCase() + "_" + 1);
        else
            producer = aliMqConfig.producer("PID" + "_" + topic.toUpperCase() + "_" + 1);
        producer.start();
        return producer;
    }

    @Override
    public Consumer startDefaultMQPushConsumer(String groupName, String topic, String tags, MessageListener call) {
        if(!"prod".equals(active))topic = topic+"_test";
        Consumer consumer = aliMqConfig.consumer("CID" + "_" + topic.toUpperCase() + "_" + 1);
        consumer.subscribe(topic,tags,call);
        consumer.start();
        logger.info("groupName:{},topic:{},tags:{} ==================> start",groupName,topic,tags);
        return consumer;
    }
    @Override
    public OrderConsumer startDefaultMQPushConsumer(String groupName, String topic, String tags, MessageOrderListener call) {
        if(!"prod".equals(active))topic = topic+"_test";
        OrderConsumer consumer = aliMqConfig.orderConsumer("CID" + "_" + topic.toUpperCase() + "_" + 1);
        consumer.subscribe(topic,tags,call);
        consumer.start();
        logger.info("groupName:{},topic:{},tags:{} ==================> start",groupName,topic,tags);
        return consumer;
    }
    private void closeProducer(Admin producer){
        if(producer != null) {
            producer.shutdown();
        }
    }

    private ResultMQ<SendResult> sendResult(String groupName, String topic, String tags, String keys, String result, boolean order, RedAliMessageListener.CallLog callLog){
        if(!"prod".equals(active))topic = topic+"_test";
        ResultMQ<SendResult> resultMQ = new ResultMQ<>();
        Admin producer = null;
        producer = getDefaultMQProducer(order,topic);
        if(keys == null){
            try {
                keys = groupName + "|" + topic + "|"  + System.currentTimeMillis() + "|" + result.hashCode() + "|" + InetAddress.getLocalHost().getHostAddress().hashCode();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new RuntimeException("ip未知");
            }
        }
        Message msg = new Message(topic,tags,keys,result.getBytes());
        msg.setMsgID(keys);
        MqLog mqLog = RedMqLogFactory.buildByMsg(msg, groupName, MqLogStatusEnum.SEND_FAIL.code());
        try {
            SendResult sendResult = null;
            if(!order) {
                sendResult = ((Producer)producer).send(msg);
            } else {
                sendResult = ((OrderProducer)producer).send(msg,keys);
            }
            mqLog.setStatus(MqLogStatusEnum.TO_BE_CONSUMED.code());
            resultMQ.setSendResult(sendResult);
        } catch (Exception e) {
            logger.info("发送失败:"+e.getMessage());
            e.printStackTrace();
        }finally {
            closeProducer(producer);
        }
        resultMQ.setSendLog(JSON.toJSONString(mqLog));
        callLog.saveLog(resultMQ);
        return resultMQ;
    }
    @Override
    public ResultMQ<SendResult> sendResultOrder(String groupName, String topic, String tags, String keys, String result, RedAliMessageListener.CallLog callLog){
        return sendResult(groupName,topic,tags,keys,result,true,callLog);
    }
    @Override
    public <T>ResultMQ<SendResult> sendResult(T t, JmsEnum jmsEnum, RedAliMessageListener.CallLog callLog) {
        return sendResult(t,jmsEnum,false,null,null,callLog);
    }
    @Override
    public <T>ResultMQ<SendResult> sendResult(T t, JmsEnum jmsEnum, boolean order, String tags, String keys, RedAliMessageListener.CallLog callLog) {
        try {
            String content = JSON.toJSONString(t);
            ResultMQ<SendResult> resultMQ = order?sendResultOrder(jmsEnum.groupName(), jmsEnum.topic(), tags,keys, content,callLog):sendResult(jmsEnum.groupName(), jmsEnum.topic(), tags, content,callLog);
            logger.info("发送结果:===============>{}",JSON.toJSONString(resultMQ));
            logger.info("rocketmq发送内容:================>{}",content);
            return resultMQ;
        } catch (Exception e) {
//            e.printStackTrace();
            logger.info("rocketmq发送失败:{}",e.getMessage());
//            throw new RedException("rocketmq发送失败");
        }
        return null;
    }

    @Override
    public ResultMQ<SendResult> sendResult(String groupName, String topic, String tags, String result, RedAliMessageListener.CallLog callLog) {
        return sendResult(groupName,topic,tags,null,result,false,callLog);
    }
}
