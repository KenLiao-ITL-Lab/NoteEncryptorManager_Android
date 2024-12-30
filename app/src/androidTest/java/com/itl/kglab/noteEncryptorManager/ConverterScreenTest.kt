package com.itl.kglab.noteEncryptorManager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterInputSection
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterResultSection
import com.itl.kglab.noteEncryptorManager.ui.screen.main.ConverterScreenIdRes
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ConverterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_converter_input_display_text() {

        val inputContent = "TestText"
        composeTestRule.setContent {
            ConverterInputSection(
                inputValue = inputContent,
                onConvertClicked = {},
                onInputChange = {}
            )
        }

        composeTestRule
            .onNodeWithTag(ConverterScreenIdRes.TAG_INPUT_TEXT_FILED)
            .assertTextContains(inputContent)
    }


    @Test
    fun test_converter_input_text_state() {
        val inputContent = "This is test message."
        composeTestRule.setContent {
            var inputState by remember {
                mutableStateOf("")
            }

            ConverterInputSection(
                inputValue = inputState,
                onInputChange = { inputState = it },
                onConvertClicked = {}
            )
        }

        composeTestRule.onNodeWithTag(ConverterScreenIdRes.TAG_INPUT_TEXT_FILED).apply {
            performTextInput(inputContent)
            assertTextContains(inputContent)
        }
    }


    @Test
    fun test_converter_button_event() {
        val inputContent = "This is test message"
        var result = ""

        composeTestRule.setContent {
            var inputState by remember {
                mutableStateOf("")
            }

            ConverterInputSection(
                inputValue = inputState,
                onInputChange = {
                    inputState = it
                },
                onConvertClicked = {
                    result = inputState
                }
            )
        }

        val inputSection = composeTestRule
            .onNodeWithTag(ConverterScreenIdRes.TAG_INPUT_TEXT_FILED)
        val button = composeTestRule
            .onNodeWithTag(ConverterScreenIdRes.TAG_CONVERTER_BUTTON)

        inputSection.performTextInput(inputContent)
        button.apply { performClick() }

        Assert.assertEquals(result, inputContent)
    }

    @Test
    fun test_result_label_text() {
        val resultContent = "Result text"
        composeTestRule.setContent {
            ConverterResultSection(
                resultText = resultContent
            )
        }

        composeTestRule
            .onNodeWithTag(ConverterScreenIdRes.TAG_RESULT_LABEL)
            .assertTextContains(resultContent)
    }

}