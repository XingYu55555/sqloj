package com.sqloj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqloj.entity.Submission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提交记录Mapper
 */
@Mapper
public interface SubmissionMapper extends BaseMapper<Submission> {
}
