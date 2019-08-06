package com.grgbanking.grgacd;

import com.grgbanking.grgacd.common.RedisUtil;
import com.grgbanking.grgacd.redisevent.RedisListenerConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hsjiang
 * @version 1.0
 * @Description: TODO
 * @date 2019/7/26/026
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes={GrgAcdApplication.class})// 指定启动类
public class RedisEventTest {
    @Autowired
    private RedisListenerConfig redisListenerConfig;
    @Autowired private RedisTemplate redisTemplate;
    private String key;
    private MessageListener listener;
    @Before
    public void testBefore(){
        listener = new MessageListener(){

            @Override
            public void onMessage(Message message, byte[] bytes) {
                key = message.toString();
                System.out.println(key);
            }
        };
        redisListenerConfig.addMessageListener(listener, new PatternTopic("__keyevent@0__:*"));
        RedisUtil<String> redisUtil = new RedisUtil<>(redisTemplate, "testkey");
        redisUtil.setExpireTime("testkey",3L);
        redisUtil.pushFromTail("123");
    }

    @Test
    public void test(){
        try {
            Thread.sleep(4*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals("testkey",key);
    }

    @After
    public void testAfter(){
        redisListenerConfig.removeMessageListener(listener);
    }
}
