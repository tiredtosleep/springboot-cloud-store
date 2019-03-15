package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.user.pojo.User;
import com.netflix.discovery.converters.Auto;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;
    public String login(String username, String password) {
        try {
            //校验用户名和密码
            User user = userClient.queryUser(username, password);
            //判断
            if (user==null){
                throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
            }
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            //生成token
            //String token = JwtUtils.generateToken(new UserInfo(user.getId(),username),jwtProperties.getPriKeyPath(),jwtProperties.getExpire());
            String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        }catch (Exception e){
            log.error("[授权中心] 生成token失败，用户名称："+username,e);
            throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }

    }
}
