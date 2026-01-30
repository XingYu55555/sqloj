package com.sqloj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置
 * 配置权限控制和密码加密
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离项目）
            .csrf().disable()
            // 配置请求授权
            .authorizeRequests()
                // 允许所有接口访问（在Controller层通过注解控制权限）
                .anyRequest().permitAll()
            .and()
            // 禁用session（使用无状态认证）
            .sessionManagement().disable()
            // 禁用HTTP Basic认证
            .httpBasic().disable()
            // 禁用表单登录
            .formLogin().disable();

        return http.build();
    }
}
