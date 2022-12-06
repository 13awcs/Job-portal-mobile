package com.example.joblist.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.joblist.R
import com.example.joblist.ui.login.LoginActivity

class FingerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger)
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        this@FingerActivity,
                        "Xác thực sinh trắc thành công",
                        Toast.LENGTH_LONG
                    ).show()
                    loadData()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@FingerActivity, errString, Toast.LENGTH_LONG).show()
                    this@FingerActivity.finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        this@FingerActivity,
                        "Xác thực sinh trắc không thành công. Vui lòng thử lại",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        val promptInfo = PromptInfo.Builder()
            .setTitle("Xác thực người dùng")
            .setDescription("Quét vân tay để xác thực danh tính của bạn")
            .setNegativeButtonText("Thoát")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun loadData() {
        Handler().postDelayed({
            val intent = Intent(this@FingerActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}