# 開發與維護指南

本文件說明本專案的開發環境需求、常用建置與測試指令、資料庫升版流程，以及使用 Antigravity CLI（`agy`）協作時的安全與專案規範。

---

## 1. 開發環境需求

* **JDK（Java Development Kit，Java 開發工具組）**：Java `17`
* **Android SDK**：`compileSdk = 36`、`targetSdk = 36`、`minSdk = 29`
* **Gradle / AGP**：Gradle Wrapper（`./gradlew`）+ Android Gradle Plugin `9.4.0`
* **Android Studio**：建議使用支援 AGP 9.x 與 Kotlin `2.2.10` 的版本

> 確認專案根目錄存在 `local.properties` 並指向本機 Android SDK 路徑（此檔案已列入 `.gitignore`，不應提交至版控）。

---

## 2. 常用 Gradle 指令

在專案根目錄執行以下指令：

```bash
# 建置 Debug APK（除錯安裝包）
./gradlew assembleDebug

# 執行本機 JUnit 單元測試
./gradlew testDebugUnitTest

# 執行 Android Lint 靜態程式碼檢查
./gradlew lintDebug

# 執行儀器測試（需連接實機或模擬器）
./gradlew connectedDebugAndroidTest
```

### Build Types（建置類型）說明
定義於 [app/build.gradle.kts](../app/build.gradle.kts)：
* **`debug`**：停用混淆（`isMinifyEnabled = false`）與資源縮減，加速開發除錯。
* **`release`**：啟用混淆與資源縮減（`proguard-android-optimize.txt` + `app/proguard-rules.pro`）。執行 Release 打包或發布前請先確認簽章設定與版本號（`versionCode`、`versionName`）。

---

## 3. 依賴與套件管理原則

* 所有第三方與 AndroidX 套件版本皆集中宣告於 [gradle/libs.versions.toml](../gradle/libs.versions.toml)（Gradle Version Catalog，版本目錄）。
* 新增或升級套件時，應優先修改 `libs.versions.toml`，避免在 [app/build.gradle.kts](../app/build.gradle.kts) 硬編碼版本號。
* 注意目前 [gradle.properties](../gradle.properties) 包含 `android.disallowKotlinSourceSets=false`，用於相容內建 Kotlin 與 KSP（Kotlin Symbol Processing，Kotlin 符號處理器）的設定，升級 AGP／KSP 時請一併檢視是否仍需保留。

---

## 4. Room 資料庫異動與升版流程

本專案啟用了 Room Schema（資料庫結構描述）匯出（設定位於 `app/build.gradle.kts` 的 `room { schemaDirectory("$projectDir/schemas") }`）。修改 `NoteInfoColumn` 或 `AppDatabase` 時請遵循以下步驟：

1. 在 [AppDatabase.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/data/db/AppDatabase.kt) 遞增 `@Database(version = ...)` 版本號。
2. 實作對應的 `Migration(oldVersion, newVersion)`，並於 [DataSourceProviderModule.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/di/DataSourceProviderModule.kt) 的 `Room.databaseBuilder(...)` 透過 `.addMigrations(...)` 註冊（避免使用 `fallbackToDestructiveMigration()` 導致使用者加密筆記遺失）。
3. 執行建置或測試，確認 `app/schemas/com.itl.kglab.noteEncryptorManager.data.db.AppDatabase/` 下已產生新版本的 JSON schema 檔案，並保留舊版本檔案進版控。
4. 撰寫對應的單元／遷移測試驗證既有資料可正常升級與解密。

---

## 5. 測試建議

目前測試目錄位於：
* **單元測試**：`app/src/test/java/com/itl/kglab/noteEncryptorManager/`
* **儀器測試**：`app/src/androidTest/java/com/itl/kglab/noteEncryptorManager/`

建議優先針對以下核心邏輯補充與維護 JUnit 單元測試：
* `HashTools` 的雜湊輸出與循環取樣邊界條件（例如 `size = 0`、索引大於字串長度、含換行字元）。
* `SettingInputRegex` 的合法與非法輸入檢核。
* `MainRepository`（搭配 Fake DAO 與 Dispatcher）與各 ViewModel 的狀態更新流程。

---

## 6. Antigravity CLI（`agy`）專案協作與安全規範

本專案已完成 Antigravity CLI 初始化，相關檔案如下：

| 檔案 | 用途 |
| :--- | :--- |
| [AGENTS.md](../AGENTS.md) | 專案常駐指引，提供架構摘要、指令與安全紅線給 AI 助理參考 |
| [.agents/hooks.json](../.agents/hooks.json) | 註冊 `PreToolUse` 生命週期 Hook（掛鉤），自動攔截檔案與終端操作 |
| [.agents/scripts/security_guard.py](../.agents/scripts/security_guard.py) | 硬性安全檢查腳本：封鎖敏感檔案（`local.properties`、金鑰檔、`google-services.json`、`app/release/`、`.git/`）與危險指令，並對 Release 打包、`git push` 及敏感 `adb` 指令強制要求人工確認 |
| [.agents/agy-settings.example.json](../.agents/agy-settings.example.json) | CLI 沙盒與權限規則設定範本 |

### 建議啟動方式

```bash
# 啟用終端沙盒啟動（自動套用 AGENTS.md 與 .agents/hooks.json）
agy --sandbox

# 或一併載入專案權限範本（僅影響本次工作階段）
agy --sandbox --settings .agents/agy-settings.example.json
```
