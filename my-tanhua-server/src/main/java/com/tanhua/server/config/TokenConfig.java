package com.tanhua.server.config;

import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *token 登录拦截    统一进行处理 token
 *
 */
@Component
public class TokenConfig  implements HandlerInterceptor {

    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断是否需要进行拦截 在不需要的 方

        //获取token

        //校验token


        //将查询到的 user 在放到 LocalThread 本地线程
        return false;
    }
}
