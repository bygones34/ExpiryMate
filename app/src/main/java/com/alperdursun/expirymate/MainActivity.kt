package com.alperdursun.expirymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alperdursun.expirymate.domain.model.AppThemeMode
import com.alperdursun.expirymate.ui.navigation.ExpiryMateApp
import com.alperdursun.expirymate.ui.theme.ExpiryMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settingsRepository = (application as ExpiryMateApplication).container.settingsRepository

        setContent {
            val themeMode by settingsRepository.themeMode.collectAsStateWithLifecycle(initialValue = AppThemeMode.SYSTEM)
            val dynamicColorsEnabled by settingsRepository.isDynamicColorsEnabled.collectAsStateWithLifecycle(initialValue = true)

            ExpiryMateTheme(
                themeMode = themeMode,
                dynamicColor = dynamicColorsEnabled
            ) {
                ExpiryMateApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ExpiryMateTheme {
        ExpiryMateApp()
    }
}
