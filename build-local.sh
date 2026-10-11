#!/usr/bin/env bash
set -eo pipefail

# ==========================================================
#   DIGA Commander Pro - Cross-Platform POSIX Build Script
#   Compatible with Linux, macOS, WSL, and Git Bash
# ==========================================================

TASK="${1:-assembleDebug}"

echo "=========================================================="
echo "  DIGA Commander Pro - Cross-Platform Build Pipeline"
echo "=========================================================="

# 1. Discover or Validate JAVA_HOME
if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
    if [ -d "/usr/lib/jvm/java-17-openjdk-amd64" ]; then
        export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
    elif [ -d "/Library/Java/JavaVirtualMachines" ]; then
        # macOS detection
        POSSIBLE_JDK=$(find /Library/Java/JavaVirtualMachines -maxdepth 2 -name "Home" 2>/dev/null | grep "17" | head -n 1 || true)
        if [ -n "$POSSIBLE_JDK" ]; then
            export JAVA_HOME="$POSSIBLE_JDK"
        fi
    fi
fi

if [ -n "$JAVA_HOME" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "[+] Using JAVA_HOME: $JAVA_HOME"
fi

# 2. Discover Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_HOME="$HOME/Android/Sdk"
        echo "[+] Auto-detected ANDROID_HOME: $ANDROID_HOME"
    elif [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_HOME="$HOME/Library/Android/sdk"
        echo "[+] Auto-detected ANDROID_HOME: $ANDROID_HOME"
    fi
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
chmod +x "$SCRIPT_DIR/gradlew"

echo "[*] Executing Gradle Task: $TASK ..."
"$SCRIPT_DIR/gradlew" "$TASK"
echo "[SUCCESS] Task '$TASK' completed successfully!"
