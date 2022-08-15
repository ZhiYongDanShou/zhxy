package com.atguigu.zhxy.pojo;

import lombok.Data;

/**
 * @author ysh
 * @create 2022-04-14 16:22
 */
@Data
public class LoginForm {

    private String username;
    private String password;
    private String verifiCode;
    private Integer userType;
}
