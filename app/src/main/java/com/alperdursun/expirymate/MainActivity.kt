package com.alperdursun.expirymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.alperdursun.expirymate.ui.navigation.ExpiryMateApp
import com.alperdursun.expirymate.ui.theme.ExpiryMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpiryMateTheme {
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
