<#
.SYNOPSIS
    Local build and test automation script for Panasonic DIGA Commander Pro.
    Compatible with Windows PowerShell 5.1+, PowerShell 7+, and cross-platform PowerShell.

.DESCRIPTION
    Automates JDK 17 environment discovery, Gradle wrapper invocation,
    linting audits, unit test execution, and APK assembly for local testing.

.PARAMETER Task
    Gradle task to execute. Defaults to 'assembleDebug'.
    Options: 'test', 'ktlintCheck', 'lintDebug', 'assembleDebug', 'assembleRelease', 'clean'

.EXAMPLE
    .\build-local.ps1 -Task test
    .\build-local.ps1 -Task assembleDebug
    .\build-local.ps1 -Task assembleRelease
#>

param (
    [string]$Task = "assembleDebug"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  DIGA Commander Pro - Local Build & Test Pipeline" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Discover JDK 17
if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $possibleJdks = @(
        (Get-ChildItem -Path "C:\Program Files\Microsoft\jdk-17*" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName),
        (Get-ChildItem -Path "C:\Program Files\Eclipse Adoptium\jdk-17*" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName),
        (Get-ChildItem -Path "C:\Program Files\Java\jdk-17*" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName)
    )

    if ($possibleJdks.Count -gt 0 -and (Test-Path "$($possibleJdks[0])\bin\java.exe")) {
        $env:JAVA_HOME = $possibleJdks[0]
        Write-Host "[+] Found JDK 17 at: $env:JAVA_HOME" -ForegroundColor Green
    } else {
        Write-Host "[-] JDK 17 not found! Run: winget install Microsoft.OpenJDK.17" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "[+] Using JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
}

$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 2. Check Android SDK
if (-not $env:ANDROID_HOME -and -not $env:ANDROID_SDK_ROOT) {
    $defaultSdk = "$env:LOCALAPPDATA\Android\Sdk"
    if (Test-Path $defaultSdk) {
        $env:ANDROID_HOME = $defaultSdk
        Write-Host "[+] Auto-detected ANDROID_HOME at: $env:ANDROID_HOME" -ForegroundColor Green
    } else {
        Write-Host "[!] Note: Android SDK not found locally at $defaultSdk" -ForegroundColor Yellow
        Write-Host "    If building APK locally fails due to missing SDK, run setup-android-sdk.bat" -ForegroundColor Yellow
        Write-Host "    or use the automated GitHub Actions CI/CD pipeline which builds release APKs automatically." -ForegroundColor Yellow
    }
} else {
    $configuredSdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { $env:ANDROID_SDK_ROOT }
    Write-Host "[+] Android SDK configured: $configuredSdk" -ForegroundColor Green
}

# 3. Verify Gradle Wrapper
$gradlewCmd = Join-Path $PSScriptRoot "gradlew.bat"
if (-not (Test-Path $gradlewCmd)) {
    Write-Host "[-] gradlew.bat not found in $PSScriptRoot. Initializing wrapper..." -ForegroundColor Yellow
    if (Get-Command "gradle" -ErrorAction SilentlyContinue) {
        gradle wrapper --gradle-version 8.4
    } else {
        Write-Host "[-] Neither gradlew nor system gradle found." -ForegroundColor Red
        exit 1
    }
}

# 4. Execute Selected Task
Write-Host "`n[*] Executing Gradle Task: $Task ...`n" -ForegroundColor Cyan
& $gradlewCmd $Task

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCCESS] Task '$Task' completed successfully!" -ForegroundColor Green
    if ($Task -like "*assemble*") {
        $apks = Get-ChildItem -Path "$PSScriptRoot\app\build\outputs\apk" -Filter "*.apk" -Recurse -ErrorAction SilentlyContinue
        if ($apks) {
            Write-Host "`nGenerated APKs:" -ForegroundColor Cyan
            $apks | ForEach-Object {
                Write-Host "  -> $($_.FullName) ($([math]::round($_.Length / 1MB, 2)) MB)" -ForegroundColor Yellow
            }
        }
    }
} else {
    Write-Host "`n[FAILURE] Task '$Task' failed with exit code $LASTEXITCODE" -ForegroundColor Red
    exit $LASTEXITCODE
}
