package com.leyou.page.mq;

import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Component
public class ItemListener {
    @Autowired
    private PageService pageService;

    /**
     * 处理insert和update的消息
     *
     * @param spuId
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.insert.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void listenInsertOrUpdate(Long spuId){
        if (spuId==null){
            return;
        }
        //处理消息，创建一个静态页
       pageService.createHtml(spuId);
    }

    /**
     * 处理delete的消息
     *
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "page.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"delete"}
    ))
    public void listenDelete(Long spuId){
        if (spuId==null){
            return;
        }
        //处理消息，对静态页删除
        pageService.deleteHtml(spuId);
    }
}

