package com.shihai.reservation.rocketmq.impl;

import com.alibaba.fastjson.JSON;
import com.shihai.reservation.rocketmq.RocketMQService;
import com.shihai.reservation.rocketmq.enums.JmsEnum;
import com.shihai.reservation.rocketmq.enums.MqLogStatusEnum;
import com.shihai.reservation.rocketmq.factory.RedMqLogFactory;
import com.shihai.reservation.rocketmq.listener.RedAliMessageListener;
import com.shihai.reservation.rocketmq.model.MqLog;
import com.shihai.reservation.rocketmq.model.ResultMQ;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
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
public class RocketMQServiceImpl implements RocketMQService {
    private static final Logger logger = LoggerFactory.getLogger(RocketMQServiceImpl.class);
    @Autowired
    private Environment env;
    //    @Autowired
//    private RedMqLogMapper redMqLogMapper;
//    @Value("${rocketmq.namesrv.addr}")
    private String nameSrvs;
    //    @Value("${rocketmq.namesrv.instanceName}")
    private String instanceName;

    private String accessKey;
    private String secretKey;
    private String sendMsgTimeoutMillis;
    private String ONSAddr;

    public String getAccessKey() {
        if (accessKey == null) {
            accessKey = env.getProperty("ali.rocketmq.AccessKey");
        }
        return accessKey;
    }

    public String getSecretKey() {
        if (secretKey == null) {
            secretKey = env.getProperty("ali.rocketmq.SecretKey");
        }
        return secretKey;
    }

    public String getSendMsgTimeoutMillis() {
        if (sendMsgTimeoutMillis == null) {
            sendMsgTimeoutMillis = env.getProperty("ali.rocketmq.SendMsgTimeoutMillis");
        }
        return sendMsgTimeoutMillis;
    }

    public String getONSAddr() {
        if (ONSAddr == null) {
            ONSAddr = env.getProperty("ali.rocketmq.ONSAddr");
        }
        return ONSAddr;
    }

    public String getNameSrvs() {
        if (nameSrvs == null) {
            nameSrvs = env.getProperty("rocketmq.namesrv.addr");
        }
        return nameSrvs;

    }

    public String getInstanceName() {
        if (instanceName == null) {
            instanceName = env.getProperty("rocketmq.namesrv.instanceName");
        }
        return instanceName;
    }

    private DefaultMQProducer getDefaultMQProducer(String groupName) {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(groupName);
        defaultMQProducer.setNamesrvAddr(getNameSrvs());
        try {
            defaultMQProducer.setInstanceName(getInstanceName() + "_" + groupName + "_" + InetAddress.getLocalHost().getHostAddress() + "_" + System.currentTimeMillis());
            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(3);
            defaultMQProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return defaultMQProducer;
    }

    @Override
    public DefaultMQPushConsumer startDefaultMQPushConsumer(String groupName, String topic, String tags, MessageListener call) {
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(groupName);
        defaultMQPushConsumer.setNamesrvAddr(getNameSrvs());
        try {
            defaultMQPushConsumer.setInstanceName(getInstanceName() + "_" + groupName + "_" + topic + "_" + InetAddress.getLocalHost().getHostAddress() + "_" + System.currentTimeMillis());
            defaultMQPushConsumer.subscribe(topic, tags);
            if (call instanceof MessageListenerConcurrently)
                defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) call);
            else
                defaultMQPushConsumer.registerMessageListener((MessageListenerOrderly) call);
            defaultMQPushConsumer.start();
            logger.info("groupName:{},topic:{},tags:{} ==================> start", groupName, topic, tags);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return defaultMQPushConsumer;
    }

    private void closeProducer(DefaultMQProducer producer) {
        if (producer != null) {
            producer.shutdown();
        }
    }

    private ResultMQ<SendResult> sendResult(String groupName, String topic, String tags, String keys, String result, boolean order,RedAliMessageListener.CallLog callLog) {
        ResultMQ<SendResult> resultMQ = new ResultMQ<>();
        DefaultMQProducer producer = null;
        producer = getDefaultMQProducer(groupName);
        if (keys == null) {
            try {
                keys = groupName + "|" + topic + "|" + System.currentTimeMillis() + "|" + result.hashCode() + "|" + InetAddress.getLocalHost().getHostAddress().hashCode();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        Message msg = new Message(topic, tags, keys, result.getBytes());
        MqLog mqLog = RedMqLogFactory.buildByMsg(msg, groupName, MqLogStatusEnum.SEND_FAIL.code());
        try {
            SendResult sendResult = null;
            if (!order) {
                sendResult = producer.send(msg);
            } else {
                sendResult = producer.send(msg, (mqs, msg1, arg) -> {
                    int size = mqs.size();
                    int index = arg.toString().length() % size;
                    return mqs.get(index);
                }, keys);
            }
            mqLog.setStatus(MqLogStatusEnum.TO_BE_CONSUMED.code());
            resultMQ.setSendResult(sendResult);
        } catch (Exception e) {
            logger.info("发送失败:" + e.getMessage());
            e.printStackTrace();
        } finally {
            closeProducer(producer);
        }
        resultMQ.setSendLog(JSON.toJSONString(mqLog));
        return resultMQ;
    }

    @Override
    public ResultMQ<SendResult> sendResultOrder(String groupName, String topic, String tags, String keys, String result,RedAliMessageListener.CallLog callLog) {
        if (StringUtils.isBlank(keys)) {
            throw new RuntimeException("顺序消息keys不能为null");
        }
        return sendResult(groupName, topic, tags, keys, result, true,callLog);
    }

    @Override
    public <T> ResultMQ<SendResult> sendResult(T t, JmsEnum jmsEnum,RedAliMessageListener.CallLog callLog) {
        return sendResult(t, jmsEnum, false, null, null,callLog);
    }

    @Override
    public <T> ResultMQ<SendResult> sendResult(T t, JmsEnum jmsEnum, boolean order, String tags, String keys,RedAliMessageListener.CallLog callLog) {
        try {
            String content = JSON.toJSONString(t);
            ResultMQ<SendResult> resultMQ = order ? sendResultOrder(jmsEnum.groupName(), jmsEnum.topic(), tags, keys, content,callLog) : sendResult(jmsEnum.groupName(), jmsEnum.topic(), tags, content,callLog);
            logger.info("发送结果:===============>{}", JSON.toJSONString(resultMQ));
            logger.info("rocketmq发送内容:================>{}", content);
            return resultMQ;
        } catch (Exception e) {
//            e.printStackTrace();
            logger.info("rocketmq发送失败:{}", e.getMessage());
//            throw new RedException("rocketmq发送失败");
        }
        return null;
    }

    @Override
    public ResultMQ<SendResult> sendResult(String groupName, String topic, String tags, String result, RedAliMessageListener.CallLog callLog) {
        return sendResult(groupName, topic, tags, null, result, false,callLog);
    }
}
