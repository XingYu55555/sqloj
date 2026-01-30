package com.sqloj.controller;

import com.sqloj.service.UserService;
import com.sqloj.util.Result;
import com.sqloj.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * POST /api/user/login
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody LoginRequest request) {
        try {
            UserLoginVO vo = userService.login(request.getUsername(), request.getPassword());
            return Result.success(vo);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * GET /api/user/info
     * 注意：实际项目中应该从session或token中获取当前用户ID
     * 这里简化处理，由前端传递userId参数
     */
    @GetMapping("/info")
    public Result<UserLoginVO> info(@RequestParam Integer userId) {
        try {
            UserLoginVO vo = userService.getUserById(userId);
            return Result.success(vo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 登录请求参数
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
