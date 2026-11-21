# UI 元件與 ViewModel 健檢問題清單

本文件記錄針對 `com.itl.kglab.noteEncryptorManager.ui` 與 `com.itl.kglab.noteEncryptorManager.viewmodel` 進行程式碼檢視後所整理的潛在問題、狀態管理缺陷與改善建議，依風險等級分級並提供追蹤勾選清單。

> 檢視日期：2026-11-22

---

## 總覽與修復進度

| 風險等級 | 編號範圍 | 數量 | 重點說明 |
| :--- | :---: | :---: | :--- |
| 🔴 高風險 | #1 ～ #4 | 4 | 潛在閃退、資料覆蓋／遺失、核心導覽狀態失效 |
| 🟠 中風險 | #5 ～ #11 | 7 | 功能漏接、狀態不同步、組態變更洗掉輸入、觸控與捲動異常 |
| 🟡 低風險 | #12 ～ #15 | 4 | Compose 穩定性、深色模式對比、無障礙與程式碼清理 |

---

## 🔴 高風險（潛在閃退、資料遺失或核心狀態錯誤）

### - [ ] #1 `MainViewModel` 存在 `lateinit var hashTools` 競態閃退風險

* **影響檔案**：[MainViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/main/MainViewModel.kt#L24-L78)
* **問題說明**：`hashTools` 宣告為 `private lateinit var`，僅在 `init` 啟動的非同步 `viewModelScope.launch` 讀取完 DataStore 設定後才完成初始化。若使用者進入預設首頁 `ConverterScreen` 並立即點擊「轉換」，`convertInput()` 可能在 `hashTools` 初始化完成前執行，直接拋出 `UninitializedPropertyAccessException` 導致閃退。
* **修正建議**：
  * 將 `hashTools` 改為具預設值之變數（例如 `private var hashTools = HashTools(SettingInfo())`），待 `getSettingInfo()` 完成後再更新；或改由目前 `state.settingInfo.info` 即時取得。

---

### - [ ] #2 `DetailViewModel` 空取樣欄位按「轉換」閃退，且 `getHashTool()` 造成 Coroutine Leak（協程外洩）

* **影響檔案**：[DetailViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt#L59-L70)、[L108-L111](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt#L108-L111)、[L138-L149](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt#L138-L149)
* **問題說明**：
  1. `SettingInputRegex.sampleIndexInputRegex` 與 `sampleSizeInputRegex`（`^[0-9]{0,2}$`）允許使用者將欄位清空為 `""`。若清空後點擊「轉換（`convertMessage()`）」，後續 `updateSampleSetting()` 會直接執行 `settingState.sampleIndex.toInt()` 與 `settingState.sampleSize.toInt()`，立刻拋出 `NumberFormatException` 閃退。
  2. `getHashTool()` 每次執行轉換或取樣皆呼叫 `repository.getSettingInfo().stateIn(viewModelScope).value`，會於 `viewModelScope` 中不斷建立無法自動結束的新收集協程，造成資源浪費與外洩。
* **修正建議**：
  * `updateSampleSetting()` 改以 `checkIndexString()`／`checkSizeString()` 或 `toIntOrNull() ?: default` 安全轉型。
  * `getHashTool()` 改用 `repository.getSettingInfo().first()` 單次讀取目前設定。

---

### - [ ] #3 `EditorViewModel`／`EditorActivity` 編輯後遺失原始時間，且未等寫入完成即關閉頁面

* **影響檔案**：[EditorViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/editor/EditorViewModel.kt#L47-L92)、[EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt#L70-L75)
* **問題說明**：
  1. **原始時間被清空**：`EditorViewModel.findNoteInfo(id)` 建構 `EditorNoteInfoData` 時漏傳 `time = info.timeDesc`，導致 `time` 維持預設空字串 `""`。編輯畫面頂部無法顯示原始時間，按下儲存走 `updateNoteInfo()` 時更會將資料庫中原本的 `time_desc` 覆蓋為空字串。
  2. **未等儲存完成就結束 Activity**：`EditorActivity` 的 `onSaveClicked` 在呼叫非同步 `viewModel.saveNoteInfo()` 後同步立刻呼叫 `setResult(RESULT_OK)` 與 `finish()`。Activity 結束時 `viewModelScope` 會被取消，若加解密與 Room 寫入尚未完成，資料可能遺失或返回 `MainActivity` 時讀到舊資料。
* **修正建議**：
  * 於 `findNoteInfo()` 補上 `time = info.timeDesc`。
  * 於 `EditorViewModel` 儲存完成後透過一次性事件（如 `Channel`／`SharedFlow`）通知 UI 執行 `setResult(RESULT_OK)` 與 `finish()`，並在儲存期間防呆重複點擊。

---

### - [ ] #4 `MainBottomNavigationBar` 底部導覽選中狀態永遠比對失敗

* **影響檔案**：[MainBottomNavigationBar.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainBottomNavigationBar.kt#L23-L35)
* **問題說明**：`MainRoute` 採用 Navigation Compose 的 Type-Safe（型別安全）路由（`composable<MainBottomNavigationItem.Converter>`），此時 `navBackStackEntry?.destination?.route` 為完整套件類別名稱，但第 27 行寫成 `selected = item.route == currentRoute`（比對短字串 `"Converter"`），導致底部導覽列選中高亮永遠為 `false`。
* **修正建議**：
  * 改用 Navigation Compose 2.8+ 提供的型別安全比對 API：`navBackStackEntry?.destination?.hasRoute(item::class) == true`（並同步統一 `onClick` 的當前路由判斷）。

---

## 🟠 中風險（功能無效、狀態不同步或操作體驗異常）

### - [ ] #5 多處「長按可複製」卡片未實際綁定複製事件

* **影響檔案**：
  * [DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L108-L117)、[L207-L209](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L207-L209)、[L398-L434](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L398-L434)
  * [EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt#L245-L258)
  * [ConverterScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/ConverterScreen.kt#L50-L52)
* **問題說明**：
  * `DetailActivity` 雖已實作 `sampleTableEvent.onOutputLongClicked` 剪貼簿複製邏輯，但 `SimpleTable` 內兩個標示「長按可複製」的 `ContentTextCard`（`output` 與 `sampledMessage`）皆未傳入 `onContentLongClicked`；`DetailTable` 的 `onNoteLongClicked` 亦為空實作 `{}`。
  * `EditorActivity` 下方兩個唯讀 `ContentTextCard` 及 `ConverterScreen` 的 `ConverterResultSection` 同樣標示「長按可複製」卻未串接剪貼簿事件。
* **修正建議**：將所有標示「長按可複製」的卡片確實串接剪貼簿複製（並可視需要給予 `Toast` 或 `Snackbar` 回饋）。

---

### - [ ] #6 `SettingScreen` 演算法選項未與非同步設定同步，且含潛在越界與多餘狀態

* **影響檔案**：[SettingScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L46-L51)、[L109-L111](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L109-L111)、[L192-L196](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/SettingScreen.kt#L192-L196)
* **問題說明**：
  * `SettingScreen` 僅以 `LaunchedEffect(settingInfo.isBiometricEnabled)` 同步生物辨識狀態，未同步 `settingInfo.algorithmIndex`；若設定仍在載入中，下拉選單會停留在初始值 `0` 而非實際已存演算法。
  * `onConfirmClicked` 直接存取 `hashTypeList[selectedItemIndex]`，若清單為空或索引異常會拋出 `IndexOutOfBoundsException`。
  * `DropMenuItem` 內宣告的 `textFieldState = rememberTextFieldState(defaultItemName)` 未實際綁定到 `OutlinedTextField`，屬於無效冗餘狀態。
* **修正建議**：補上 `LaunchedEffect(settingInfo.algorithmIndex)` 同步、使用 `getOrNull(selectedItemIndex)` 防呆，並移除未使用的 `textFieldState`。

---

### - [ ] #7 `MainRoute` 生物辨識待處理狀態未清理，且旋轉螢幕會遺失

* **影響檔案**：[MainRoute.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainRoute.kt#L86-L158)
* **問題說明**：
  * 當生物辨識回傳 `AuthenticationError`（如使用者主動取消）、`AuthenticationFailed`、`FeatureUnavailable` 或 `HardwareUnavailable` 時，`pendingAuthAction` 未重設為 `null`，且除 `Log.d` 外缺乏使用者提示。
  * `pendingAuthAction` 與 `showEnrollDialog` 僅以 `remember` 存於 Composable 中；若驗證對話框開啟期間發生螢幕旋轉，重建後 `pendingAuthAction` 會被清空，即使驗證成功也不會執行原操作。
* **修正建議**：將 `pendingAuthAction` 移入 `MainViewModel` 保存，並於驗證取消、失敗或錯誤分支明確重置狀態與顯示適當提示。

---

### - [ ] #8 螢幕旋轉或切回前台會覆蓋未儲存的編輯與轉換內容

* **影響檔案**：[EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt#L49)、[L85-L99](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt#L85-L99)、[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L80-L87)
* **問題說明**：
  * `EditorActivity.onCreate()` 每次重建皆無條件呼叫 `initViewData()`，旋轉螢幕會覆蓋使用者正在輸入的標題、備註與勾選狀態。
  * `DetailActivity` 每次進入 `Lifecycle.State.RESUMED`（包含從背景切回、下拉通知欄或旋轉螢幕）皆會重新從資料庫覆寫 `settingState`，洗掉使用者尚未按下「轉換」或「取樣」的輸入內容。
* **修正建議**：
  * `EditorActivity` 僅於 `savedInstanceState == null`（或 ViewModel 尚未初始化）時呼叫 `initViewData()`。
  * `DetailActivity` 區分「首次載入／從編輯頁返回（用 `ActivityResultLauncher` 刷新筆記基本資訊）」與一般 `ON_RESUME`，避免覆寫使用者未送出的取樣與轉換輸入。

---

### - [ ] #9 `DetailActivity` 原文輸入框鍵盤型別誤設為 `KeyboardType.Decimal`

* **影響檔案**：[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L384-L387)
* **問題說明**：`SimpleTable` 中的密文／原文輸入框將 `keyboardType` 設為 `KeyboardType.Decimal`，導致使用者只能彈出純數字與小數點鍵盤，無法輸入一般文字或英數密碼。
* **修正建議**：改為 `KeyboardType.Text`（或預設值）。

---

### - [ ] #10 `ContentTextCard` 的 `Modifier` 順序影響觸控範圍，且固定高度時無法捲動

* **影響檔案**：[ContentTextCard.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/ContentTextCard.kt#L32-L53)、[ConverterScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/ConverterScreen.kt#L112-L118)
* **問題說明**：
  * `Text` 的修飾子順序為 `.border(...).fillMaxWidth().padding(16.dp).combinedClickable(...)`，導致長按觸控區與水波紋（Ripple）只涵蓋內距之內的文字範圍，點擊卡片邊緣空白處無反應。
  * 在 `ConverterScreen` 傳入 `Modifier.height(200.dp)` 給外層 `Column` 時，內層 `Box` 未配置 `weight(1f)` 與 `verticalScroll`，長輸出內容可能被裁切或擠壓底部提示文字。
* **修正建議**：將 `.clip(RoundedCornerShape(8.dp))` 與 `.combinedClickable(...)` 移到 `.padding(16.dp)` 之前，並針對固定高度情境提供可捲動容器。

---

### - [ ] #11 `NoteListScreen` 的 `LazyColumn` 未提供穩定 `key`，且 `NoteListItem` 的 `modifier` 套用位置錯誤

* **影響檔案**：[NoteListScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/NoteListScreen.kt#L73)、[L120-L139](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/NoteListScreen.kt#L120-L139)
* **問題說明**：
  * `items(noteList)` 未設定 `key = { it.id }`；因 `NoteListItem` 內部使用 `remember` 保存 `menuExpanded`，刪除列表項目時容易發生狀態錯位與多餘重組（Recomposition）。
  * `NoteListItem` 接收的 `modifier` 參數套用在內層 `Column` 而非根元件 `OutlinedCard`，違反 Jetpack Compose 元件慣例。
* **修正建議**：改為 `items(items = noteList, key = { it.id })`，並將 `modifier` 套用至 `OutlinedCard`。

---

## 🟡 低風險（穩定性、深色模式、無障礙與程式碼清理）

### - [ ] #12 `DetailScreenUiState` 使用可變 `var` 屬性影響 Compose 穩定性

* **影響檔案**：[DetailScreenUiState.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailScreenUiState.kt#L3-L5)
* **問題說明**：`var isLoading: Boolean = false` 使用 `var` 會讓 Compose 編譯器將該資料類別標記為 Unstable（不穩定），降低重組跳過（Skipping）效率。
* **修正建議**：改為不可變 `val isLoading: Boolean = false`。

---

### - [ ] #13 `LoadingDialog` 外層全螢幕 `Box` 層級冗餘

* **影響檔案**：[LoadingDialog.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/dialog/LoadingDialog.kt#L17-L38)
* **問題說明**：`Dialog` 本身即會建立獨立視窗並依 `DialogProperties` 阻擋返回與外部點擊，外層再包 `Box(Modifier.fillMaxSize().pointerInput(Unit) {})` 屬於多餘版面階層。
* **修正建議**：移除外層 `Box`，直接使用 `Dialog` 顯示 `CircularProgressIndicator`。

---

### - [ ] #14 無障礙描述、深色模式配色與多語系字串抽離

* **影響檔案**：
  * [TitleFuncButtons.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/TitleFuncButtons.kt#L94-L99)
  * [ContentTextCard.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/ContentTextCard.kt#L40)、[TableDivider.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/component/TableDivider.kt#L30)、[NoteListScreen.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/NoteListScreen.kt#L167)、[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L278)
  * [DeleteConfirmDialog.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/dialog/DeleteConfirmDialog.kt#L23-L39)、[MainRoute.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainRoute.kt#L168-L196)、[EditorActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/editor/EditorActivity.kt#L199-L234)
* **問題說明**：
  * `TitleFuncButtons` 內所有按鈕（返回、編輯、儲存）的 `Icon` 皆寫死 `contentDescription = "Return Icon"`（因右側已有 `Text`，應設為 `null` 或對應 `text`）。
  * 多處硬編碼 `Color.Gray` 與 `Color.LightGray`，在深色主題下可能對比不足，建議改用 `MaterialTheme.colorScheme.outline`／`onSurfaceVariant`。
  * 多數 UI 提示與對話框文字直接寫死於 Kotlin 檔中，尚未統一抽至 `app/src/main/res/values/strings.xml`。

---

### - [ ] #15 ViewModel 依賴介面化與未使用程式碼清理

* **影響檔案**：
  * [MainViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/main/MainViewModel.kt#L20-L44)、[DetailViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/detail/DetailViewModel.kt#L19-L21)、[EditorViewModel.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/viewmodel/editor/EditorViewModel.kt#L16-L18)
  * [MainRoute.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/main/route/MainRoute.kt#L31-L35)、[DetailActivity.kt](../app/src/main/java/com/itl/kglab/noteEncryptorManager/ui/screen/detail/DetailActivity.kt#L283-L302)
* **問題說明**：
  * 三個 ViewModel 直接注入具體類別 `MainRepository` 而非 `MainRepositoryInterface`，不利於單元測試抽換假資料來源（Fake Repository）。
  * `MainViewModel.clearPreviousPreferences()`、`DetailActivity.BackTextButton` 及 `MainRoute` 中已棄用的 `LocalClipboardManager`／`AnnotatedString` 匯入皆未使用。
* **修正建議**：透過 Hilt `@Binds` 綁定 `MainRepositoryInterface` 供 ViewModel 注入，並清理未使用的函式與匯入。
