package com.shihai.reservation.red.spider.ctl;

import com.alibaba.fastjson.JSON;
import com.shihai.reservation.rocketmq.base.BaseMQ;
import com.shihai.reservation.rocketmq.enums.JmsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private BaseMQ baseMQ;
    @RequestMapping("/test")
    @ResponseBody
    public String test(String user) {
        baseMQ.sendResult(user, JmsEnum.RED_TEST, resultMQ -> System.out.println(JSON.toJSONString(resultMQ)));
        return "success";
    }
}
