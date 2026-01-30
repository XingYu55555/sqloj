package com.sqloj.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 判题详情实体类
 */
@Data
@TableName("judge_detail")
public class JudgeDetail {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long submissionId;
    
    private Integer lackNum;
    
    private Integer errNum;
    
    private String actualOutput;
}
