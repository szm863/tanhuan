package com.tanhua.server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.server.utils.Cache;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class RedisCacheInterceptor implements HandlerInterceptor {


    @Value("${tanhua.cache.enable}")
    private Boolean enable;


    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 返回false 表示不释放
     * <p>
     * 前置处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //检测是否开启全局缓存
        if (!enable) {
            return true;
        }
        //HandlerMethod 包含了前端发送的 很多下面的请求 url，参数等
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //判断是否是get请求 ，只对get请求拦截
        if (!((HandlerMethod)handler).hasMethodAnnotation(GetMapping.class)){
            return true;
        }

        //判断是否添加了@Cache注解
        if (!((HandlerMethod)handler).hasMethodAnnotation(Cache.class)){
            return true;
        }

        String redisKey = createRedisKey((HttpServletRequest) request);

        String result = redisTemplate.opsForValue().get(redisKey);
        if(StringUtils.isBlank(result)){
            return true;
        }

        // 将data数据进行响应
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(result);

        return true;


    }


    /**
     * 生成redis中的key，规则：SERVER_CACHE_DATA_MD5(url + param + token)
     *
     * @param request
     * @return
     */
    public static String createRedisKey(HttpServletRequest request) throws Exception {
        String url = request.getRequestURI();
        String param = MAPPER.writeValueAsString(request.getParameterMap());
        String token = request.getHeader("Authorization");

        String data = url + "_" + param + "_" + token;
        return "SERVER_CACHE_DATA_" + DigestUtils.md5Hex(data);
    }
}
