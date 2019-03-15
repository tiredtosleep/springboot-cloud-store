package com.leyou.sms.mq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSms() throws Exception {
        HashMap<Object, Object> msg = new HashMap<>();
        msg.put("phone","18860892543");
        msg.put("code","200888");
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
        Thread.sleep(10000L);
    }
}