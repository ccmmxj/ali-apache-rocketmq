package com.shihai.reservation.rocketmq.utils;

/**
 * @description
 * @author: Mingxi Chen
 * @Version: v0.1
 * @time: 2018/10/13 11:50
 * ${TAGS}
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Desc:properties文件获取工具类
 * Created by hafiz.zhang on 2016/9/15.
 */
public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);
    private static Map<String,Properties> propertiesMap = new HashMap<>();

    synchronized static private void loadProps(String propName){
        logger.info("开始加载properties文件内容.......");
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = PropertyUtil.class.getClassLoader().getResourceAsStream(propName+".properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            logger.error("jdbc.properties文件未找到");
        } catch (IOException e) {
            logger.error("出现IOException");
        } finally {
            try {
                if(null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("jdbc.properties文件流关闭出现异常");
            }
        }
        logger.info("加载properties文件内容完成...........");
        logger.info("properties文件内容：" + props);
        propertiesMap.put(propName,props);
    }

    public static String getProperty(String propName,String key){
        if(null == propertiesMap.get(propName)) {
            loadProps(propName);
        }
        return propertiesMap.get(propName).getProperty(key);
    }

    public static String getProperty(String propName,String key, String defaultValue) {
        if(null == propertiesMap.get(propName)) {
            loadProps(propName);
        }
        return propertiesMap.get(propName).getProperty(key, defaultValue);
    }

    public static Properties getProperties(String propName) {
        if(null == propertiesMap.get(propName)) {
            loadProps(propName);
        }
        return propertiesMap.get(propName);
    }
}