# SDK 37（Android 17）與 AGP 9.5.2 升級盤點清單

本文件盤點將 **NoteEncryptorManager（Android）** 從現行環境（`compileSdk / targetSdk = 36`、`AGP 9.4.0`）升級至 **`compileSdk / targetSdk = 37`（Android 17）** 與 **`AGP 9.5.2`** 時，所有需要調整的建置設定、相依套件、行為變更適配項目與驗證流程。

> 盤點日期：2026-11-22

---

## 1. 目前版本與目標版本對照

| 項目 | 目前版本 | 目標／最低需求 | 檔案位置 |
| :--- | :--- | :--- | :--- |
| `compileSdk` | `36` | `37` | [app/build.gradle.kts](../app/build.gradle.kts#L17) |
| `targetSdk` | `36` | `37` | [app/build.gradle.kts](../app/build.gradle.kts#L22) |
| `minSdk` | `29` | 維持 `29` | [app/build.gradle.kts](../app/build.gradle.kts#L21) |
| Android Gradle Plugin（AGP） | `9.4.0` | `9.5.2` | [gradle/libs.versions.toml](../gradle/libs.versions.toml#L2) |
| Gradle Wrapper | `9.7.1` | 最低 `9.6.0`（現行已符合） | [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties#L4) |
| Android SDK Build Tools | — | 最低 `36.0.0` ＋ 安裝 API 37 Platform | 本機 Android SDK Manager |
| JDK | Java `17`（專案宣告） | 最低 Java `17` | [app/build.gradle.kts](../app/build.gradle.kts#L52-L55) |
| Kotlin / KSP | `2.2.10` / `2.2.10-2.0.2` | 建議評估升級以移除 `disallowKotlinSourceSets` 逃生開關 | [gradle/libs.versions.toml](../gradle/libs.versions.toml#L3-L4) |

---

## 2. 建置腳本與工具鏈調整（必要項目）

### - [ ] 2.1 更新 AGP 版本與外掛宣告（[gradle/libs.versions.toml](../gradle/libs.versions.toml)）
* 將 `agp = "9.4.0"` 更新為 `agp = "9.5.2"`。
* 將目前硬編碼在 `app/build.gradle.kts` 第 12 行的 `kotlin("plugin.serialization") version "2.2.10"` 納入 `[plugins]` 管理：
  ```toml
  kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
  ```
* 清理 `[plugins]` 中已未使用的 `jetbrains-kotlin-android`（AGP 9 預設啟用內建 Kotlin，已不需再掛載此舊外掛）。

### - [ ] 2.2 更新 SDK 版本並清理過時 DSL（[app/build.gradle.kts](../app/build.gradle.kts)）
* 第 17 行與第 22 行：將 `compileSdk = 36` 與 `targetSdk = 36` 更新為 `37`。
* 第 12 行：將 `kotlin("plugin.serialization") version "2.2.10"` 改為 `alias(libs.plugins.kotlin.serialization)`。
* 第 60～62 行：**移除過時的 `composeOptions { kotlinCompilerExtensionVersion = "1.5.12" }`**（專案已改用 Kotlin 2.x 的 `libs.plugins.kotlin.compose`，此區塊在新版 AGP 中無效且屬棄用寫法）。
* **補齊 Kotlin `jvmTarget` 設定**：目前建置記錄顯示本機 JDK 為較新版本時出現 `Kotlin does not yet support 25 JDK target, falling back to Kotlin JVM_24 JVM target`，造成 Kotlin 編譯目標與 `compileOptions` 的 Java 17 不一致（檔案第 1～2 行已引入 `JvmTarget` 與 `KotlinJvmCompile`，但未套用）。建議明確加入：
  ```kotlin
  kotlin {
      compilerOptions {
          jvmTarget.set(JvmTarget.JVM_17)
      }
  }
  ```

### - [ ] 2.3 處理 `android.disallowKotlinSourceSets=false` 實驗性開關（[gradle.properties](../gradle.properties#L25-L26)）
* **現況**：`gradle.properties` 目前啟用 `android.disallowKotlinSourceSets=false` 以暫時繞過 KSP `2.2.10-2.0.2` 與 AGP 9 內建 Kotlin 的衝突，建置時已出現實驗性警告，且此逃生開關預計在未來 AGP 版本移除。
* **調整方案**：
  * **建議方案**：同步升級 Kotlin 與對應支援 AGP 9 built-in Kotlin 的 **KSP（例如 KSP `2.3.6+` 對應版本）**，完成後即可正式移除 `android.disallowKotlinSourceSets=false`。
  * **過渡方案**：若本次僅先升 AGP 9.5.2 而不升 Kotlin／KSP，需先實測 `android.disallowKotlinSourceSets=false` 在 AGP 9.5.2 是否仍可正常編譯，並排程後續移除。

### - [ ] 2.4 檢查 Gradle Wrapper 與本機 SDK 環境
* [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties#L4) 目前為 `gradle-9.7.1-bin.zip`，已滿足 AGP 9.5 最低 Gradle `9.6.0` 需求，升級後需確認無 Gradle 9.7 棄用錯誤。
* 於 Android Studio SDK Manager 安裝 **Android 17（API 37）SDK Platform** 與最新 **Build Tools（≥ 36.0.0）**。

---

## 3. 第三方與 AndroidX 套件相容性盤點

| 套件 | 目前版本 | 升級評估與建議 |
| :--- | :--- | :--- |
| Hilt | `2.60.1` | 已高於 AGP 9 built-in Kotlin 相容門檻（`2.59.2+`）；升級後驗證 KSP 程式碼生成與 DI 注入即可 |
| Room | `2.8.3` | 確認搭配新 AGP／KSP 時 `app/schemas/` 匯出與 DAO 產生正常 |
| AndroidX Biometric | `1.4.0-alpha02` | 目前為 Alpha（測試版），建議評估升級至較穩定的 Beta／Stable 版本並驗證 API 37 行為 |
| Compose BOM / Material 3 | `2025.06.00` | 升至 `compileSdk = 37` 後建議檢查 `MenuAnchorType.PrimaryNotEditable`（[SettingScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L210)）等實驗性／棄用 API |
| Core KTX / Activity / Navigation | `1.17.0` / `1.10.1` / `2.9.3` | 視 `compileSdk = 37` 編譯與 Lint（靜態程式碼檢查）結果決定是否小幅升版 |
| DataStore / Serialization | `1.1.7` / `1.9.0` | 若同步升級 Kotlin，須同步確認 `kotlinx-serialization-json` 與外掛版本相容 |

---

## 4. Android 17（API 37）平台行為變更影響評估

### 4.1 高度相關（須實際調整或強化）

#### - [ ] 大螢幕可調整視窗大小與方向強制生效（`targetSdk = 37`）
* **平台變更**：Android 17 在大螢幕裝置（最小寬度 `≥ 600dp`，含平板、摺疊機與桌面視窗模式）正式移除禁止縮放與鎖定螢幕方向的排除選項（opt-out），系統將忽略限制並強制允許任意縮放與旋轉。
* **對本專案的影響**：
  1. **組態變更狀態流失**：`EditorActivity` 重建時重複執行 `initViewData()` 會洗掉編輯內容、`DetailActivity` 於 `ON_RESUME` 重抓會覆蓋未送出的轉換／取樣輸入、`MainRoute` 的 `pendingAuthAction` 旋轉後會遺失（詳見 [ui-viewmodel-review.md](./ui-viewmodel-review.md) #7、#8）。
  2. **小高度／橫向視窗版面裁切**：[ConverterScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/ConverterScreen.kt#L38-L69) 外層 `Column` **沒有 `verticalScroll`**，且輸入框與結果卡各自寫死 `height(200.dp)`（合計超過 `400dp`）；在橫向或分割視窗模式下，底部「複製／儲存／清除」按鈕會被擠出畫面無法操作。
  3. [SettingScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L53-L122) 亦無外層捲動容器，在窄高視窗可能出現按鈕與卡片擠壓。
* **建議調整**：為 `ConverterScreen` 與 `SettingScreen` 加上垂直捲動與彈性高度，並修復組態變更時的狀態保存。

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

#### - [ ] AndroidKeyStore 加解密與 R8 混淆（Release 建置）
* **檔案**：[CryptographicUtility.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/tools/CryptographicUtility.kt)、[app/proguard-rules.pro](../app/proguard-rules.pro)
* **檢查重點**：
  * `CryptographicUtility` 使用標準 `AndroidKeyStore` + `AES/CBC/PKCS7Padding`，無受影響之棄用 API，但仍須實測既有已加密 Room 資料可正常解密。
  * Android 17 的 ART（Android Runtime，Android 執行時期）禁止透過反射修改 `static final` 欄位；升級後須建置並實測開啟 `isMinifyEnabled = true` 的 Release 版本，確認 Room、Hilt 與 Kotlinx Serialization 在 R8 下運作正常。
  * [app/proguard-rules.pro](../app/proguard-rules.pro#L23-L25) 內的 `-optimizationpasses 5`、`-dontusemixedcaseclassnames` 為舊版 ProGuard 指令，R8 會忽略或發出提示，可視需要整理。

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

完成實際升級後，記得同步更新以下文件中的版本記載：
* [AGENTS.md](../AGENTS.md)（更新 AGP 與 `compileSdk / targetSdk` 版本）
* [docs/README.md](./README.md)（更新技術棧版本表）
* [docs/development-guide.md](./development-guide.md)（更新環境需求與 AGP／SDK 版本）

---

## 6. 建議升級執行順序與驗證清單

1. **備份與分支**：確認工作區乾淨，建立獨立升級分支（例如 `upgrade/sdk37-agp952`）。
2. **安裝 SDK 37**：於 Android Studio SDK Manager 安裝 Android 17（API 37）SDK Platform。
3. **更新建置設定**：調整 `libs.versions.toml`（`agp = "9.5.2"`、統一 `kotlin-serialization`）、`app/build.gradle.kts`（`compileSdk/targetSdk = 37`、移除 `composeOptions`、補上 `JvmTarget.JVM_17`），並視情況同步升級 Kotlin／KSP 以移除 `android.disallowKotlinSourceSets=false`。
4. **執行編譯與靜態檢查**：
   ```bash
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ./gradlew lintDebug
   ```
5. **大螢幕與 API 37 回歸測試**：
   * 在 API 37 模擬器（手機＋平板／可折疊視窗縮放模式）驗證所有畫面、旋轉與分割畫面操作。
   * 實測加密筆記的新增、讀取、編輯、取樣、刪除與生物辨識驗證流程。
   * （經確認後）驗證 Release 混淆建置與既有資料解密相容性。
6. **同步更新文件**：更新 `AGENTS.md` 與 `docs/` 內相關版本號。
