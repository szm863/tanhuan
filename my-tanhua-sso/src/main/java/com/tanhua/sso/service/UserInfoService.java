package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.sso.enmus.SexEnum;
import com.tanhua.sso.mapper.UserInfoMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.pojo.UserInfo;
import com.tanhua.sso.vo.PicUploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class UserInfoService {


    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private FaceEngineService faceEngineService;

    @Autowired
    private PicUploadService picUploadService;

    //发送验证码以后
    public Boolean saveUserInfo(Map<String,String> param, String token) {

        // 1 判断token是否存在
       User user= userService.queryUserByToken(token);
       if (user==null){
           return false;
       }
        //  2 保存结果
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setSex(StringUtils.equalsIgnoreCase(param.get("gender"), "man") ? SexEnum.MAN : SexEnum.WOMAN);
        userInfo.setNickName(param.get("nickname"));
        userInfo.setBirthday(param.get("birthday"));

        return this.userInfoMapper.insert(userInfo) == 1;
    }

    public Boolean saveUserLogo(MultipartFile file, String token) throws IOException {
        // 1 判断token是否存在
        User user= userService.queryUserByToken(token);
        if (user==null){
            log.error("用户不存在");
            return false;
        }
        if (file==null){
            log.error("头像不存在");
            return false;
        }

        boolean b = faceEngineService.checkIsPortrait(file.getBytes());
        if (!b){
            log.error("不是人");
            return false;
        }
        //上传阿里云
        PicUploadResult uplod = picUploadService.uplod(file);
        if (!StringUtils.isNoneEmpty(uplod.getName())) {
            return false;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setLogo(uplod.getName());

        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId,user.getId());
        return userInfoMapper.update(userInfo,wrapper)==1;
    }
}
