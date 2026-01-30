package com.sqloj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqloj.entity.Problem;
import com.sqloj.mapper.ProblemMapper;
import com.sqloj.vo.ProblemDetailVO;
import com.sqloj.vo.ProblemListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目服务
 */
@Slf4j
@Service
public class ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    /**
     * 查询所有题目列表
     */
    public List<ProblemListVO> list() {
        QueryWrapper<Problem> wrapper = new QueryWrapper<>();
        wrapper.eq("is_delete", 0);
        List<Problem> problems = problemMapper.selectList(wrapper);

        List<ProblemListVO> voList = new ArrayList<>();
        for (Problem problem : problems) {
            ProblemListVO vo = new ProblemListVO();
            BeanUtils.copyProperties(problem, vo);
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 根据ID查询题目详情
     */
    public ProblemDetailVO getById(Integer id) {
        Problem problem = problemMapper.selectById(id);
        if (problem == null || problem.getIsDelete() == 1) {
            throw new RuntimeException("题目不存在");
        }

        ProblemDetailVO vo = new ProblemDetailVO();
        BeanUtils.copyProperties(problem, vo);

        return vo;
    }

    /**
     * 新增题目
     */
    public Problem add(Problem problem) {
        problemMapper.insert(problem);
        return problem;
    }

    /**
     * 编辑题目
     */
    public Problem edit(Problem problem) {
        Problem existProblem = problemMapper.selectById(problem.getId());
        if (existProblem == null) {
            throw new RuntimeException("题目不存在");
        }

        problemMapper.updateById(problem);
        return problemMapper.selectById(problem.getId());
    }

    /**
     * 删除题目（软删除）
     */
    public void delete(Integer id) {
        Problem problem = problemMapper.selectById(id);
        if (problem == null) {
            throw new RuntimeException("题目不存在");
        }

        problemMapper.deleteById(id);
    }
}
