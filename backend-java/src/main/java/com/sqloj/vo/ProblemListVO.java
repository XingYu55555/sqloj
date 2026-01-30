package com.sqloj.vo;

import lombok.Data;

/**
 * 题目列表响应VO
 */
@Data
public class ProblemListVO {
    private Integer id;
    private String title;
    private String description;
    private String difficulty;
    private Integer teacherId;
}
