# UI 元件與 ViewModel 健檢問題清單

本文件記錄針對 `com.itl.kglab.noteEncryptorManager.ui` 與 `com.itl.kglab.noteEncryptorManager.viewmodel` 進行程式碼檢視後所整理的潛在問題、狀態管理缺陷與改善建議，依風險等級分級並提供追蹤勾選清單。

> 檢視日期：2026-11-22｜最後更新：2026-11-25（全部 15 項問題皆已修正完成）

---

## 總覽與修復進度

| 風險等級 | 編號範圍 | 進度 | 重點說明 |
| :--- | :---: | :---: | :--- |
| 🔴 高風險 | #1 ～ #4 | 4 / 4 已修正 | 潛在閃退、資料覆蓋／遺失、核心導覽狀態失效 |
| 🟠 中風險 | #5 ～ #11 | 7 / 7 已修正 | 功能漏接、狀態不同步、組態變更洗掉輸入、觸控與捲動異常 |
| 🟡 低風險 | #12 ～ #15 | 4 / 4 已修正 | Compose 穩定性、深色模式對比、無障礙、字串抽離與程式碼清理 |

---

## 🔴 高風險（潛在閃退、資料遺失或核心狀態錯誤）

### - [x] #1 `MainViewModel` 存在 `lateinit var hashTools` 競態閃退風險

* **影響檔案**：[MainViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/main/MainViewModel.kt)
* **問題說明**：`hashTools` 原為 `private lateinit var`，在 `init` 非同步讀取 DataStore 前呼叫 `convertInput()` 會拋出 `UninitializedPropertyAccessException`。
* **修復方式**：已改為初始化預設實例 `HashTools(SettingInfo())`，並於 `getSettingInfo()` 完成後更新。

---

### - [x] #2 `DetailViewModel` 空取樣欄位按「轉換」閃退，且 `getHashTool()` 造成 Coroutine Leak（協程外洩）

* **影響檔案**：[DetailViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt)
* **問題說明**：清空取樣輸入框後按「轉換」執行 `.toInt()` 會拋出 `NumberFormatException`；`getHashTool()` 每次呼叫 `.stateIn(viewModelScope).value` 會累積協程收集器。
* **修復方式**：`updateSampleSetting()` 已改用 `toIntOrNull() ?: default` 安全轉型並加上 `id <= 0L` 防護；`getHashTool()` 已改用 `repository.getSettingInfo().first()`，並以 `try / finally` 確保 `showLoading(false)` 必定還原。

---

### - [x] #3 `EditorViewModel`／`EditorActivity` 編輯後遺失原始時間，且未等寫入完成即關閉頁面

* **影響檔案**：[EditorViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/editor/EditorViewModel.kt)、[EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt)
* **問題說明**：`findNoteInfo()` 漏傳 `time = info.timeDesc` 導致編輯儲存清空原始時間；點擊儲存未等非同步寫入完成即呼叫 `finish()`。
* **修復方式**：已於 `findNoteInfo()` 補回 `time = info.timeDesc`；新增 `isSaving` 防重入與 `saveCompleted` 事件流，改由 `EditorActivity` 收到完成事件後才呼叫 `setResult(RESULT_OK)` 與 `finish()`，寫入期間顯示 `LoadingDialog`。

---

### - [x] #4 `MainBottomNavigationBar` 底部導覽選中狀態永遠比對失敗

* **影響檔案**：[MainBottomNavigationBar.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainBottomNavigationBar.kt)
* **問題說明**：Type-Safe 路由下 `destination.route` 為完整類別名，原程式比對短字串 `item.route` 導致選中高亮失效。
* **修復方式**：已改用 `currentDestination?.hasRoute(item::class) == true` 統一比對 `selected` 與 `onClick` 條件。

---

## 🟠 中風險（功能無效、狀態不同步或操作體驗異常）

### - [x] #5 多處「長按可複製」卡片未實際綁定複製事件

* **影響檔案**：[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt)、[EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt)、[ConverterScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/ConverterScreen.kt)
* **修復方式**：已將 `ConverterScreen` 結果卡、`DetailActivity`（備註、轉換結果、取樣結果）與 `EditorActivity`（原文、密文）全部接上長按複製至剪貼簿與 `Toast` 提示。

---

### - [x] #6 `SettingScreen` 演算法選項未與非同步設定同步，且含潛在越界與多餘狀態

* **影響檔案**：[SettingScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt)
* **修復方式**：已補上 `LaunchedEffect(settingInfo.algorithmIndex)` 同步、改用 `getOrNull(selectedItemIndex)` 防越界、移除未使用之 `rememberTextFieldState`，並為外層補上 `verticalScroll`。

---

### - [x] #7 `MainRoute` 生物辨識待處理狀態未清理，且旋轉螢幕會遺失

* **影響檔案**：[MainRoute.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainRoute.kt)、[MainViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/main/MainViewModel.kt)
* **修復方式**：已將 `pendingAuthAction` 移至 `MainViewModel` 保存以跨組態變更，`showEnrollDialog` 改用 `rememberSaveable`，並於驗證錯誤／硬體不支援／未設定時清除 `pendingAuthAction` 且提供 `Toast` 回饋。

---

### - [x] #8 螢幕旋轉或切回前台會覆蓋未儲存的編輯與轉換內容

* **影響檔案**：[EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt)、[EditorViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/editor/EditorViewModel.kt)、[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt)、[DetailViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt)
* **修復方式**：
  * `EditorViewModel` 加上 `isInitialized` 防護（`setNewInfoIfNeeded`／`findNoteInfoIfNeeded`），旋轉螢幕不再覆蓋編輯中內容。
  * `DetailActivity` 改為初次載入 `loadNoteInfoIfNeeded(noteId)`，並透過 `editorLauncher` 在編輯頁回傳 `RESULT_OK` 時呼叫 `refreshNoteInfo(noteId)` 僅刷新基本資訊，不覆寫未送出的轉換／取樣輸入。

---

### - [x] #9 `DetailActivity` 原文輸入框鍵盤型別誤設為 `KeyboardType.Decimal`

* **影響檔案**：[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt)
* **修復方式**：已改為 `KeyboardType.Text`。

---

### - [x] #10 `ContentTextCard` 的 `Modifier` 順序影響觸控範圍，且固定高度時無法捲動

* **影響檔案**：[ContentTextCard.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/ContentTextCard.kt)、[ConverterScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/ConverterScreen.kt)
* **修復方式**：已調整為先 `.clip`／`.border`／`.combinedClickable` 再 `.verticalScroll` 與 `.padding(16.dp)`，並搭配 `weight(1f, fill = false)` 支援固定高度捲動；`ConverterScreen` 亦補上外層垂直捲動以適配橫向／分割視窗。

---

### - [x] #11 `NoteListScreen` 的 `LazyColumn` 未提供穩定 `key`，且 `NoteListItem` 的 `modifier` 套用位置錯誤

* **影響檔案**：[NoteListScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/NoteListScreen.kt)
* **修復方式**：已改為 `items(items = noteList, key = { it.id })`，並將 `modifier` 正確套用至根元件 `OutlinedCard`。

---

## 🟡 低風險（穩定性、深色模式、無障礙與程式碼清理）

### - [x] #12 `DetailScreenUiState` 使用可變 `var` 屬性影響 Compose 穩定性

* **影響檔案**：[DetailScreenUiState.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailScreenUiState.kt)
* **修復方式**：已改為不可變 `val isLoading: Boolean = false`。

---

### - [x] #13 `LoadingDialog` 外層全螢幕 `Box` 層級冗餘

* **影響檔案**：[LoadingDialog.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/dialog/LoadingDialog.kt)
* **修復方式**：已移除多餘外層 `Box`，直接使用 `Dialog`。

---

### - [x] #14 無障礙描述、深色模式配色與多語系字串抽離

* **修復方式**：
  * [TitleFuncButtons.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/TitleFuncButtons.kt) 已將寫死的 `contentDescription = "Return Icon"` 改為 `null`（避免覆蓋右側文字標籤）。
  * [ContentTextCard.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/ContentTextCard.kt)、[TableDivider.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/TableDivider.kt)、[NoteListScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/NoteListScreen.kt)、[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt) 已將硬編碼 `Color.Gray`／`Color.LightGray` 改用 `MaterialTheme.colorScheme.onSurfaceVariant`／`outlineVariant`。
  * 已將全部 UI 與生物辨識文案（非 `@Preview` 範例資料）抽離至 [strings.xml](../app/src/main/res/values/strings.xml)，並同步修正 [MainBottomNavigationItem.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainBottomNavigationItem.kt) 註解目標警告與移除 [HashTools.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/tools/HashTools.kt) 無效 `.apply`。

---

### - [x] #15 ViewModel 依賴介面化與未使用程式碼清理

* **影響檔案**：[RepositoryProviderModule.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/di/RepositoryProviderModule.kt)、[MainViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/main/MainViewModel.kt)、[DetailViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt)、[EditorViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/editor/EditorViewModel.kt)、[MainRoute.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainRoute.kt)、[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt)
* **修復方式**：已改用 Hilt `@Binds` 綁定 `MainRepositoryInterface` 供三個 ViewModel 注入，清理 `MainViewModel` 未使用的 `clearPreviousPreferences()` 包裝方法、`BackTextButton` 與已棄用的 `LocalClipboardManager` 匯入，並新增 [UiStateAndRegexUnitTest.kt](../app/src/test/java/com/itl/kglab/noteEncryptorManager/UiStateAndRegexUnitTest.kt)。
