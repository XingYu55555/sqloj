package com.sqloj.service;

import com.alibaba.fastjson.JSON;
import com.sqloj.entity.JudgeDetail;
import com.sqloj.entity.Problem;
import com.sqloj.entity.Submission;
import com.sqloj.mapper.JudgeDetailMapper;
import com.sqloj.mapper.ProblemMapper;
import com.sqloj.mapper.SubmissionMapper;
import com.sqloj.util.JdbcUtil;
import com.sqloj.util.NormalizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 判题服务
 * 核心判题逻辑：独立连接+事务回滚+超时控制+标准化比对
 */
@Slf4j
@Service
public class JudgeService {

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private JudgeDetailMapper judgeDetailMapper;

    @Value("${judge.timeout:3000}")
    private int timeout;

    /**
     * 执行判题任务
     */
    public void judge(Long submissionId) {
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            log.error("提交记录不存在: {}", submissionId);
            return;
        }

        Problem problem = problemMapper.selectById(submission.getProblemId());
        if (problem == null) {
            log.error("题目不存在: {}", submission.getProblemId());
            updateSubmissionStatus(submissionId, "RE", 0);
            return;
        }

        // 创建判题任务
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<JudgeResult> future = executor.submit(() -> executeJudge(submission, problem));

        try {
            // 设置超时时间
            JudgeResult result = future.get(timeout, TimeUnit.MILLISECONDS);

            // 更新提交记录
            updateSubmissionStatus(submissionId, result.getStatus(), result.getRunningTime());

            // 保存判题详情
            saveJudgeDetail(submissionId, result);

        } catch (TimeoutException e) {
            log.error("判题超时: {}", submissionId);
            updateSubmissionStatus(submissionId, "TLE", timeout);
            future.cancel(true);
        } catch (Exception e) {
            log.error("判题异常: {}", submissionId, e);
            updateSubmissionStatus(submissionId, "RE", 0);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 执行判题核心逻辑
     */
    private JudgeResult executeJudge(Submission submission, Problem problem) {
        JudgeResult result = new JudgeResult();
        long startTime = System.currentTimeMillis();

        try {
            // 执行学生SQL
            List<Map<String, Object>> actualResult;
            String sql = submission.getSqlContent().trim();

            if (JdbcUtil.isQuerySql(sql)) {
                // 查询语句，使用只读账号
                actualResult = JdbcUtil.executeQuery(sql);
            } else if (JdbcUtil.isUpdateSql(sql)) {
                // 更新语句，使用写账号并回滚
                actualResult = JdbcUtil.executeUpdateWithRollback(sql);
            } else {
                // 其他语句（DDL等），不支持
                result.setStatus("RE");
                result.setRunningTime(0);
                return result;
            }

            long endTime = System.currentTimeMillis();
            result.setRunningTime((int) (endTime - startTime));

            // 标准化实际结果
            List<Map<String, Object>> normalizedActual = NormalizeUtil.normalizeFromList(actualResult);

            // 标准化标准答案
            String[] headers = problem.getStdHeaders().split(",");
            List<Map<String, Object>> normalizedStandard = NormalizeUtil.normalize(problem.getStdData(), headers);

            // 比对结果
            boolean isCorrect = NormalizeUtil.equals(normalizedStandard, normalizedActual);

            if (isCorrect) {
                result.setStatus("AC");
                result.setLackNum(0);
                result.setErrNum(0);
            } else {
                result.setStatus("WA");
                // 计算差异
                int[] diff = NormalizeUtil.calculateDifference(normalizedStandard, normalizedActual);
                result.setLackNum(diff[0]);
                result.setErrNum(diff[1]);
            }

            // 保存实际输出
            result.setActualOutput(JSON.toJSONString(actualResult));

        } catch (SQLException e) {
            log.error("SQL执行错误", e);
            result.setStatus("RE");
            result.setRunningTime((int) (System.currentTimeMillis() - startTime));
        } catch (Exception e) {
            log.error("判题异常", e);
            result.setStatus("RE");
            result.setRunningTime((int) (System.currentTimeMillis() - startTime));
        }

        return result;
    }

    /**
     * 更新提交记录状态
     */
    private void updateSubmissionStatus(Long submissionId, String status, int runningTime) {
        Submission submission = new Submission();
        submission.setId(submissionId);
        submission.setJudgeStatus(status);
        submission.setRunningTime(runningTime);
        submissionMapper.updateById(submission);
    }

    /**
     * 保存判题详情
     */
    private void saveJudgeDetail(Long submissionId, JudgeResult result) {
        JudgeDetail detail = new JudgeDetail();
        detail.setSubmissionId(submissionId);
        detail.setLackNum(result.getLackNum());
        detail.setErrNum(result.getErrNum());
        detail.setActualOutput(result.getActualOutput());
        judgeDetailMapper.insert(detail);
    }

    /**
     * 判题结果内部类
     */
    private static class JudgeResult {
        private String status;
        private Integer runningTime;
        private Integer lackNum;
        private Integer errNum;
        private String actualOutput;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getRunningTime() {
            return runningTime;
        }

        public void setRunningTime(Integer runningTime) {
            this.runningTime = runningTime;
        }

        public Integer getLackNum() {
            return lackNum;
        }

        public void setLackNum(Integer lackNum) {
            this.lackNum = lackNum;
        }

        public Integer getErrNum() {
            return errNum;
        }

        public void setErrNum(Integer errNum) {
            this.errNum = errNum;
        }

        public String getActualOutput() {
            return actualOutput;
        }

        public void setActualOutput(String actualOutput) {
            this.actualOutput = actualOutput;
        }
    }
}
