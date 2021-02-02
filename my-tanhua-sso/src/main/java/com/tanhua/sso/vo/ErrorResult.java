package com.tanhua.sso.vo;

import lombok.Builder;
import lombok.Data;

@Data
//用于构造参数，用类名点获取build 点 属性名 在build
@Builder
public class ErrorResult {

    private String errCode;
    private String errMessage;
}
