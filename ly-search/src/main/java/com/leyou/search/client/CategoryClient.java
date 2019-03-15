package com.leyou.search.client;


import com.leyou.item.api.CategoryAPI;
import org.springframework.cloud.openfeign.FeignClient;


import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryAPI{


}
