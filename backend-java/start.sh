#!/bin/bash

# SQLOJ Java后端快速启动脚本
# 用于快速启动后端服务

echo "======================================"
echo "  SQLOJ Java后端快速启动"
echo "======================================"
echo ""

# 检查Java环境
echo "1. 检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未检测到Java环境，请先安装JDK 1.8+"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "   Java版本: $JAVA_VERSION"
echo ""

# 检查Maven环境
echo "2. 检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "错误: 未检测到Maven，请先安装Maven 3.6+"
    exit 1
fi

MVN_VERSION=$(mvn -version | head -n 1)
echo "   $MVN_VERSION"
echo ""

# 检查MySQL连接
echo "3. 检查MySQL连接..."
if ! command -v mysql &> /dev/null; then
    echo "警告: 未检测到MySQL客户端，请确保MySQL已安装并正在运行"
else
    echo "   MySQL已安装"
fi
echo ""

# 编译打包
echo "4. 编译打包项目..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "错误: 编译失败，请检查错误信息"
    exit 1
fi
echo "   编译成功"
echo ""

# 启动服务
echo "5. 启动后端服务..."
echo "   服务地址: http://localhost:8080"
echo "   API基准路径: http://localhost:8080/api"
echo ""
echo "按Ctrl+C停止服务"
echo "======================================"
echo ""

java -jar target/sqloj-backend-1.0.0.jar
