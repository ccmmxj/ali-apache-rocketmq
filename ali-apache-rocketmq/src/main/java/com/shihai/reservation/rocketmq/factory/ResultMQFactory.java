package com.shihai.reservation.rocketmq.factory;

import com.alibaba.fastjson.JSON;
import com.shihai.reservation.rocketmq.model.MqLog;
import com.shihai.reservation.rocketmq.model.ResultMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/13 16:13
 * ${TAGS}
 */
public class ResultMQFactory {
    private static final Logger logger = LoggerFactory.getLogger(ResultMQFactory.class);
    private ResultMQFactory(){

    }
    public static<T> ResultMQ<T> newInstance(T t, MqLog mqLog){
        ResultMQ<T> resultMQ = new ResultMQ<>();
        resultMQ.setSendLog(JSON.toJSONString(mqLog));
        resultMQ.setSendResult(t);
        return resultMQ;
    }
    public static ResultMQ newInstance(MqLog mqLog){
        return newInstance(null,mqLog);
    }
}
