.PHONY: all build release test lint format android-lint clean help

# Detect operating system and select proper Gradle wrapper
ifeq ($(OS),Windows_NT)
    GRADLEW := gradlew.bat
else
    GRADLEW := ./gradlew
endif

all: test build

build:
	$(GRADLEW) assembleDebug
	@powershell -Command "New-Item -ItemType Directory -Force -Path apks | Out-Null; Copy-Item 'app/build/outputs/apk/debug/app-debug.apk' 'Panasonic-DIGA-Commander-debug.apk'; Copy-Item 'app/build/outputs/apk/debug/app-debug.apk' 'apks/Panasonic-DIGA-Commander-debug.apk'"
	@echo [+] Debug APK available: Panasonic-DIGA-Commander-debug.apk

release:
	$(GRADLEW) assembleRelease
	@powershell -Command "New-Item -ItemType Directory -Force -Path apks | Out-Null; Copy-Item 'app/build/outputs/apk/release/app-release.apk' 'Panasonic-DIGA-Commander-release.apk'; Copy-Item 'app/build/outputs/apk/release/app-release.apk' 'apks/Panasonic-DIGA-Commander-release.apk'"
	@echo [+] Release APK available: Panasonic-DIGA-Commander-release.apk

test:
	$(GRADLEW) test

lint:
	$(GRADLEW) ktlintCheck

format:
	$(GRADLEW) ktlintFormat

android-lint:
	$(GRADLEW) lintDebug

clean:
	$(GRADLEW) clean

help:
	@echo "DIGA Commander Pro Build Automation:"
	@echo "  make build        - Compile Debug APK"
	@echo "  make release      - Compile Release APK"
	@echo "  make test         - Run JUnit unit tests"
	@echo "  make lint         - Run KtLint style auditor"
	@echo "  make format       - Auto-format code style"
	@echo "  make android-lint - Run Android Lint"
	@echo "  make clean        - Clean build outputs"
