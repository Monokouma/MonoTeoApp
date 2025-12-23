package com.despaircorp.monoteo.ui.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.despaircorp.monoteo.ui.theme.MonoTeoTheme
import monoteo.composeapp.generated.resources.Res
import monoteo.composeapp.generated.resources.app_logo
import monoteo.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(Res.drawable.app_logo),
            contentDescription = "MonoTéo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.weight(1f))

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )

        Text("MonoTéo", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 20.dp))
    }
}

@Composable
@Preview
private fun LoadingScreenLightPreview() {
    MonoTeoTheme(darkTheme = false) {
        LoadingScreen()
    }
}

@Composable
@Preview
private fun LoadingScreenDarkPreview() {
    MonoTeoTheme(darkTheme = true) {
        LoadingScreen()
    }
}