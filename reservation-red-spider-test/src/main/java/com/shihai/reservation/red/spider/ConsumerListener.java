package com.shihai.reservation.red.spider;

import com.alibaba.fastjson.JSON;
import com.shihai.reservation.rocketmq.base.BaseMQ;
import com.shihai.reservation.rocketmq.base.BaseMQListener;
import com.shihai.reservation.rocketmq.enums.JmsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebListener;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/9/18 17:46
 * ${TAGS}
 */
@WebListener
@Component
public class ConsumerListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(ConsumerListener.class);
    @Autowired
    private BaseMQ baseMQ;
    @Autowired
    private BaseMQListener baseMQListener;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //防止重复执行。
        if(event.getApplicationContext().getParent() == null){
            try {
                //测试消费者
                baseMQ.startDefaultMQPushConsumer(JmsEnum.RED_TEST.groupName(),JmsEnum.RED_TEST.topic(), "*",baseMQListener.newInstaceConcurrently(content -> {
                    logger.info("--测试mq--" + content);
                },resultMQ -> System.out.println(JSON.toJSONString(resultMQ))));
                //rocketMQService.sendResult("test", aliMqTopicConfig.RED_NOTE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
