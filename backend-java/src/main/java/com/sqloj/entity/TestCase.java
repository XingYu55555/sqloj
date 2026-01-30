package com.sqloj.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 测试用例实体类
 */
@Data
@TableName("test_case")
public class TestCase {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer problemId;
    
    private String outputData;
    
    private Integer isPublic;
}
