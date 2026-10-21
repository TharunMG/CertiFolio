# CertiFolio One-Click PowerShell Runner
Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host "      Starting CertiFolio Application (Spring Boot 3)         " -ForegroundColor Cyan
Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Detect / Configure Java
if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Program Files\Apache NetBeans\jdk") {
        $env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
        Write-Host "[INFO] Configured JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
    }
}

if ($env:JAVA_HOME) {
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}

# 2. Schedule automatic browser launch
Write-Host "[INFO] Opening browser in 6 seconds at http://localhost:5173 ..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoProfile", "-Command", "Start-Sleep -Seconds 6; Start-Process 'http://localhost:5173'" -WindowStyle Hidden

# 3. Start Spring Boot app
Write-Host "[INFO] Starting application on http://localhost:5173 ..." -ForegroundColor Green
Write-Host "[INFO] Press Ctrl+C anytime to stop.`n" -ForegroundColor Gray

& "$PSScriptRoot\mvnw.cmd" spring-boot:run
