package com.sqloj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SQLOJ Backend Application
 * 数据库课程在线实践平台 - Java后端
 */
@SpringBootApplication
@MapperScan("com.sqloj.mapper")
public class SqlojApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqlojApplication.class, args);
    }
}
