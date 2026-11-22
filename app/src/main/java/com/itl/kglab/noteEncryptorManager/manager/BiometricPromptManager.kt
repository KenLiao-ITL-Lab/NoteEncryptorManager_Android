package com.itl.kglab.noteEncryptorManager.manager

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity
import com.itl.kglab.noteEncryptorManager.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val context: FragmentActivity
) {

    private val resultChannel = Channel<BioAuthResult>()
    val promptResult = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title: String,
        desc: String
    ) {
        resultChannel.trySend(BioAuthResult.Init) // 重設狀態，觸發Compose SideEffect

        val manager = BiometricManager.from(context)

        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL
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

    fun checkBiometricSupport(): BioAuthResult {
        val manager = BiometricManager.from(context)
        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        }
        return when(manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BioAuthResult.AuthenticationSuccess
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BioAuthResult.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BioAuthResult.FeatureUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BioAuthResult.AuthenticationNotSet
            else -> BioAuthResult.FeatureUnavailable
        }
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
            if ((authenticators and DEVICE_CREDENTIAL) == 0) {
                setNegativeButtonText(context.getString(R.string.common_cancel))
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
        resultChannel.trySend(BioAuthResult.Init) // 重設狀態，觸發Compose SideEffect

        when(manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BioAuthResult.HardwareUnavailable)
                resultChannel.trySend(BioAuthResult.Init)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BioAuthResult.FeatureUnavailable)
                resultChannel.trySend(BioAuthResult.Init)
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BioAuthResult.AuthenticationNotSet)
                resultChannel.trySend(BioAuthResult.Init)
            }
            else -> Unit
        }
    }

    sealed class BioAuthResult {
        data object Init: BioAuthResult()
        data object HardwareUnavailable: BioAuthResult()
        data object FeatureUnavailable: BioAuthResult()
        data object AuthenticationNotSet: BioAuthResult()

        data object AuthenticationSuccess: BioAuthResult()
        data object AuthenticationFailed: BioAuthResult()

        data class AuthenticationError(val errorMessage: String): BioAuthResult()
    }

    companion object {
        fun allowedAuthenticators(): Int {
            return if (Build.VERSION.SDK_INT >= 30) {
                BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            } else {
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            }
        }

        fun isSystemBiometricReady(context: Context): Boolean {
            val manager = BiometricManager.from(context)
            return manager.canAuthenticate(allowedAuthenticators()) == BiometricManager.BIOMETRIC_SUCCESS
        }
    }
}