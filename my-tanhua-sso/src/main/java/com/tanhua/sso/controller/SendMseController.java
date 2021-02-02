package com.tanhua.sso.controller;

import com.tanhua.sso.dto.MessageDto;
import com.tanhua.sso.service.SendMseService;
import com.tanhua.sso.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@Slf4j
public class SendMseController {


    @Autowired
    private SendMseService sendMseService;

    @GetMapping("sendCode")
    public ResponseEntity<ErrorResult> sendCode(@RequestBody MessageDto messageDto) {

        String phone = messageDto.getPhone();
        ErrorResult result = null;
        try {
            result = sendMseService.sendMse(phone);
            if (result == null) {
                return ResponseEntity.ok(null);
            }
        } catch (Exception e) {
            log.error("发送短信验证码失败~ phone = " + phone, e);
            result = ErrorResult.builder().errCode("02222").errMessage("发送短信验证码失败").build();
            e.printStackTrace();

        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);


    }
}
