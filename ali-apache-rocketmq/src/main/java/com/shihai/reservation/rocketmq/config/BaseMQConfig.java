package com.shihai.reservation.rocketmq.config;

import com.shihai.reservation.rocketmq.utils.PropertyUtil;
import com.shihai.reservation.rocketmq.AliMQService;
import com.shihai.reservation.rocketmq.RocketMQService;
import com.shihai.reservation.rocketmq.base.BaseMQ;
import com.shihai.reservation.rocketmq.base.BaseMQListener;
import com.shihai.reservation.rocketmq.listener.RedAliMessageListener;
import com.shihai.reservation.rocketmq.listener.RedMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/11 16:09
 * ${TAGS}
 */
@Configuration
public class BaseMQConfig {
    @Autowired
    private Environment env;
    @Autowired
    private AliMQService aliMQService;
    @Autowired
    private RocketMQService rocketMQService;
    @Autowired
    private RedAliMessageListener redAliMessageListener;
    @Autowired
    private RedMessageListener redMessageListener;

    private boolean checkProd() {
        if (env != null) {
            return ("prod".equals(env.getProperty("spring.profiles.active")) || env.getProperty("rocketmq.alimq.switch", Boolean.class,true));
        }
        return ("prod".equals(PropertyUtil.getProperty("applicationContent", "spring.profiles.active", "dev"))||Boolean.valueOf(PropertyUtil.getProperty("applicationContent","rocketmq.alimq.switch", "true")));
    }


    @Bean
    public BaseMQ baseMQ() {
        if (checkProd()) {
            return aliMQService;
        }
        return rocketMQService;
    }

    @Bean
    public BaseMQListener baseMQListener() {
        if (checkProd()) {
            return redAliMessageListener;
        }
        return redMessageListener;
    }
}
