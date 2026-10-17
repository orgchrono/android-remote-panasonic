@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM   DIGA Commander Pro - Windows Native Android SDK Setup
REM   Uses native Windows curl & tar (no PowerShell required)
REM ==========================================================

echo ==========================================================
echo   DIGA Commander Pro - Native Android SDK Setup
echo ==========================================================

REM 1. Discover JDK 17
if not defined JAVA_HOME (
    if exist "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot\bin\java.exe" (
        set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
    ) else if exist "C:\Program Files\Eclipse Adoptium\jdk-17*\bin\java.exe" (
        for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do set "JAVA_HOME=%%D"
    ) else if exist "C:\Program Files\Java\jdk-17*\bin\java.exe" (
        for /d %%D in ("C:\Program Files\Java\jdk-17*") do set "JAVA_HOME=%%D"
    )
)

if not defined JAVA_HOME (
    echo [ERROR] JDK 17 not found! Run: winget install Microsoft.OpenJDK.17
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
set "SDK_DIR=%LOCALAPPDATA%\Android\Sdk"
set "CMDLINE_TOOLS_DIR=%SDK_DIR%\cmdline-tools\latest"

if not exist "%CMDLINE_TOOLS_DIR%\bin\sdkmanager.bat" (
    echo [*] Creating SDK directories at: %SDK_DIR%
    if not exist "%SDK_DIR%\cmdline-tools" mkdir "%SDK_DIR%\cmdline-tools"

    echo [*] Downloading Android Command-Line Tools...
    curl.exe -L -o "%TEMP%\cmdline-tools.zip" "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"

    echo [*] Extracting tools via tar...
    tar.exe -xf "%TEMP%\cmdline-tools.zip" -C "%SDK_DIR%\cmdline-tools"
    if exist "%SDK_DIR%\cmdline-tools\cmdline-tools" (
        if exist "%CMDLINE_TOOLS_DIR%" rmdir /s /q "%CMDLINE_TOOLS_DIR%"
        rename "%SDK_DIR%\cmdline-tools\cmdline-tools" latest
    )
    del "%TEMP%\cmdline-tools.zip" >nul 2>&1
)

echo [*] Accepting licenses and installing platform-tools, platforms;android-34, and build-tools;34.0.0...
set "ANDROID_HOME=%SDK_DIR%"
set "ANDROID_SDK_ROOT=%SDK_DIR%"

(echo y & echo y & echo y & echo y) | "%CMDLINE_TOOLS_DIR%\bin\sdkmanager.bat" --licenses >nul 2>&1
call "%CMDLINE_TOOLS_DIR%\bin\sdkmanager.bat" "platform-tools" "platforms;android-34" "build-tools;34.0.0"

(echo sdk.dir=%SDK_DIR:\=\\%)>"%~dp0local.properties"
echo [+] Generated local.properties with sdk.dir = %SDK_DIR%

echo [SUCCESS] Android SDK is ready! You can now run build-local.bat
exit /b 0
