package com.leyou.cart.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.PanelUI;

/**
 * @version V1.0
 * @ClassName:
 * @Description: 加入购物车进行拦截
 * @author: cxg
 * @date :
 */
@Slf4j

public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    private  JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> tl=new ThreadLocal<>();

    public UserInterceptor(JwtProperties jwtProperties){
        this.jwtProperties=jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
       try {
           //解析token
           UserInfo user = JwtUtils.getInfoFromToken(jwtProperties.getPublicKey(), token);
           //传递user
           tl.set(user);
           //放行
           return  true;
       }catch (Exception e){
           log.error("[购物车服务]  解析身份失败",e);
           return false;
       }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //最后用完数据清空
        tl.remove();

    }
    //取出userinfo

    public static UserInfo getUser(){
        return tl.get();
    }
}
