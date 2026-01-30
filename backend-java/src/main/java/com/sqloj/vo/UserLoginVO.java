package com.sqloj.vo;

import lombok.Data;

/**
 * 用户登录响应VO
 */
@Data
public class UserLoginVO {
    private Integer id;
    private String username;
    private String role;
    private String createTime;
}
