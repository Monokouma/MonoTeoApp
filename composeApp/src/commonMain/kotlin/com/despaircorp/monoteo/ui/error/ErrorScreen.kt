package com.despaircorp.monoteo.ui.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.despaircorp.monoteo.ui.theme.MonoTeoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ErrorScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "😕",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
@Preview
private fun ErrorScreenLightPreview() {
    MonoTeoTheme(darkTheme = false) {
        ErrorScreen(
            message = "Une erreur est survenue",
        )
    }
}

@Composable
@Preview
private fun ErrorScreenDarkPreview() {
    MonoTeoTheme(darkTheme = true) {
        ErrorScreen(
            message = "Une erreur est survenue",
        )
    }
}