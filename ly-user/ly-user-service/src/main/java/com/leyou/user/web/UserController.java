package com.leyou.user.web;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据
     * @param data 数据
     * @param type 要校验的数据类型：1，用户名；2，手机；
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data,
                                             @PathVariable("type")Integer type){
        return ResponseEntity.ok(userService.checkData(data,type));
    }
    /**
     * 发送手机生成验证码
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone){
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code")String code){
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public  ResponseEntity<User> queryUser(@RequestParam("username")String username,
                                            @RequestParam("password")String password){
        return ResponseEntity.ok(userService.queryUser(username,password));
    }

    
}