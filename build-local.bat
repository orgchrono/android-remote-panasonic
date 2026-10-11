@echo off
setlocal enabledelayedexpansion

REM ==========================================================
REM   DIGA Commander Pro - Native Windows CMD Build Script
REM ==========================================================

set "TASK=%~1"
if "%TASK%"=="" set "TASK=assembleRelease"

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
echo [+] Using JAVA_HOME: %JAVA_HOME%

REM 2. Discover Android SDK
if not defined ANDROID_HOME (
    if exist "%LOCALAPPDATA%\Android\Sdk" (
        set "ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk"
        echo [+] Auto-detected ANDROID_HOME: %ANDROID_HOME%
    )
)

REM 3. Execute Gradle Wrapper
echo [*] Executing Gradle Task: %TASK%
call "%~dp0gradlew.bat" %TASK%
set "EXIT_CODE=%ERRORLEVEL%"

if %EXIT_CODE% equ 0 (
    echo.
    echo ==========================================================
    echo [SUCCESS] Gradle task '%TASK%' completed successfully!
    echo ==========================================================
    if not exist "%~dp0apks" mkdir "%~dp0apks"
    if exist "%~dp0app\build\outputs\apk\release\app-release.apk" (
        copy /y "%~dp0app\build\outputs\apk\release\app-release.apk" "%~dp0Panasonic-DIGA-Commander-release.apk" >nul
        copy /y "%~dp0app\build\outputs\apk\release\app-release.apk" "%~dp0apks\Panasonic-DIGA-Commander-release.apk" >nul
        echo [*] RELEASE APK GERADO:
        echo     - %~dp0Panasonic-DIGA-Commander-release.apk
        echo     - %~dp0apks\Panasonic-DIGA-Commander-release.apk
    )
    if exist "%~dp0app\build\outputs\apk\debug\app-debug.apk" (
        copy /y "%~dp0app\build\outputs\apk\debug\app-debug.apk" "%~dp0Panasonic-DIGA-Commander-debug.apk" >nul
        copy /y "%~dp0app\build\outputs\apk\debug\app-debug.apk" "%~dp0apks\Panasonic-DIGA-Commander-debug.apk" >nul
        echo [*] DEBUG APK GERADO:
        echo     - %~dp0Panasonic-DIGA-Commander-debug.apk
        echo     - %~dp0apks\Panasonic-DIGA-Commander-debug.apk
    )
    echo ==========================================================
) else (
    echo.
    echo [FAILURE] Gradle task '%TASK%' failed with exit code %EXIT_CODE%
)

exit /b %EXIT_CODE%
