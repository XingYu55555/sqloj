package com.sqloj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqloj.entity.Problem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目Mapper
 */
@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
}
