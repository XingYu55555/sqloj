package com.sqloj.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 题目实体类
 */
@Data
@TableName("problem")
public class Problem {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String title;
    
    private String description;
    
    private Integer teacherId;
    
    private String stdSql;
    
    private String stdHeaders;
    
    private String stdData;
    
    private String difficulty; // EASY, MEDIUM, HARD
    
    @TableLogic
    private Integer isDelete;
}
