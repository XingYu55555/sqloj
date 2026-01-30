package com.sqloj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqloj.entity.JudgeDetail;
import com.sqloj.entity.Submission;
import com.sqloj.mapper.JudgeDetailMapper;
import com.sqloj.mapper.SubmissionMapper;
import com.sqloj.vo.JudgeResultVO;
import com.sqloj.vo.SubmissionHistoryVO;
import com.sqloj.vo.SubmitResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 提交服务
 */
@Slf4j
@Service
public class SubmissionService {

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private JudgeDetailMapper judgeDetailMapper;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private Executor judgeExecutor;

    /**
     * 提交SQL
     */
    public SubmitResultVO submit(Integer studentId, Integer problemId, String sqlContent) {
        // 创建提交记录
        Submission submission = new Submission();
        submission.setStudentId(studentId);
        submission.setProblemId(problemId);
        submission.setSqlContent(sqlContent);
        submission.setJudgeStatus("PENDING");
        submission.setRunningTime(0);

        submissionMapper.insert(submission);

        // 异步执行判题
        judgeExecutor.execute(() -> judgeService.judge(submission.getId()));

        // 返回提交结果
        SubmitResultVO vo = new SubmitResultVO();
        vo.setSubmissionId(submission.getId());
        vo.setStatus("PENDING");
        
        if (submission.getSubmitTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            vo.setSubmitTime(submission.getSubmitTime().format(formatter));
        }

        return vo;
    }

    /**
     * 查询判题结果
     */
    public JudgeResultVO getResult(Long submissionId) {
        Submission submission = submissionMapper.selectById(submissionId);
        if (submission == null) {
            throw new RuntimeException("提交记录不存在");
        }

        JudgeResultVO vo = new JudgeResultVO();
        vo.setStatus(submission.getJudgeStatus());
        vo.setRunningTime(submission.getRunningTime());

        // 查询判题详情
        if (!"PENDING".equals(submission.getJudgeStatus())) {
            QueryWrapper<JudgeDetail> wrapper = new QueryWrapper<>();
            wrapper.eq("submission_id", submissionId);
            JudgeDetail detail = judgeDetailMapper.selectOne(wrapper);

            if (detail != null) {
                vo.setLackNum(detail.getLackNum());
                vo.setErrNum(detail.getErrNum());
                vo.setActualOutput(detail.getActualOutput());
            }
        }

        return vo;
    }

    /**
     * 查询学生提交历史
     */
    public List<SubmissionHistoryVO> getHistory(Integer studentId) {
        QueryWrapper<Submission> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        wrapper.eq("is_delete", 0);
        wrapper.orderByDesc("submit_time");

        List<Submission> submissions = submissionMapper.selectList(wrapper);

        List<SubmissionHistoryVO> voList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Submission submission : submissions) {
            SubmissionHistoryVO vo = new SubmissionHistoryVO();
            BeanUtils.copyProperties(submission, vo);
            vo.setStatus(submission.getJudgeStatus());
            
            if (submission.getSubmitTime() != null) {
                vo.setSubmitTime(submission.getSubmitTime().format(formatter));
            }

            voList.add(vo);
        }

        return voList;
    }
}
