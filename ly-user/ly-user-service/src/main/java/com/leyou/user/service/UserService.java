package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_CODE="user:code:phone:";

    public Boolean checkData(String data, Integer type) {
        User user = new User();
        //判断数据类型用switch
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnums.INVALID_DATA_TYPE);
        }
        return userMapper.selectCount(user)==0;
    }

    public void sendCode(String phone) {
        //随机生成六位数字
        String code= NumberUtils.generateCode(6);
        String  key=KEY_CODE+phone;
        try {
        HashMap<Object, Object> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        //发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);

        } catch (Exception e) {
            log.error("发送短信失败。phone：{}， code：{}", phone, code);

        }
    }

    public void register(User user, String code) {
        String key=KEY_CODE+user.getPhone();

        //redis中取code
        String redisCode = redisTemplate.opsForValue().get(key);
        //校验验证码
        if (!StringUtils.equals(code,redisCode)){
            throw new LyException(ExceptionEnums.INVALID_USER_CODE);
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        user.setCreated(new Date());
        user.setId(null);
        //写入数据库
        int insert = userMapper.insert(user);
        //注册成功删除code的验证码
        if (insert==1){
            redisTemplate.delete(key);
        }
    }

    public User queryUser(String username, String password) {
        //查询用户
        User recode = new User();
        recode.setUsername(username);
        User user = userMapper.selectOne(recode);
        //校验
        if (user==null){
            throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }
        //校验密码和加密是否相等
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password,user.getSalt()))) {
            throw new LyException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }
        return user;
    }
}
