@echo off
title CertiFolio - Student Skill ^& Certification Portfolio Registry
echo ==============================================================
echo       Starting CertiFolio Application (Spring Boot 3)
echo ==============================================================
echo.

:: 1. Detect or configure Java Environment
if defined JAVA_HOME (
    echo [INFO] Using existing JAVA_HOME: "%JAVA_HOME%"
) else if exist "C:\Program Files\Apache NetBeans\jdk" (
    set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
    echo [INFO] Detected system JDK at: "C:\Program Files\Apache NetBeans\jdk"
) else (
    echo [INFO] JAVA_HOME not set; relying on PATH java...
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

:: 2. Auto-open browser in the background once server boots
echo [INFO] Launching browser to http://localhost:5173 in 6 seconds...
start "" cmd /c "timeout /t 6 /nobreak >nul && start http://localhost:5173"

:: 3. Launch Spring Boot application
echo [INFO] Starting Spring Boot server on port 5173...
echo [INFO] Press Ctrl+C in this window anytime to terminate the server.
echo.

call "%~dp0mvnw.cmd" spring-boot:run

echo.
echo Application stopped.
pause
