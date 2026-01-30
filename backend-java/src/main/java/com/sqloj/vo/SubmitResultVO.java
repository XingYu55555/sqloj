package com.sqloj.vo;

import lombok.Data;

/**
 * 提交SQL响应VO
 */
@Data
public class SubmitResultVO {
    private Long submissionId;
    private String status;
    private String submitTime;
}
