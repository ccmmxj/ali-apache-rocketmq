package com.shihai.reservation.rocketmq.factory;

import com.aliyun.openservices.ons.api.Message;
import com.shihai.reservation.rocketmq.model.MqLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/29 10:56
 * ${TAGS}
 */
public class RedMqLogFactory {
    private static final Logger logger = LoggerFactory.getLogger(RedMqLogFactory.class);
    private RedMqLogFactory(){

    }

    public static void unkownInit(MqLog mqLog, String orderIdSub){
        if(StringUtils.isNotBlank(mqLog.getOrderId())){
            if(mqLog.getOrderId().indexOf("|")>-1) {
                String groupName = mqLog.getOrderId().split("\\|")[0];
                mqLog.setGroupName(groupName);
            }
            mqLog.setOrderId(mqLog.getOrderId() + orderIdSub);
        } else {
            mqLog.setOrderId(System.currentTimeMillis() + orderIdSub);
        }
    }

    public static<T> MqLog buildByMsg(T msg, String groupName, Integer status, boolean auto){
        if(msg instanceof Message)
            return buildByMsg((Message)msg,groupName,status);
        if(msg instanceof org.apache.rocketmq.common.message.Message)
            return buildByMsg((org.apache.rocketmq.common.message.Message)msg,groupName,status);
        return null;
    }
    public static MqLog buildByMsg(Message msg, String groupName, Integer status){
        logger.info("ali msg =================> {}",msg);
        MqLog mqLog = new MqLog();
        mqLog.setContent(new String(msg.getBody()));
        mqLog.setGroupName(groupName);
        mqLog.setOrderId(msg.getKey());
        mqLog.setStatus(status);
        mqLog.setTag(msg.getTag());
        mqLog.setTimes(1);
        mqLog.setTopic(msg.getTopic());
        return mqLog;
    }
    public static MqLog buildByMsg(org.apache.rocketmq.common.message.Message msg, String groupName, Integer status){
        logger.info("apache msg =================> {}",msg);
        MqLog mqLog = new MqLog();
        mqLog.setContent(new String(msg.getBody()));
        mqLog.setGroupName(groupName);
        mqLog.setOrderId(msg.getKeys());
        mqLog.setStatus(status);
        mqLog.setTag(msg.getTags());
        mqLog.setTimes(1);
        mqLog.setTopic(msg.getTopic());
        return mqLog;
    }
}
