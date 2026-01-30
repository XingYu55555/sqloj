package com.sqloj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sqloj.entity.User;
import com.sqloj.mapper.UserMapper;
import com.sqloj.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 用户服务
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    public UserLoginVO login(String username, String password) {
        // 查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 构建返回对象
        UserLoginVO vo = new UserLoginVO();
        BeanUtils.copyProperties(user, vo);
        
        if (user.getCreateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            vo.setCreateTime(user.getCreateTime().format(formatter));
        }

        return vo;
    }

    /**
     * 根据ID获取用户信息
     */
    public UserLoginVO getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserLoginVO vo = new UserLoginVO();
        BeanUtils.copyProperties(user, vo);
        
        if (user.getCreateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            vo.setCreateTime(user.getCreateTime().format(formatter));
        }

        return vo;
    }
}
