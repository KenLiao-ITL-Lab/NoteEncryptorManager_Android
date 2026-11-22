# NoteEncryptorManager（Android）專案文件

本目錄存放 **NoteEncryptorManager** Android 專案的架構、功能、資料安全與開發維護文件，方便開發者快速理解系統設計與日常維護流程。

## 專案簡介

**NoteEncryptorManager** 是一款本地端（Offline-first，離線優先）筆記加密與雜湊（Hash）轉換管理應用程式，主要提供以下能力：

* **訊息雜湊轉換**：將使用者輸入的原文轉換為 `SHA-256` 或 `SHA-512` 雜湊值（經 URL-Safe Base64 編碼），並可快速複製或另存為筆記。
* **雜湊結果取樣**：在筆記詳細頁中針對雜湊結果設定取樣起點與長度，產生指定長度的取樣字串。
* **本地端欄位加密儲存**：透過 AndroidKeyStore（Android 金鑰存放區）與 `AES/CBC/PKCS7` 對資料庫中的敏感欄位自動加解密，所有資料僅保存於裝置本地並停用雲端備份。
* **生物辨識保護**：支援針對單筆筆記啟用密語保護，搭配系統生物辨識（指紋／臉部）或裝置鎖定憑證驗證檢視、編輯與刪除操作。

## 技術棧（Tech Stack）一覽

| 項目 | 技術／版本 | 說明 |
| :--- | :--- | :--- |
| 程式語言 | Kotlin `2.2.10` | 採用官方程式碼風格（`kotlin.code.style=official`） |
| UI 框架 | Jetpack Compose + Material 3（BOM `2025.06.00`） | 宣告式介面與 Edge-to-Edge（無邊框全螢幕）顯示 |
| 導覽元件 | Navigation Compose `2.9.3` + Kotlinx Serialization `1.9.0` | Type-safe（型別安全）路由導覽 |
| 依賴注入 | Hilt `2.60.1` + KSP `2.2.10-2.0.2` | 元件與 ViewModel 依賴管理 |
| 本地資料庫 | Room `2.8.3` | 啟用 `exportSchema = true`，匯出至 `app/schemas/` |
| 偏好設定 | DataStore Preferences `1.1.7` | 非同步儲存演算法與生物辨識開關設定 |
| 生物辨識 | AndroidX Biometric `1.4.0-alpha02` | 整合 `BiometricPrompt` 與裝置憑證驗證 |
| 建置工具 | Android Gradle Plugin（AGP）`9.4.0` / Java `17` | 採用 Version Catalog（[gradle/libs.versions.toml](../gradle/libs.versions.toml)） |
| 支援系統版本 | `minSdk 29` / `compileSdk 36` / `targetSdk 36` | 支援 Android 10（API 29）以上裝置 |

## 文件導覽

| 文件 | 說明 |
| :--- | :--- |
| [architecture.md](./architecture.md) | 整體分層架構、套件目錄職責、Hilt 依賴注入與 Coroutine（協程）分派機制 |
| [features-and-screens.md](./features-and-screens.md) | 三大 Activity、底部導覽畫面、ViewModel 狀態管理與生物辨識互動流程 |
| [data-and-security.md](./data-and-security.md) | Room 資料表結構、DataStore 設定鍵值、欄位加密機制、雜湊取樣演算法與生物辨識實作 |
| [development-guide.md](./development-guide.md) | 開發環境設定、常用 Gradle 指令、Room 升版流程、測試與 Antigravity CLI（`agy`）協作規範 |
| [ui-viewmodel-review.md](./ui-viewmodel-review.md) | UI 元件與 ViewModel 健檢問題清單、風險分級與修復追蹤 |
| [sdk37-agp952-upgrade-checklist.md](./sdk37-agp952-upgrade-checklist.md) | SDK 37（Android 17）與 AGP 9.5.2 升級盤點與驗證清單 |
