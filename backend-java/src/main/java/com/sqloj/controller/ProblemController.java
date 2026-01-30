package com.sqloj.controller;

import com.sqloj.entity.Problem;
import com.sqloj.service.ProblemService;
import com.sqloj.util.Result;
import com.sqloj.vo.ProblemDetailVO;
import com.sqloj.vo.ProblemListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    /**
     * 查询所有题目列表
     * GET /api/problem/list
     */
    @GetMapping("/list")
    public Result<List<ProblemListVO>> list() {
        try {
            List<ProblemListVO> list = problemService.list();
            return Result.success(list);
        } catch (Exception e) {
            log.error("查询题目列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询题目详情
     * GET /api/problem/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public Result<ProblemDetailVO> detail(@PathVariable Integer id) {
        try {
            ProblemDetailVO vo = problemService.getById(id);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("查询题目详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增题目
     * POST /api/problem/add
     */
    @PostMapping("/add")
    public Result<Problem> add(@RequestBody Problem problem) {
        try {
            Problem result = problemService.add(problem);
            return Result.success(result);
        } catch (Exception e) {
            log.error("新增题目失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 编辑题目
     * PUT /api/problem/edit
     */
    @PutMapping("/edit")
    public Result<Problem> edit(@RequestBody Problem problem) {
        try {
            Problem result = problemService.edit(problem);
            return Result.success(result);
        } catch (Exception e) {
            log.error("编辑题目失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除题目
     * DELETE /api/problem/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            problemService.delete(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除题目失败", e);
            return Result.error(e.getMessage());
        }
    }
}
