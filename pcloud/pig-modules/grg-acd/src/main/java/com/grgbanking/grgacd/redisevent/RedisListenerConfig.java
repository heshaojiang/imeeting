package com.grgbanking.grgacd.redisevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: redis消息监听容器扩展类，
 * 添加一些通用的方法
 * @auther: hsjiang
 * @date: 2019/7/25/025
 * @version 1.0
 */
@Component
public class RedisListenerConfig extends RedisMessageListenerContainer {
    public static final List<PatternTopic> listAddTopics = new ArrayList<>();
    static {
        listAddTopics.add(new PatternTopic("__keyevent@0__:rpush"));//尾部插入
        listAddTopics.add(new PatternTopic("__keyevent@0__:lpush"));//头部插入
        listAddTopics.add(new PatternTopic("__keyevent@0__:lset"));//根据下标插入
    }
    public static final List<PatternTopic> zSetAddTopics = new ArrayList<>();
    static {
        zSetAddTopics.add(new PatternTopic("__keyevent@0__:zadd"));//zset添加元素
    }
    @Autowired private RedisConnectionFactory connectionFactory;
    /**
     * 初始化方法，spring注入bean不会调用构造函数
     * @param
     * @return
     * @author hsjiang
     * @date 2019/7/26/026
     **/
    @PostConstruct
    public void init(){
        setConnectionFactory(connectionFactory);
    }

    /**
     * 重载方法，设置通用的PatternTopic
     * PatternTopic中的参数不懂自己百度
     * @param listener 监听通知对象
     * @return
     * @author hsjiang
     * @date 2019/7/26/026
     **/
    public void addMessageListener(MessageListener listener) {
        this.addMessageListener(listener,new PatternTopic("__keyevent@0__:*"));
    }

}
