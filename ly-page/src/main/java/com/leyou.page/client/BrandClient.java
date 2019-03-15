package com.leyou.page.client;


import com.leyou.item.api.BrandAPI;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@FeignClient("item-service")
public interface BrandClient extends BrandAPI{

}
