@echo off
title CertiFolio One-Click Launcher
echo ================================================================
echo             CertiFolio Full-Stack Registry Launcher             
echo ================================================================
echo.

:: Detect JAVA_HOME
if not defined JAVA_HOME (
    if exist "C:\Program Files\Apache NetBeans\jdk" (
        set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
        echo [INFO] Detected Java JDK at: !JAVA_HOME!
    ) else if exist "C:\Program Files\Android\Android Studio\jbr" (
        set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
        echo [INFO] Detected Android Studio Java JDK at: !JAVA_HOME!
    ) else (
        echo [WARN] JAVA_HOME is not set. Assuming java is in PATH.
    )
)

:: Detect Maven
set "MVN_CMD=mvn"
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" (
        set "MVN_CMD=C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
        echo [INFO] Detected Maven at: %MVN_CMD%
    ) else (
        echo [ERROR] Maven not found. Please install Maven or configure PATH.
    )
)

echo.
echo [1/3] Starting Spring Boot REST API on port 8080...
start "CertiFolio - Backend [Port 8080]" cmd /k "cd /d %~dp0backend && set JAVA_HOME=%JAVA_HOME%&& "%MVN_CMD%" spring-boot:run"

echo [2/3] Starting React 19 Frontend on port 5173...
start "CertiFolio - Frontend [Port 5173]" cmd /k "cd /d %~dp0frontend && npm run dev"

echo [3/3] Waiting for servers to initialize...
timeout /t 5 /nobreak >nul

echo Opening browser at http://localhost:5173 ...
start http://localhost:5173

echo.
echo ================================================================
echo   CertiFolio is now running!
echo   - Frontend:  http://localhost:5173
echo   - Backend:   http://localhost:8080
echo   - Swagger:   http://localhost:8080/swagger-ui/index.html
echo   - H2 Console: http://localhost:8080/h2-console
echo.
echo   Demo Accounts:
echo   - Student:  student@college.edu / student123
echo   - Faculty:  faculty@college.edu / faculty123
echo ================================================================
echo.
pause
