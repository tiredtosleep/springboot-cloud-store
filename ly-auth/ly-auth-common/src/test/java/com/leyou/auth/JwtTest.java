package com.leyou.auth;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;


import java.security.PrivateKey;
import java.security.PublicKey;


public class JwtTest {

    private static final String pubKeyPath = "D:\\heima\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\heima\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 执行生成秘钥
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 执行生成token
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 将上述生成的token解析
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU0NzM1NDU3OH0.NQWMLXSMnslvNNc5IYtI6flzpqlY_YaXyrNzdqEbWRvMeKQAE-mjCX8dF5T9DUwVjWaAqSc3g0NSp2K6an9LkLNVji_44YuNa1OsJ1Fbsk3SEHNm93Bl_5Vx2yMVYxq3eh_nQ4LqytXEFus24Q8N1VCt-zbNqB3_f8R2Q-PmoZI";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken( publicKey,token);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getName());
    }
}
