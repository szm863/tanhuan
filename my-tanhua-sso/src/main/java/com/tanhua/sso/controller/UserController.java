package com.tanhua.sso.controller;

import com.tanhua.sso.pojo.User;
import com.tanhua.sso.service.UserService;
import com.tanhua.sso.vo.ErrorResult;
import com.tanhua.sso.vo.VerificationCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param
     * @return
     */
    @PostMapping("loginVerification")
    public ResponseEntity<Object> login(@RequestBody VerificationCodeVO verificationCodeVO) {

        Map<String,Object> map= null;
        try {
            map = userService.login(verificationCodeVO);
            if (map==null){
                ErrorResult errorResult = ErrorResult.builder().errCode("000002").errMessage("登录失败！").build();
              return   ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(map);
    }



    @GetMapping("{token}")
    public User queryUserByToken(@PathVariable String token){
        return  userService.queryUserByToken(token);

    }
}