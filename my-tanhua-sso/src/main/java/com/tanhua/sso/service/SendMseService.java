package com.tanhua.sso.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.tanhua.sso.config.AliyunSMSConfig;
import com.tanhua.sso.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class SendMseService {

    @Autowired
    private AliyunSMSConfig aliyunSMSConfig;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 发送短信验证码
     *
     * @param mobile
     * @return
     */
    public String sendSms(String mobile) {
        DefaultProfile profile = DefaultProfile.getProfile(this.aliyunSMSConfig.getRegionId(),
                this.aliyunSMSConfig.getAccessKeyId(), this.aliyunSMSConfig.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        String code = RandomUtils.nextInt(100000, 999999) + "";

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(this.aliyunSMSConfig.getDomain());
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", this.aliyunSMSConfig.getRegionId());
        request.putQueryParameter("PhoneNumbers", mobile); //目标手机号
        request.putQueryParameter("SignName", this.aliyunSMSConfig.getSignName()); //签名名称
        request.putQueryParameter("TemplateCode", this.aliyunSMSConfig.getTemplateCode()); //短信模板code
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");//模板中变量替换
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            if (StringUtils.contains(data, "\"Message\":\"OK\"")) {
                return code;
            }
            log.info("发送短信验证码失败~ data = " + data);
        } catch (Exception e) {
            log.error("发送短信验证码失败~ mobile = " + mobile, e);
        }
        return null;
    }


    public ErrorResult sendMse(String phone) {
        String redisKey = "CHECK_CODE_" + phone;
        if (this.redisTemplate.hasKey(redisKey)) {
            return ErrorResult.builder().errMessage("上一次发送的验证码还未失效").errCode("0001").build();
        }
        //发送验证码
        //String code = this.sendSms(phone);
        String code = "123456";
        if (StringUtils.isBlank(code)) {
            return ErrorResult.builder().errMessage("发送失败").errCode("0002").build();

        }

        //短信发送成功，将验证码保存到redis中，有效期为5分钟
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));

        return null;



    }
}
