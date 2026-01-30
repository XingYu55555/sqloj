package com.sqloj.vo;

import lombok.Data;

/**
 * 判题结果响应VO
 */
@Data
public class JudgeResultVO {
    private String status;
    private Integer runningTime;
    private Integer lackNum;
    private Integer errNum;
    private String actualOutput;
}
