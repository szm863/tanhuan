package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.server.mapper.UserInforMapper;
import com.tanhua.server.pojo.User;
import com.tanhua.server.pojo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserInforMapper userInforMapper;

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Value("${tanhua.sso.url}")
    private String url;


    public User queryToken(String token) {
        //返回的是json字符串
        String result = restTemplate.getForObject(url + "/user/" + token, String.class);
        if (StringUtils.isBlank(result)) {
            return null;
        }

        User user=null;
        try {
             user = MAPPER.readValue(result, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }


    public List<UserInfo> queryRecommendation(LambdaQueryWrapper<UserInfo> wapper) {

        return userInforMapper.selectList(wapper);
    }
}
