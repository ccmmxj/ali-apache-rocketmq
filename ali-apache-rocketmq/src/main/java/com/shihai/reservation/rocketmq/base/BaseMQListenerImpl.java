package com.shihai.reservation.rocketmq.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.shihai.reservation.rocketmq.enums.MqLogStatusEnum;
import com.shihai.reservation.rocketmq.factory.RedMqLogFactory;
import com.shihai.reservation.rocketmq.factory.ResultMQFactory;
import com.shihai.reservation.rocketmq.model.MqLog;
import com.shihai.reservation.rocketmq.model.ResultMQ;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/11 16:01
 * ${TAGS}
 */
@Component
public abstract class BaseMQListenerImpl<MessageListenerOrderly,MessageListenerConcurrently,Message>  implements BaseMQListener<MessageListenerOrderly,MessageListenerConcurrently,Message> {
    private static final Logger logger = LoggerFactory.getLogger(BaseMQListenerImpl.class);
    @Override
    public ResultMQ after(Message msg, Call call){
        String orderId="";
        String content="";
        MqLog mqLog = RedMqLogFactory.buildByMsg(msg, "未知", MqLogStatusEnum.FAIL_CONSUMED.code(),true);
        RedMqLogFactory.unkownInit(mqLog,"");
        try {
            // Message   (ali Message的order为key，apache为keys)
            if(msg instanceof com.aliyun.openservices.ons.api.Message){//ali message
                orderId = ((com.aliyun.openservices.ons.api.Message)msg).getKey();
                content = new String(((com.aliyun.openservices.ons.api.Message)msg).getBody(),"UTF-8");
            }else if(msg instanceof org.apache.rocketmq.common.message.Message) {//apache message
                orderId = ((org.apache.rocketmq.common.message.Message)msg).getKeys();
                content = new String(((org.apache.rocketmq.common.message.Message)msg).getBody(),"UTF-8");
            }else {//其他
                JSONObject value = JSON.parseObject(new Gson().toJson(msg));
                orderId = StringUtils.isNotBlank(value.getString("keys"))?value.getString("keys"):value.getString("key");
                content = new String(value.getBytes("body"),"UTF-8");
            }
            logger.info("开始消费orderId:{}",orderId);
            call.consumeMessage(content);
            logger.info("消费完成orderId:{}",orderId);
            mqLog.setStatus(MqLogStatusEnum.ALREADY_CONSUMED.code());
        } catch (Exception e) {
            logger.info("消费失败orderId:{}",orderId);
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        return ResultMQFactory.newInstance(mqLog);
    }
}
