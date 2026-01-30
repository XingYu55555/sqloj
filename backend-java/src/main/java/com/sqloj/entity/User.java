package com.sqloj.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String username;
    
    private String password;
    
    private String role; // STUDENT, TEACHER, ADMIN
    
    private String email;
    
    @TableLogic
    private Integer isDelete;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
