package com.itl.kglab.noteEncryptorManager.manager

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val context: FragmentActivity
) {

    private val resultChannel = Channel<BioAuthResult>()
    private val promptResult = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title: String,
        desc: String
    ) {
        val manager = BiometricManager.from(context)

        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or ERROR_NO_DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }

        val promptInfo = getPromptInfo(
            title = title,
            desc = desc,
            authenticators = authenticators
        )

        checkBioAuthSupportState(manager, authenticators)

        val prompt = BiometricPrompt(
            context,
            getPromptCallback()
        )

        prompt.authenticate(promptInfo)
    }

    private fun getPromptInfo(
        title: String,
        desc: String,
        authenticators: Int
    ): PromptInfo {
        return PromptInfo.Builder().apply {
            setTitle(title)
            setDescription(desc)
            setAllowedAuthenticators(authenticators)

            if (Build.VERSION.SDK_INT < 30) {
                setNegativeButtonText("取消")
            }
        }.build()

    }

    private fun getPromptCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                resultChannel.trySend(BioAuthResult.AuthenticationError(errString.toString()))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                resultChannel.trySend(BioAuthResult.AuthenticationSuccess)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                resultChannel.trySend(BioAuthResult.AuthenticationFailed)
            }
        }
    }

    private fun checkBioAuthSupportState(
        manager: BiometricManager,
        authenticators: Int
    ) {
        when(manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BioAuthResult.HardwareUnavailable)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BioAuthResult.FeatureUnavailable)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BioAuthResult.AuthenticationNotSet)
            }
            else -> Unit
        }
    }

    sealed class BioAuthResult {
        data object HardwareUnavailable: BioAuthResult()
        data object FeatureUnavailable: BioAuthResult()
        data object AuthenticationNotSet: BioAuthResult()

        data object AuthenticationSuccess: BioAuthResult()
        data object AuthenticationFailed: BioAuthResult()

        data class AuthenticationError(val errorMessage: String): BioAuthResult()
    }
}