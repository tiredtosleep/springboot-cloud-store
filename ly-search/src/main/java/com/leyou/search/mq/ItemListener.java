package com.leyou.search.mq;

import com.leyou.search.service.SearchService;
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
 * @Description:接收mq消息
 * @author:cxg
 * @Date:${time}
 */
@Component
public class ItemListener {
    @Autowired
    private SearchService searchService;

    /**
     * 处理insert和update的消息
     *
     * @param spuId
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.insert.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.insert","item.update"}
    ))
    public void listenInsertOrUpdate(Long spuId){
        if (spuId==null){
            return;
        }
        //处理消息，对索引库进行新增和修改
        searchService.createOrUpdateIndex(spuId);
    }

    /**
     * 处理delete的消息
     *
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search.item.delete.queue",durable = "true"),
            exchange = @Exchange(name = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"delete"}
    ))
    public void listenDelete(Long spuId){
        if (spuId==null){
            return;
        }
        //处理消息，对索引库进行新增和修改
        searchService.deleteIndex(spuId);
    }
}
