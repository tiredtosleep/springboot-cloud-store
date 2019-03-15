package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String secret; // 密钥

    private String pubKeyPath;// 公钥

    private String priKeyPath;// 私钥

    private int expire;// token过期时间
    private String CookieName;
    private Integer CookieMaxage;
    private PrivateKey privateKey;

    private PublicKey publicKey;


    //对象一旦实例化后，就应该读取公钥和私钥
    @PostConstruct //实例化后执行
    public void init() throws Exception {
        //公钥私钥如果不存在，先生成
        File pubPath = new File(pubKeyPath);
        File priPath = new File(priKeyPath);
        if (!pubPath.exists()||!priPath.exists()){
            //生成公钥私钥
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }

        //读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);


    }
}
