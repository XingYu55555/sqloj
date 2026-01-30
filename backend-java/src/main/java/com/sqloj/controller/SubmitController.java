package com.sqloj.controller;

import com.sqloj.service.SubmissionService;
import com.sqloj.util.Result;
import com.sqloj.vo.JudgeResultVO;
import com.sqloj.vo.SubmissionHistoryVO;
import com.sqloj.vo.SubmitResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提交控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/submit")
public class SubmitController {

    @Autowired
    private SubmissionService submissionService;

    /**
     * 提交SQL
     * POST /api/submit/sql
     */
    @PostMapping("/sql")
    public Result<SubmitResultVO> submit(@RequestBody SubmitRequest request) {
        try {
            SubmitResultVO vo = submissionService.submit(
                request.getStudentId(),
                request.getProblemId(),
                request.getSqlContent()
            );
            return Result.success(vo);
        } catch (Exception e) {
            log.error("提交SQL失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询判题结果
     * GET /api/submit/result/{submissionId}
     */
    @GetMapping("/result/{submissionId}")
    public Result<JudgeResultVO> result(@PathVariable Long submissionId) {
        try {
            JudgeResultVO vo = submissionService.getResult(submissionId);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("查询判题结果失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询学生提交历史
     * GET /api/submit/history/{studentId}
     */
    @GetMapping("/history/{studentId}")
    public Result<List<SubmissionHistoryVO>> history(@PathVariable Integer studentId) {
        try {
            List<SubmissionHistoryVO> list = submissionService.getHistory(studentId);
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询提交历史失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交请求参数
     */
    public static class SubmitRequest {
        private Integer studentId;
        private Integer problemId;
        private String sqlContent;

        public Integer getStudentId() {
            return studentId;
        }

        public void setStudentId(Integer studentId) {
            this.studentId = studentId;
        }

        public Integer getProblemId() {
            return problemId;
        }

        public void setProblemId(Integer problemId) {
            this.problemId = problemId;
        }

        public String getSqlContent() {
            return sqlContent;
        }

        public void setSqlContent(String sqlContent) {
            this.sqlContent = sqlContent;
        }
    }
}
