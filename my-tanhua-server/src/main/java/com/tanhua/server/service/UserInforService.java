package com.tanhua.server.service;

import com.tanhua.server.mapper.UserInforMapper;
import com.tanhua.server.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInforService {

    @Autowired
    private UserInforMapper userInforMapper;

    public UserInfo queryUserInfoByUserId(Long id) {
        UserInfo userInfo = userInforMapper.selectById(id);

        return userInfo;
    }
}
