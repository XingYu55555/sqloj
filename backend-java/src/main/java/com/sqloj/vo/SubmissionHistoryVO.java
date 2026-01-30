package com.sqloj.vo;

import lombok.Data;

/**
 * 提交历史响应VO
 */
@Data
public class SubmissionHistoryVO {
    private Long id;
    private Integer problemId;
    private String sqlContent;
    private String status;
    private String submitTime;
    private Integer runningTime;
}
