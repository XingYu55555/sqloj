@echo off
REM SQLOJ Java后端快速启动脚本 (Windows版本)

echo ======================================
echo   SQLOJ Java后端快速启动
echo ======================================
echo.

REM 检查Java环境
echo 1. 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未检测到Java环境，请先安装JDK 1.8+
    pause
    exit /b 1
)
java -version
echo.

REM 检查Maven环境
echo 2. 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未检测到Maven，请先安装Maven 3.6+
    pause
    exit /b 1
)
mvn -version | findstr "Apache Maven"
echo.

REM 编译打包
echo 4. 编译打包项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 错误: 编译失败，请检查错误信息
    pause
    exit /b 1
)
echo    编译成功
echo.

REM 启动服务
echo 5. 启动后端服务...
echo    服务地址: http://localhost:8080
echo    API基准路径: http://localhost:8080/api
echo.
echo 按Ctrl+C停止服务
echo ======================================
echo.

java -jar target\sqloj-backend-1.0.0.jar

pause
