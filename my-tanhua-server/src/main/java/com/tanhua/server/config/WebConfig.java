package com.tanhua.server.config;

import com.tanhua.server.interceptor.RedisCacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig  implements WebMvcConfigurer {

    @Autowired
    private RedisCacheInterceptor redisCacheInterceptor;

    @Autowired
    private TokenConfig tokenConfig;

    /**
     *
     *
     *将自己写的拦截器 添加到 mvc配置类中
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //先进行拦截校验 才做其他操作 有先后的拦截顺序
        registry.addInterceptor(this.tokenConfig);
        registry.addInterceptor(this.redisCacheInterceptor).addPathPatterns("/**");
    }

}
