package com.leyou.auth.client;

import com.leyou.user.api.UserAPi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@FeignClient("user-service")
public interface UserClient extends UserAPi {
}
