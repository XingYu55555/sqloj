package com.sqloj.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.*;
import java.util.*;

/**
 * JDBC工具类
 * 用于连接判题测试数据库（sqloj_test）
 */
@Slf4j
@Component
public class JdbcUtil {

    @Value("${judge.datasource.read.url}")
    private String readUrl;

    @Value("${judge.datasource.read.username}")
    private String readUsername;

    @Value("${judge.datasource.read.password}")
    private String readPassword;

    @Value("${judge.datasource.write.url}")
    private String writeUrl;

    @Value("${judge.datasource.write.username}")
    private String writeUsername;

    @Value("${judge.datasource.write.password}")
    private String writePassword;

    private static DruidDataSource readDataSource;
    private static DruidDataSource writeDataSource;

    @PostConstruct
    public void init() {
        // 初始化只读数据源
        readDataSource = new DruidDataSource();
        readDataSource.setUrl(readUrl);
        readDataSource.setUsername(readUsername);
        readDataSource.setPassword(readPassword);
        readDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        readDataSource.setInitialSize(2);
        readDataSource.setMinIdle(2);
        readDataSource.setMaxActive(10);
        readDataSource.setMaxWait(60000);

        // 初始化写数据源
        writeDataSource = new DruidDataSource();
        writeDataSource.setUrl(writeUrl);
        writeDataSource.setUsername(writeUsername);
        writeDataSource.setPassword(writePassword);
        writeDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        writeDataSource.setInitialSize(2);
        writeDataSource.setMinIdle(2);
        writeDataSource.setMaxActive(10);
        writeDataSource.setMaxWait(60000);

        log.info("JDBC连接池初始化完成");
    }

    @PreDestroy
    public void destroy() {
        if (readDataSource != null) {
            readDataSource.close();
        }
        if (writeDataSource != null) {
            writeDataSource.close();
        }
        log.info("JDBC连接池已关闭");
    }

    /**
     * 执行查询SQL（只读模式）
     * @param sql SQL语句
     * @return 查询结果
     */
    public static List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = readDataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            return resultSetToList(rs);
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    /**
     * 执行更新SQL（写模式，带事务回滚）
     * @param sql SQL语句
     * @return 查询结果（用于判题）
     */
    public static List<Map<String, Object>> executeUpdateWithRollback(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = writeDataSource.getConnection();
            conn.setAutoCommit(false); // 开启事务

            stmt = conn.createStatement();
            
            // 执行SQL
            boolean isQuery = stmt.execute(sql);
            
            List<Map<String, Object>> result = new ArrayList<>();
            if (isQuery) {
                rs = stmt.getResultSet();
                result = resultSetToList(rs);
            }

            // 强制回滚，防止污染数据
            conn.rollback();

            return result;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    log.error("回滚失败", ex);
                }
            }
            throw e;
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    /**
     * 将ResultSet转换为List<Map>
     */
    private static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }
            list.add(row);
        }

        return list;
    }

    /**
     * 关闭资源
     */
    private static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("关闭ResultSet失败", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("关闭Statement失败", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("关闭Connection失败", e);
            }
        }
    }

    /**
     * 判断SQL是否为查询语句
     */
    public static boolean isQuerySql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        String trimmedSql = sql.trim().toUpperCase();
        return trimmedSql.startsWith("SELECT");
    }

    /**
     * 判断SQL是否为更新语句
     */
    public static boolean isUpdateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        String trimmedSql = sql.trim().toUpperCase();
        return trimmedSql.startsWith("INSERT") || 
               trimmedSql.startsWith("UPDATE") || 
               trimmedSql.startsWith("DELETE");
    }
}
