package com.tanhua.sso.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.sso.mapper.UserMapper;
import com.tanhua.sso.pojo.User;
import com.tanhua.sso.vo.VerificationCodeVO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret}")
    private String secret;

    public User queryUserByToken(String token) {
        try {
            // 通过token解析数据
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            User user = new User();
            user.setId(Long.valueOf(body.get("id").toString()));

            //获取手机号，因 查询token 很频繁 获取手机号应放在redis中
            String redisKey = "TANHUA_USER_MOBILE_" + user.getId();
            String mobile = this.redisTemplate.opsForValue().get(redisKey);
            if (mobile!=null){
                user.setMobile(mobile);
            }else {
                User user1 = this.userMapper.selectById(Long.valueOf(body.get("id").toString()));
                user.setMobile(user1.getMobile());
                //存 1 天
                this.redisTemplate.opsForValue().set(redisKey,user1.getMobile(),1, TimeUnit.DAYS);
            }

            return user;
        }  catch (ExpiredJwtException e) {
            log.info("token已经过期！ token = " + token);
        } catch (Exception e) {
            log.error("token不合法！ token = "+ token, e);
        }
        return null;
    }

    public Map<String, Object> login(VerificationCodeVO verificationCodeVO) {
        String redisKey = "CHECK_CODE_" + verificationCodeVO.getPhone();

        String res = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotBlank(res) && StringUtils.equals(verificationCodeVO.getVerificationCode(), res)) {
            //相等说明有了，可以删了
            this.redisTemplate.delete(redisKey);

            Boolean isNew = false;
            QueryWrapper<User> warpper = new QueryWrapper<>();
            warpper.eq("mobile", verificationCodeVO.getPhone());
            User user = userMapper.selectOne(warpper);


            if (user == null) {
                //表示新用户，要注册,保存到数据库
                user=new User();
                user.setMobile(verificationCodeVO.getPhone());
                user.setPassword(DigestUtils.md5Hex("123456"));
                userMapper.insert(user);
                isNew = true;
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("id",user.getId());            // 生成token

            String token = Jwts.builder()
                    .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                    .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                    .setExpiration(new DateTime().plusHours(12).toDate()) //设置过期时间，12小时后过期
                    .compact();

            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("isNew", isNew);

            //为了不影响主业务，这里只捕获异常  暂时毫不清楚为啥要发送MQ
            try {
                this.rocketMQTemplate.convertAndSend("tanhua-sso-login", map);
            } catch (MessagingException e) {
                log.error("发送消息失败", e);
            }
            return map;
        }
        return null;
    }
}
