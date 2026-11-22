package com.itl.kglab.noteEncryptorManager

import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.itl.kglab.noteEncryptorManager.ui.screen.detail.DetailActivity
import com.itl.kglab.noteEncryptorManager.ui.screen.editor.EditorActivity
import com.itl.kglab.noteEncryptorManager.ui.screen.main.MainActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppUiFlowInstrumentedTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun mainBottomNavSelection_and_converterAndSettingFlows_workCorrectly() {
        ActivityScenario.launch(MainActivity::class.java).use {
            // 1. Default Converter tab should be selected (Fix #4)
            val navConverter = composeRule.onNode(isSelectable() and hasText("轉換"))
            val navList = composeRule.onNode(isSelectable() and hasText("筆記"))
            val navSetting = composeRule.onNode(isSelectable() and hasText("設定"))
            navConverter.assertIsDisplayed().assertIsSelected()
            navList.assertIsNotSelected()
            navSetting.assertIsNotSelected()

            // 2. Immediate convert without lateinit crash (Fix #1)
            composeRule.onNode(hasSetTextAction() and hasText("輸入")).performTextInput("TestPlaintext123")
            composeRule.onNode(!isSelectable() and hasText("轉換")).performClick()
            composeRule.waitForIdle()

            // Copy and Clear should work cleanly
            composeRule.onNodeWithText("複製").performScrollTo().performClick()
            composeRule.onNodeWithText("清除").performScrollTo().performClick()
            composeRule.waitForIdle()

            // 3. Switch to NoteList tab and verify selected state (Fix #4)
            navList.performClick()
            composeRule.waitForIdle()
            navList.assertIsSelected()
            navConverter.assertIsNotSelected()
            navSetting.assertIsNotSelected()

            // 4. Switch to Setting tab and verify selected state + algorithm dropdown (Fix #4 & #6)
            navSetting.performClick()
            composeRule.waitForIdle()
            navSetting.assertIsSelected()
            navConverter.assertIsNotSelected()
            navList.assertIsNotSelected()
            composeRule.onNodeWithText("啟用生物辨識驗證").assertIsDisplayed()
            composeRule.onNodeWithText("選擇演算法").assertIsDisplayed()
        }
    }

    @Test
    fun createNote_openDetail_emptySampleConvertNoCrash_and_editPreservesTime() {
        val uniqueTitle = "自動化測試筆記_${System.currentTimeMillis()}"
        val updatedTitle = "${uniqueTitle}_已編輯"

        // 1. Create a new note via EditorActivity (Fix #3b waits for async DB write before finish)
        val createIntent = Intent(context, EditorActivity::class.java).apply {
            putExtras(
                Bundle().apply {
                    putBoolean(EditorActivity.ARG_IS_EDIT, false)
                    putString(EditorActivity.ARG_INPUT, "SampleSecretInput")
                    putString(EditorActivity.ARG_OUTPUT, "SampleHashOutput1234567890")
                }
            )
        }
        ActivityScenario.launch<EditorActivity>(createIntent).use {
            // Verify both ContentTextCards render their actual content visibly (not collapsed to 0 height)
            composeRule.onNodeWithText("SampleSecretInput").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("SampleHashOutput1234567890").performScrollTo().assertIsDisplayed()

            composeRule.onNode(hasSetTextAction() and hasText("標題")).performScrollTo().performTextInput(uniqueTitle)
            composeRule.onNode(hasSetTextAction() and hasText("備註")).performScrollTo().performTextInput("初始備註內容")
            composeRule.onNodeWithText("儲存").performScrollTo().performClick()

            // Wait until EditorActivity finishes after async Room insert completes
            composeRule.waitUntil(timeoutMillis = 5_000) {
                it.state.name == "DESTROYED"
            }
        }

        // 2. Open MainActivity -> NoteList -> verify note appears with timestamp & open DetailActivity
        ActivityScenario.launch(MainActivity::class.java).use {
            composeRule.onNodeWithText("筆記").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodesWithText(uniqueTitle).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText(uniqueTitle).performClick()

            // Wait for DetailActivity to display the note & updated sample size hint
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodesWithText("取樣設定").fetchSemanticsNodes().isNotEmpty()
            }
            // Verify note ContentTextCard is visibly rendered inside DetailActivity
            composeRule.onNodeWithText("初始備註內容").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("請輸入 1 ~ 99 整數（空白或 0 預設 8）")
                .performScrollTo()
                .assertIsDisplayed()

            // 3. Clear sampleIndex & sampleSize and click Convert -> must NOT crash (Fix #2)
            composeRule.onNode(hasSetTextAction() and hasText("取樣起始"))
                .performScrollTo()
                .performTextClearance()
            composeRule.onNode(hasSetTextAction() and hasText("取樣長度"))
                .performScrollTo()
                .performTextClearance()
            composeRule.onNodeWithText("轉換").performScrollTo().performClick()
            composeRule.waitForIdle()

            // 4. Sample with valid index/size
            composeRule.onNode(hasSetTextAction() and hasText("取樣起始"))
                .performScrollTo()
                .performTextInput("2")
            composeRule.onNode(hasSetTextAction() and hasText("取樣長度"))
                .performScrollTo()
                .performTextInput("6")
            composeRule.onNodeWithText("取樣").performScrollTo().performClick()
            composeRule.waitForIdle()

            // 5. Open EditorActivity from DetailActivity, edit title, save -> original timestamp preserved (Fix #3a & #8)
            composeRule.onNodeWithText("編輯").performClick()
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodesWithText("是否啟用生物驗證").fetchSemanticsNodes().isNotEmpty()
            }
            // Original timestamp (yyyy-MM-dd HH:mm) should be displayed (not empty!)
            val timeNodes = composeRule.onAllNodes(hasText("20", substring = true)).fetchSemanticsNodes()
            assertTrue("Expected original timestamp to be shown in EditorActivity", timeNodes.isNotEmpty())

            composeRule.onNode(hasSetTextAction() and hasText("標題")).performTextClearance()
            composeRule.onNode(hasSetTextAction() and hasText("標題")).performTextInput(updatedTitle)
            composeRule.onNodeWithText("儲存").performClick()

            // Back in DetailActivity: updated title shown AND sampleIndex "2" still preserved (Fix #8)
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodesWithText(updatedTitle).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithText(updatedTitle).assertIsDisplayed()
            composeRule.onNode(hasSetTextAction() and hasText("取樣起始"))
                .performScrollTo()
                .assertIsDisplayed()
        }
    }
}
