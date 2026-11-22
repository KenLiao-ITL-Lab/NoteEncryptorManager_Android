package com.itl.kglab.noteEncryptorManager

import com.itl.kglab.noteEncryptorManager.tools.SettingInputRegex
import com.itl.kglab.noteEncryptorManager.ui.screen.main.data.SettingScreenInfo
import com.itl.kglab.noteEncryptorManager.viewmodel.detail.DetailScreenUiState
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorNoteInfoData
import com.itl.kglab.noteEncryptorManager.viewmodel.editor.EditorViewState
import com.itl.kglab.noteEncryptorManager.viewmodel.main.ScreenState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UiStateAndRegexUnitTest {

    @Test
    fun editorViewState_initAndUpdate_preservesOriginalTimeAndCipherFields() {
        val state = EditorViewState()
        state.initNoteInfo(
            EditorNoteInfoData(
                id = 42L,
                title = "舊標題",
                time = "2026-11-22 09:00",
                note = "舊備註",
                input = "plaintext",
                output = "hashed",
                isPrivate = false
            )
        )

        state.updateNoteInfo(
            title = "新標題",
            note = "新備註",
            isPrivate = true
        )

        assertEquals(42L, state.noteInfo.id)
        assertEquals("新標題", state.noteInfo.title)
        assertEquals("2026-11-22 09:00", state.noteInfo.time)
        assertEquals("新備註", state.noteInfo.note)
        assertEquals("plaintext", state.noteInfo.input)
        assertEquals("hashed", state.noteInfo.output)
        assertTrue(state.noteInfo.isPrivate)
    }

    @Test
    fun editorViewState_setNewInfo_setsIdZeroAndCipherFields() {
        val state = EditorViewState()
        state.setNewInfo(input = "hello", output = "hash_value")

        assertEquals(0L, state.noteInfo.id)
        assertEquals("hello", state.noteInfo.input)
        assertEquals("hash_value", state.noteInfo.output)
    }

    @Test
    fun settingInputRegex_acceptsEmptyAndTwoDigitNumericInput() {
        assertTrue("".matches(SettingInputRegex.sampleIndexInputRegex))
        assertTrue("0".matches(SettingInputRegex.sampleIndexInputRegex))
        assertTrue("99".matches(SettingInputRegex.sampleIndexInputRegex))
        assertFalse("100".matches(SettingInputRegex.sampleIndexInputRegex))
        assertFalse("ab".matches(SettingInputRegex.sampleIndexInputRegex))

        assertTrue("".matches(SettingInputRegex.sampleSizeInputRegex))
        assertTrue("12".matches(SettingInputRegex.sampleSizeInputRegex))
        assertFalse("123".matches(SettingInputRegex.sampleSizeInputRegex))
    }

    @Test
    fun detailScreenUiState_defaultsToNotLoading() {
        val state = DetailScreenUiState()
        assertFalse(state.isLoading)
        assertTrue(state.copy(isLoading = true).isLoading)
    }

    @Test
    fun screenState_isBiometricEffective_requiresBothAppAndSystemReady() {
        assertFalse(
            ScreenState(
                settingInfo = SettingScreenInfo(isBiometricEnabled = false),
                isSystemBiometricReady = false
            ).isBiometricEffective
        )
        assertFalse(
            ScreenState(
                settingInfo = SettingScreenInfo(isBiometricEnabled = false),
                isSystemBiometricReady = true
            ).isBiometricEffective
        )
        assertFalse(
            ScreenState(
                settingInfo = SettingScreenInfo(isBiometricEnabled = true),
                isSystemBiometricReady = false
            ).isBiometricEffective
        )
        assertTrue(
            ScreenState(
                settingInfo = SettingScreenInfo(isBiometricEnabled = true),
                isSystemBiometricReady = true
            ).isBiometricEffective
        )
    }
}
