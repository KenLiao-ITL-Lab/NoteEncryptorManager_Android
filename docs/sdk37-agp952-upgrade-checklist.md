# SDK 37（Android 17）與 AGP 9.5.2 升級盤點清單

本文件盤點將 **NoteEncryptorManager（Android）** 從現行環境（`compileSdk / targetSdk = 36`、`AGP 9.4.0`）升級至 **`compileSdk / targetSdk = 37`（Android 17）** 與 **`AGP 9.5.2`** 時，所有需要調整的建置設定、相依套件、行為變更適配項目與驗證流程。

> 盤點日期：2026-11-22｜升級執行日期：2026-11-22｜最後更新：2026-11-25（AGP `9.5.2` ＋ `compileSdk / targetSdk = 37` 已套用）

---

## 1. 升級前後版本對照

| 項目 | 升級前版本 | 目前版本（已套用）／最低需求 | 檔案位置 |
| :--- | :--- | :--- | :--- |
| `compileSdk` | `36` | **`37`**（已升級） | [app/build.gradle.kts](../app/build.gradle.kts#L15) |
| `targetSdk` | `36` | **`37`**（已升級） | [app/build.gradle.kts](../app/build.gradle.kts#L20) |
| `minSdk` | `29` | 維持 `29` | [app/build.gradle.kts](../app/build.gradle.kts#L19) |
| Android Gradle Plugin（AGP） | `9.4.0` | **`9.5.2`**（已升級） | [gradle/libs.versions.toml](../gradle/libs.versions.toml#L2) |
| Gradle Wrapper | `9.7.1` | `9.7.1`（最低需求 `9.6.0`） | [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties#L4) |
| Android SDK Build Tools | — | 最低 `36.0.0` ＋ 已安裝 API 37 Platform | 本機 Android SDK Manager |
| JDK | Java `17` | Java `17`（最低 Java `17`） | [app/build.gradle.kts](../app/build.gradle.kts#L50-L53)、[L69-L73](../app/build.gradle.kts#L69-L73) |
| Kotlin / KSP | `2.2.10` / `2.2.10-2.0.2` | 維持現行；後續建議評估升級以移除 `disallowKotlinSourceSets` 逃生開關 | [gradle/libs.versions.toml](../gradle/libs.versions.toml#L3-L4) |

---

## 2. 建置腳本與工具鏈調整（必要項目）

### - [x] 2.1 更新 AGP 版本與外掛宣告（[gradle/libs.versions.toml](../gradle/libs.versions.toml)）
* 已將 `agp = "9.4.0"` 更新為 `agp = "9.5.2"`。
* 已將 `kotlin("plugin.serialization") version "2.2.10"` 納入 `[plugins]` 管理（`kotlin-serialization`）並同步於根目錄 [build.gradle.kts](../build.gradle.kts) 宣告 `apply false`。
* 已清理 `[plugins]` 中未使用的 `jetbrains-kotlin-android`。

### - [x] 2.2 更新 SDK 版本並清理過時 DSL（[app/build.gradle.kts](../app/build.gradle.kts)）
* 已將 `compileSdk` 與 `targetSdk` 更新為 `37`。
* 已改為 `alias(libs.plugins.kotlin.serialization)`。
* 已移除過時的 `composeOptions { kotlinCompilerExtensionVersion = "1.5.12" }`。
* 已加入 `kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }` 明確鎖定 Kotlin JVM 目標版本為 Java 17。

### - [ ] 2.3 處理 `android.disallowKotlinSourceSets=false` 實驗性開關（[gradle.properties](../gradle.properties#L25-L26)）
* **現況**：`AGP 9.5.2` 搭配現行 Kotlin `2.2.10` + KSP `2.2.10-2.0.2` 仍需暫留 `android.disallowKotlinSourceSets=false`（建置正常通過，僅有實驗性警告）。
* **後續排程**：待同步升級 Kotlin 與 KSP（支援 AGP 9 built-in Kotlin 之版本）後即可正式移除。

### - [x] 2.4 檢查 Gradle Wrapper 與本機 SDK 環境
* [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties#L4) 為 `gradle-9.7.1-bin.zip`，已驗證與 AGP `9.5.2` 相容。
* 本機已安裝 Android 17（API 37）SDK Platform，並完成 `assembleDebug`、`testDebugUnitTest` 與 `lintDebug` 驗證。

---

## 3. 第三方與 AndroidX 套件相容性盤點

| 套件 | 目前版本 | 升級評估與建議 |
| :--- | :--- | :--- |
| Hilt | `2.60.1` | 已高於 AGP 9 built-in Kotlin 相容門檻（`2.59.2+`）；升級後驗證 KSP 程式碼生成與 DI 注入即可 |
| Room | `2.8.3` | 確認搭配新 AGP／KSP 時 `app/schemas/` 匯出與 DAO 產生正常 |
| AndroidX Biometric | `1.4.0-alpha02` | 目前為 Alpha（測試版），建議評估升級至較穩定的 Beta／Stable 版本並驗證 API 37 行為 |
| Compose BOM / Material 3 | `2025.06.00` | 升至 `compileSdk = 37` 後建議持續追蹤 `MenuAnchorType.PrimaryNotEditable`（[SettingScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L201)）等實驗性／棄用 API |
| Core KTX / Activity / Navigation | `1.17.0` / `1.10.1` / `2.9.3` | 視 `compileSdk = 37` 編譯與 Lint（靜態程式碼檢查）結果決定是否小幅升版 |
| DataStore / Serialization | `1.1.7` / `1.9.0` | 若同步升級 Kotlin，須同步確認 `kotlinx-serialization-json` 與外掛版本相容 |

---

## 4. Android 17（API 37）平台行為變更影響評估

### 4.1 高度相關（須實際調整或強化）

#### - [ ] 大螢幕可調整視窗大小與方向強制生效（`targetSdk = 37`）
* **平台變更**：Android 17 在大螢幕裝置（最小寬度 `≥ 600dp`，含平板、摺疊機與桌面視窗模式）正式移除禁止縮放與鎖定螢幕方向的排除選項（opt-out），系統將忽略限制並強制允許任意縮放與旋轉。
* **已完成之程式碼調整**：
  1. **組態變更狀態保存**：已修復 `EditorActivity` 重建重複初始化、`DetailActivity` 回前景覆蓋輸入與 `MainRoute` 旋轉遺失 `pendingAuthAction` 的問題（詳見 [ui-viewmodel-review.md](./ui-viewmodel-review.md) #7、#8）。
  2. **小高度／橫向視窗捲動支援**：已於 [ConverterScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/ConverterScreen.kt#L39) 與 [SettingScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L58) 外層 `Column` 加上 `.verticalScroll(rememberScrollState())`，避免在橫向或分割視窗模式下底部按鈕遭裁切。
* **剩餘實測項目**：於 API 37 平板／折疊機與分割視窗實際驗證各畫面縮放、旋轉與按鈕可點擊性。

#### - [ ] Edge-to-Edge（無邊框全螢幕）與鍵盤內距檢查
* **現況**：三個 Activity 皆已呼叫 `enableEdgeToEdge()` 並套用 `Scaffold` 的 `innerPadding`。
* **建議檢查**：在 API 37 模擬器上開啟手勢導覽列與三鍵導覽列，並於 `ConverterScreen`、`DetailActivity`、`EditorActivity` 叫出軟鍵盤，確認 `imePadding()` 正常、最下方按鈕與輸入框不會被鍵盤或系統列遮住。

### 4.2 中度相關（需回歸測試）

#### - [ ] 生物辨識（`BiometricPrompt`）與裝置憑證流程
* **檔案**：[BiometricPromptManager.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/manager/BiometricPromptManager.kt)、[MainRoute.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainRoute.kt)
* **檢查重點**：Android 17 持續支援 `BiometricPrompt` 與 `BIOMETRIC_STRONG or DEVICE_CREDENTIAL`，升級後須在 API 37 實測：
  * 啟用／關閉生物辨識開關
  * 鎖定筆記的檢視、編輯、刪除驗證流程
  * 未登錄生物辨識時透過 `Settings.ACTION_BIOMETRIC_ENROLL` 跳轉系統設定

#### - [x] AndroidKeyStore 加解密與 R8 混淆（Release 建置）
* **檔案**：[CryptographicUtility.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/tools/CryptographicUtility.kt)、[app/proguard-rules.pro](../app/proguard-rules.pro)
* **驗證結果**：
  * 已執行 `./gradlew assembleRelease` 通過（`:app:lintVitalRelease`、`:app:minifyReleaseWithR8`、`:app:optimizeReleaseResources` 皆零錯誤、零 R8 缺漏規則）。
  * 已檢查 R8 `seeds.txt` 與 `mapping.txt`，確認 Room `AppDatabase`／`AppDatabase_Impl` 與 Navigation Compose Type-Safe 路由之 `MainBottomNavigationItem`（`Converter`／`NoteList`／`Setting`）`INSTANCE` 與 `KSerializer serializer()` 皆正確保留，其餘 ViewModel、DAO 與工具類別已正常混淆。
  * [app/proguard-rules.pro](../app/proguard-rules.pro#L23-L27) 內的 `-optimizationpasses 5`、`-dontusemixedcaseclassnames` 與 `-verbose` 為舊版 ProGuard 指令（R8 會略過），不影響建置，後續可視需要清理。

### 4.3 已盤點但不受影響的 Android 17 變更

| Android 17 變更項目 | 本專案影響 | 原因 |
| :--- | :---: | :--- |
| `ACCESS_LOCAL_NETWORK` 本地網路權限 | 無 | 純離線 App，未宣告網路權限 |
| Certificate Transparency（憑證透明度）預設啟用 | 無 | 無任何網路連線 |
| SMS OTP 3 小時延遲保護 | 無 | 未使用簡訊權限或廣播接收器 |
| `System.load()` 原生函式庫唯讀限制 | 無 | 未載入自訂 NDK／`.so` 原生函式庫 |
| 背景音訊焦點強化（Media3） | 無 | 無音訊播放功能 |

---

## 5. 專案文件同步更新項目

以下文件中的版本記載均已同步更新至 SDK 37 與 AGP 9.5.2：
* [x] [README.md](../README.md)（專案技術棧摘要）
* [x] [AGENTS.md](../AGENTS.md)（AGP 與 `compileSdk / targetSdk` 版本）
* [x] [docs/README.md](./README.md)（技術棧版本表）
* [x] [docs/development-guide.md](./development-guide.md)（環境需求與 AGP／SDK 版本）

---

## 6. 升級執行順序與驗證清單

* [x] 1. **備份與分支**：確認工作區乾淨，建立獨立升級分支。
* [x] 2. **安裝 SDK 37**：於 Android Studio SDK Manager 安裝 Android 17（API 37）SDK Platform。
* [x] 3. **更新建置設定**：調整 `libs.versions.toml`（`agp = "9.5.2"`、統一 `kotlin-serialization`）、`app/build.gradle.kts`（`compileSdk/targetSdk = 37`、移除 `composeOptions`、補上 `JvmTarget.JVM_17`）。（註：`android.disallowKotlinSourceSets=false` 待後續升級 Kotlin／KSP 時移除。）
* [x] 4. **執行編譯與靜態檢查**：已完成 `./gradlew assembleDebug`、`./gradlew testDebugUnitTest`、`./gradlew lintDebug` 與 `./gradlew assembleRelease` 驗證通過。
* [ ] 5. **大螢幕與 API 37 回歸測試**：
   * 在 API 37 模擬器（手機＋平板／可折疊視窗縮放模式）驗證所有畫面、旋轉、分割畫面與軟鍵盤 `imePadding()` 操作。
   * 實測加密筆記的新增、讀取、編輯、取樣、刪除、既有資料解密相容性與生物辨識驗證流程。
* [x] 6. **同步更新文件**：已更新 `README.md`、`AGENTS.md` 與 `docs/` 內相關版本號與進度。
