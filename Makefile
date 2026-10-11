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

release:
	$(GRADLEW) assembleRelease

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
