package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
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


    private String pubKeyPath;// 公钥
    private String CookieName;
    private Integer CookieMaxage;
    private PublicKey publicKey;

    //对象一旦实例化后，就应该读取公钥和私钥
    @PostConstruct //实例化后执行
    public void init() throws Exception {

        //读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);

    }
}
