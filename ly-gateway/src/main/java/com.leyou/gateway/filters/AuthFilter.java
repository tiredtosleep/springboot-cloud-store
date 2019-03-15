package com.leyou.gateway.filters;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter{
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;//过滤器类型，前置过滤
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;//过滤器顺序
    }

    /**
     * 放行逻辑
     * true：过滤
     * false：不过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取请求的url路径
        String requestURL = request.getRequestURI();
        //判断是否放行，否则返回false
        boolean isAllowPath=isAllowPath(requestURL);

        return !isAllowPath;
    }


    /**
     * 判断路径
     * @param requestURL
     * @return
     */
    private Boolean isAllowPath(String requestURL) {
        for (String allowPath : filterProperties.getAllowPaths()) {
            //判断路径是否相等
            if (requestURL.startsWith(allowPath)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //解析token
            UserInfo userInfo = JwtUtils.getInfoFromToken(jwtProperties.getPublicKey(), token);
            //todo 校验权限
        }catch (Exception e){
            //解析token失败，未登入，拦截
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
        }

        return null;
    }
}
