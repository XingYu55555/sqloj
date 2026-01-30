package com.sqloj.vo;

import lombok.Data;

/**
 * 题目详情响应VO
 */
@Data
public class ProblemDetailVO {
    private Integer id;
    private String title;
    private String description;
    private String stdHeaders;
    private String stdData;
    private String difficulty;
    private Integer teacherId;
}
