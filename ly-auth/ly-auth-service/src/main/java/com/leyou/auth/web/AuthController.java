package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:授权中心
 * @author:cxg
 * @Date:${time}
 */
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登入授权功能
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public ResponseEntity<Void> login(
            @RequestParam("username")String username,
            @RequestParam("password")String password,
            HttpServletRequest request,
            HttpServletResponse response){
        //登入校验
        String token = authService.login(username, password);
        //将token写入cookie,并指定httpOnly为true，防止通过JS获取和修改
        CookieUtils.newBuilder(response).httpOnly().maxAge(jwtProperties.getCookieMaxage()).request(request).build(jwtProperties.getCookieName(),token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登入状态，获取用户名显示在前端
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response){

        try {
            UserInfo info = JwtUtils.getInfoFromToken(jwtProperties.getPublicKey(), token);
            //重新刷新token
            String newToken = JwtUtils.generateToken(info, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            //写入cookie
            CookieUtils.newBuilder(response).httpOnly().maxAge(jwtProperties.getCookieMaxage()).request(request).build(jwtProperties.getCookieName(),newToken);
            return ResponseEntity.ok(info);
        }catch (Exception e){
            //token已经过期，或者，token已经被篡改
            throw new LyException(ExceptionEnums.UN_AUTHORIZED);
        }
    }
}
