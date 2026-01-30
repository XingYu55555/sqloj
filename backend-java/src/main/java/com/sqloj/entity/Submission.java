package com.sqloj.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提交记录实体类
 */
@Data
@TableName("submission")
public class Submission {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Integer studentId;
    
    private Integer problemId;
    
    private String sqlContent;
    
    private String judgeStatus; // PENDING, AC, WA, TLE, RE
    
    private Integer runningTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime submitTime;
    
    @TableLogic
    private Integer isDelete;
}
