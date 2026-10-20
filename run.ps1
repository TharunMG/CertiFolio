# CertiFolio One-Click Launcher for PowerShell
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "          CertiFolio Full-Stack Registry Launcher               " -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

$scriptRoot = $PSScriptRoot
if (-not $scriptRoot) { $scriptRoot = Get-Location }

# Detect Java Home
if (-not $env:JAVA_HOME) {
    if (Test-Path "C:\Program Files\Apache NetBeans\jdk") {
        $env:JAVA_HOME = "C:\Program Files\Apache NetBeans\jdk"
        Write-Host "[INFO] Detected Java at: $env:JAVA_HOME" -ForegroundColor Green
    } elseif (Test-Path "C:\Program Files\Android\Android Studio\jbr") {
        $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
        Write-Host "[INFO] Detected Java at: $env:JAVA_HOME" -ForegroundColor Green
    }
}

# Detect Maven
$mvnCmd = "mvn"
$null = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $?) {
    if (Test-Path "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd") {
        $mvnCmd = "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
        Write-Host "[INFO] Detected Maven at: $mvnCmd" -ForegroundColor Green
    }
}

Write-Host "`n[1/3] Starting Spring Boot REST API (Port 8080)..." -ForegroundColor Yellow
Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$scriptRoot\backend`" && set JAVA_HOME=$env:JAVA_HOME && `"$mvnCmd`" spring-boot:run"

Write-Host "[2/3] Starting React 19 Frontend (Port 5173)..." -ForegroundColor Yellow
Start-Process cmd.exe -ArgumentList "/k", "cd /d `"$scriptRoot\frontend`" && npm run dev"

Write-Host "[3/3] Initializing servers (waiting 6 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 6

Write-Host "Opening default browser at http://localhost:5173..." -ForegroundColor Green
Start-Process "http://localhost:5173"

Write-Host "`n================================================================" -ForegroundColor Cyan
Write-Host "  CertiFolio is running!" -ForegroundColor Green
Write-Host "  - Frontend:   http://localhost:5173"
Write-Host "  - Backend:    http://localhost:8080"
Write-Host "  - Swagger UI: http://localhost:8080/swagger-ui/index.html"
Write-Host "  - H2 Console: http://localhost:8080/h2-console"
Write-Host ""
Write-Host "  Demo Credentials:"
Write-Host "  - Student: student@college.edu / student123"
Write-Host "  - Faculty: faculty@college.edu / faculty123"
Write-Host "================================================================`n" -ForegroundColor Cyan
