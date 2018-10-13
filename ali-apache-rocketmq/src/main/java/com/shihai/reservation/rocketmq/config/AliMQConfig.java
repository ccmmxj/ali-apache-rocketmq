package com.shihai.reservation.rocketmq.config;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/10 20:48
 * ${TAGS}
 */
@Configuration
public class AliMQConfig {
    @Autowired
    private Environment env;
    private String accessKey;
    private String secretKey;
    private String sendMsgTimeoutMillis;
    private String ONSAddr;

    public String getAccessKey() {
        if(accessKey == null){
            accessKey = env.getProperty("ali.rocketmq.AccessKey");
        }
        return accessKey;
    }

    public String getSecretKey() {
        if(secretKey == null){
            secretKey = env.getProperty("ali.rocketmq.SecretKey");
        }
        return secretKey;
    }

    public String getSendMsgTimeoutMillis() {
        if(sendMsgTimeoutMillis == null){
            sendMsgTimeoutMillis = env.getProperty("ali.rocketmq.SendMsgTimeoutMillis");
        }
        return sendMsgTimeoutMillis;
    }

    public String getONSAddr() {
        if(ONSAddr == null){
            ONSAddr = env.getProperty("ali.rocketmq.ONSAddr");
        }
        return ONSAddr;
    }
    public Producer producer(String producerId){
        Map<String,String> map = new HashMap<>();
        map.put(PropertyKeyConst.ProducerId,producerId);
        return ONSFactory.createProducer(build(map));
    }
    public OrderProducer orderProduct(String producerId){
        Map<String,String> map = new HashMap<>();
        map.put(PropertyKeyConst.ProducerId,producerId);
        return ONSFactory.createOrderProducer(build(map));
    }
    public Consumer consumer(String consumerId){
        Map<String,String> map = new HashMap<>();
        map.put(PropertyKeyConst.ConsumerId,consumerId);
        return ONSFactory.createConsumer(build(map));
    }
    public OrderConsumer orderConsumer(String consumerId){
        Map<String,String> map = new HashMap<>();
        map.put(PropertyKeyConst.ConsumerId,consumerId);
        return ONSFactory.createOrderedConsumer(build(map));
    }

    private Properties build(Map<String,String> map){
        Properties properties = new Properties();
        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey,getAccessKey());
        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, getSecretKey());
        // 设置 TCP 接入域名（此处以公共云生产环境为例）
        properties.put(PropertyKeyConst.ONSAddr, getONSAddr());
        if(map != null){
            map.keySet().forEach(v->{
                properties.put(v,map.get(v));
            });
        }
        return properties;
    }
}
