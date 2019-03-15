package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkerProperties {
    private long workerId;// 当前机器id
    private long dataCenterId;// 序列号
}
