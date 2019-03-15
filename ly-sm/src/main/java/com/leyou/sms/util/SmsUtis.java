package com.leyou.sms.util;

import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtis{
    @Autowired
    private SmsProperties properties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final String KEY_PHOME="sms_phone:";

    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    /**
     *
     * @param phoneNumber 号码
     * @param templateCode 短信模板ID
     * @param templateParam 发送信息代码
     */
    public  void mobileQuery(String phoneNumber,String templateCode,String templateParam){
        //号码用于redis中
        String key=KEY_PHOME + phoneNumber;
        //按照手机号码限流
        //读取时间
        String lastTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(lastTime)){
            Long last = Long.valueOf(lastTime);
            if (System.currentTimeMillis()-last<60000){
                log.info("[短息服务] 发送短信频率过高，被拦截，手机号码：{}",phoneNumber);
                return ;
            }
        }
        String result =null;
        String url =properties.getUrl();//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("mobile",phoneNumber);//接受短信的用户手机号码
        params.put("tpl_id",templateCode);//您申请的短信模板ID，根据实际情况修改
        params.put("tpl_value","#code#="+templateParam);//您设置的模板变量，根据实际情况修改("#code#=123456")
        params.put("key",properties.getAccessKeySecret());//应用APPKEY(应用详细页查询)
        try {
            result = net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                System.out.println(object.get("result"));
                /**
                 * 发送短信成功后将手机存入redis中,String.valueOf(System.currentTimeMillis())是系统时间
                 */
                redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),1, TimeUnit.MILLISECONDS);
                log.info("[短信服务] 发送验证码，手机号：{}",phoneNumber);
            }else{
                //控制台输出
                System.out.println("[短信服务] 发送失败，phoneNumber:" + phoneNumber + ",返回码" + object.get("error_code") + "，原因" + object.get("reason"));
                //日志记录
                log.info("[短信服务] 发送失败，phoneNumber:" + phoneNumber + ",返回码:" + object.get("error_code") + "，原因:" + object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        /**
         *
         * @param strUrl 请求地址
         * @param params 请求参数
         * @param method 请求方法
         * @return  网络请求字符串
         * @throws Exception
         */
    public static String net(String strUrl, Map params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }
    //将map型转为请求参数型
    public static String urlencode(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
