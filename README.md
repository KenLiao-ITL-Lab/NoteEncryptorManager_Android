# NoteEncryptorManager（密語訊息筆記簿）

**NoteEncryptorManager** 是一款離線優先（Offline-first）的 Android 筆記加密與雜湊轉換管理應用程式，採用 Kotlin、Jetpack Compose（Material 3）、Hilt、Room 與 AndroidKeyStore 建置。

## 主要功能

* **訊息雜湊轉換**：將輸入原文轉換為 `SHA-256` 或 `SHA-512`（URL-Safe Base64 編碼），支援快速複製或另存為筆記。
* **雜湊結果循環取樣**：在筆記詳細頁中針對雜湊結果設定起始索引與長度，產生指定長度的取樣字串並自動儲存。
* **本地端欄位加密儲存**：透過 `AndroidKeyStore` 與 `AES/CBC/PKCS7Padding` 自動加解密資料庫敏感欄位，且停用 Android Auto Backup（`android:allowBackup="false"`），確保資料僅存於裝置本地。
* **生物辨識保護與狀態一致性**：支援針對單筆筆記啟用密語保護，搭配系統 `BiometricPrompt`／裝置憑證驗證檢視、編輯與刪除操作，並於系統移除生物辨識時自動同步關閉與提醒。

## 技術棧摘要

* **語言與 UI**：Kotlin `2.2.10`、Jetpack Compose + Material 3（BOM `2025.06.00`）、Navigation Compose `2.9.3`
* **架構與資料**：MVVM + Repository、Hilt `2.60.1`、Room `2.8.3`（`exportSchema = true`）、DataStore Preferences `1.1.7`
* **安全與驗證**：AndroidKeyStore（`AES/CBC/PKCS7Padding`）、AndroidX Biometric `1.4.0-alpha02`
* **建置與 SDK**：AGP `9.5.2`、Gradle `9.7.1`、Java `17`、`minSdk 29` / `compileSdk 37` / `targetSdk 37`

## 快速開始

```bash
# 建置 Debug APK
./gradlew assembleDebug

# 執行本機 JUnit 單元測試
./gradlew testDebugUnitTest

# 執行 Android Lint 靜態檢查
./gradlew lintDebug

# 執行儀器測試（需連接實機或模擬器）
./gradlew connectedDebugAndroidTest
```

## 專案文件導覽

完整架構、功能、資料安全與維護指南請參閱 [docs/README.md](./docs/README.md)：

* [系統架構與模組設計](./docs/architecture.md)
* [核心功能與畫面流程](./docs/features-and-screens.md)
* [資料結構與安全設計](./docs/data-and-security.md)
* [開發與維護指南](./docs/development-guide.md)
* [系統身份辨識提醒功能規格](./docs/biometric-reminder-spec.md)
* [SDK 37 與 AGP 9.5.2 升級盤點清單](./docs/sdk37-agp952-upgrade-checklist.md)
* [UI 與 ViewModel 健檢問題清單](./docs/ui-viewmodel-review.md)
* [Antigravity 專案指引](./AGENTS.md)

## 授權條款

本專案採用 [MIT License](./LICENSE)。
