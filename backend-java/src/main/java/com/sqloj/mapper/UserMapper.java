package com.sqloj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqloj.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
