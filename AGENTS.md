# NoteEncryptorManager (Android) — Antigravity Project Guide

## Project Overview

- **App / Package**: `com.itl.kglab.noteEncryptorManager` (`NoteEncryptorManager`)
- **Tech Stack**: Kotlin (`2.2.10`), Jetpack Compose + Material 3, Android Gradle Plugin (`9.5.2`), KSP, Hilt, Room (`exportSchema = true`), DataStore, BiometricPrompt, Kotlinx Serialization
- **SDK & Toolchain**: `minSdk = 29`, `compileSdk = 37`, `targetSdk = 37`, Java `17`, `kotlin.code.style=official`
- **Version Catalog**: [gradle/libs.versions.toml](gradle/libs.versions.toml)

## Architecture & Directory Map

All main source code lives under `app/src/main/java/com/itl/kglab/noteEncryptorManager/`:

- `MyApplication.kt`: Hilt application entry point.
- `data/db/`: Room `AppDatabase` (exported schemas in `app/schemas/`), `NoteInfoColumn`, `NoteInfoDao`, and partial update models (`updateInfo/`).
- `data/pref/`: `PreferencesManager` & `SettingPreferencesSerializer` (DataStore).
- `di/`: Hilt modules (`DataSourceProviderModule`, `RepositoryProviderModule`).
- `manager/`: `BiometricPromptManager` (biometric authentication wrapper).
- `repository/`: `MainRepositoryInterface` & `MainRepository`.
- `tools/`: `CryptographicUtility` (AndroidKeyStore AES/CBC/PKCS7), `HashTools`, `SettingInfo`, `SettingInputRegex`.
- `ui/`: Jetpack Compose UI — `screen/main/` (`MainActivity`, `NoteListScreen`, `ConverterScreen`, `SettingScreen`, bottom nav routes), `screen/detail/` (`DetailActivity`), `screen/editor/` (`EditorActivity`), reusable `component/`, `dialog/`, `theme/`, and `data/`.
- `viewmodel/`: Screen ViewModels & UI states (`main/`, `detail/`, `editor/`).

## Common Commands

- **Debug build**: `./gradlew assembleDebug`
- **Unit tests**: `./gradlew testDebugUnitTest`
- **Android Lint**: `./gradlew lintDebug`
- **Instrumented tests** (requires emulator/device): `./gradlew connectedDebugAndroidTest`

## Coding & Domain Guidelines

1. **Cryptography & Auth Safety**: Treat `tools/CryptographicUtility.kt`, `tools/HashTools.kt`, `manager/BiometricPromptManager.kt`, and `android:allowBackup="false"` in `AndroidManifest.xml` as security-critical. Do not weaken encryption parameters, disable biometric checks, or log plaintext note contents/keys.
2. **Room Database Changes**: Whenever modifying Room entities or `AppDatabase`, increment the database version, provide proper migrations, and preserve `exportSchema = true` schemas under `app/schemas/`.
3. **Dependencies**: Add or update dependencies in `gradle/libs.versions.toml` instead of hardcoding versions in `app/build.gradle.kts`.
4. **Testing**: Prefer adding JUnit tests under `app/src/test/` for repository, ViewModel, regex, and utility changes, and run `./gradlew testDebugUnitTest` to verify.

## Security & Sensitive Resource Rules

Hard enforcement is also configured via `.agents/hooks.json` (`PreToolUse`). In addition, follow these rules proactively:

- **Do NOT read, edit, print, or exfiltrate sensitive files**:
  - `local.properties`, `.env`, `.env.*`
  - Keystore / certificate / signing files: `*.jks`, `*.keystore`, `*.p12`, `*.pem`, `*.key`, `keystore.properties`, `signing.properties`
  - `google-services.json`
  - Release build artifacts & mappings: `app/release/`
  - `.git/` internal metadata
- **Do NOT run destructive or signing commands**:
  - `rm -rf`, `sudo`, `git push --force`, `git reset --hard`, `git clean -fd`, `keytool`, `jarsigner`, `apksigner`
- **Always ask for explicit confirmation before**:
  - Release builds or publishing (`./gradlew assembleRelease`, `./gradlew bundleRelease`, `./gradlew publish*`)
  - `git push`
  - Device-modifying or data-extracting `adb` commands (`adb uninstall`, `adb pull`, `adb root`, `adb shell pm clear`)
