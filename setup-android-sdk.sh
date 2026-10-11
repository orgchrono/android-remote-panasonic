#!/usr/bin/env bash
set -eo pipefail

# ==========================================================
#   DIGA Commander Pro - POSIX Android SDK Setup Script
#   Compatible with Linux, macOS, WSL
# ==========================================================

echo "=========================================================="
echo "  DIGA Commander Pro - Cross-Platform Android SDK Setup"
echo "=========================================================="

OS_NAME="$(uname -s)"
case "$OS_NAME" in
    Linux*)
        TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
        DEFAULT_SDK="$HOME/Android/Sdk"
        ;;
    Darwin*)
        TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-mac-11076708_latest.zip"
        DEFAULT_SDK="$HOME/Library/Android/sdk"
        ;;
    *)
        echo "[ERROR] Unsupported operating system: $OS_NAME"
        exit 1
        ;;
esac

SDK_DIR="${ANDROID_HOME:-$DEFAULT_SDK}"
CMDLINE_DIR="$SDK_DIR/cmdline-tools/latest"

if [ ! -f "$CMDLINE_DIR/bin/sdkmanager" ]; then
    echo "[*] Creating SDK directory: $SDK_DIR"
    mkdir -p "$SDK_DIR/cmdline-tools"
    
    TEMP_ZIP="/tmp/cmdline-tools.zip"
    TEMP_EXTRACT="/tmp/cmdline-extract"
    rm -rf "$TEMP_ZIP" "$TEMP_EXTRACT"
    
    echo "[*] Downloading command-line tools..."
    curl -L -o "$TEMP_ZIP" "$TOOLS_URL"
    
    echo "[*] Extracting command-line tools..."
    mkdir -p "$TEMP_EXTRACT"
    unzip -q "$TEMP_ZIP" -d "$TEMP_EXTRACT"
    rm -rf "$CMDLINE_DIR"
    mv "$TEMP_EXTRACT/cmdline-tools" "$CMDLINE_DIR"
    rm -rf "$TEMP_ZIP" "$TEMP_EXTRACT"
fi

echo "[*] Installing platform-tools, platforms;android-34, build-tools;34.0.0..."
yes | "$CMDLINE_DIR/bin/sdkmanager" --licenses > /dev/null 2>&1 || true
"$CMDLINE_DIR/bin/sdkmanager" "platform-tools" "platforms;android-34" "build-tools;34.0.0"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "sdk.dir=$SDK_DIR" > "$SCRIPT_DIR/local.properties"
echo "[+] Generated local.properties with sdk.dir = $SDK_DIR"
echo "[SUCCESS] Android SDK configured successfully!"
