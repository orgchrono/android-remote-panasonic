<#
.SYNOPSIS
    Automated Android SDK Command-Line Tools installer for headless local compilation.

.DESCRIPTION
    Installs the official Google Android SDK commandline-tools into
    $env:LOCALAPPDATA\Android\Sdk, accepts licenses, downloads Platform 34 and
    Build-Tools 34.0.0, and creates local.properties for instant local Gradle builds.
#>

$ErrorActionPreference = "Stop"

$sdkDir = "$env:LOCALAPPDATA\Android\Sdk"
$cmdlineToolsDir = "$sdkDir\cmdline-tools"
$latestDir = "$cmdlineToolsDir\latest"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Automated Android SDK Setup for Local Compilation" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

if (-not (Test-Path "$latestDir\bin\sdkmanager.bat")) {
    Write-Host "[*] Creating SDK directories at: $sdkDir" -ForegroundColor Cyan
    New-Item -ItemType Directory -Path $cmdlineToolsDir -Force | Out-Null

    $zipUrl = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
    $tempZip = "$env:TEMP\commandlinetools.zip"
    $tempExtract = "$env:TEMP\cmdline-tools-extract"

    Write-Host "[*] Downloading official Google Android Command-Line Tools..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $zipUrl -OutFile $tempZip

    Write-Host "[*] Extracting Command-Line Tools..." -ForegroundColor Cyan
    Expand-Archive -Path $tempZip -DestinationPath $tempExtract -Force
    Move-Item -Path "$tempExtract\cmdline-tools" -Destination $latestDir -Force
    Remove-Item -Path $tempZip -Force -ErrorAction SilentlyContinue
    Remove-Item -Path $tempExtract -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "[+] Command-Line Tools installed at $latestDir" -ForegroundColor Green
} else {
    Write-Host "[+] Command-Line Tools already present at $latestDir" -ForegroundColor Green
}

# Configure environment for sdkmanager execution
$env:ANDROID_HOME = $sdkDir
$env:ANDROID_SDK_ROOT = $sdkDir
$sdkManager = "$latestDir\bin\sdkmanager.bat"

Write-Host "[*] Accepting licenses and installing platform-tools, platforms;android-34, and build-tools;34.0.0..." -ForegroundColor Cyan
cmd /c "echo y | `"$sdkManager`" --licenses" | Out-Null
& $sdkManager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Generate local.properties in project root
$escapedSdkDir = $sdkDir.Replace("\", "\\")
$localPropertiesPath = Join-Path $PSScriptRoot "local.properties"
"sdk.dir=$escapedSdkDir" | Set-Content -Path $localPropertiesPath -Encoding ASCII
Write-Host "[+] Generated local.properties with sdk.dir = $sdkDir" -ForegroundColor Green

Write-Host "`n[SUCCESS] Android SDK is ready! You can now run .\build-local.ps1 to assemble APKs locally." -ForegroundColor Green
