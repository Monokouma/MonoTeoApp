package com.despaircorp.monoteo.ui.background.drizzle

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin
import kotlin.random.Random

private data class DrizzleDrop(
    val x: Float,
    val y: Float,
    var z: Float,
    val size: Float,
    val speed: Float,
    val drift: Float
)

private data class SoftSplash(
    val x: Float,
    val y: Float,
    var progress: Float,
    val size: Float
)

private data class ScreenMist(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2A3040),
        Color(0xFF3D4555),
        Color(0xFF4A5568),
        Color(0xFF5A6678)
    )
)

private val dropColor = Color(0xFFB8C8D8)
private val mistColor = Color(0xFFD0DDE8)
private val splashColor = Color(0xFFA0B8C8)

@Suppress("EffectKeys")
@Composable
fun DrizzleBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var drops by remember { mutableStateOf(emptyList<DrizzleDrop>()) }
    var splashes by remember { mutableStateOf(emptyList<SoftSplash>()) }
    var mists by remember { mutableStateOf(emptyList<ScreenMist>()) }
    var mistPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            drops = List(60) {
                DrizzleDrop(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    z = Random.nextFloat(),
                    size = Random.nextFloat() * 1.5f + 0.5f,
                    speed = Random.nextFloat() * 0.012f + 0.008f,
                    drift = Random.nextFloat() * 0.5f - 0.25f
                )
            }
            mists = List(25) {
                ScreenMist(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    alpha = Random.nextFloat() * 0.3f + 0.1f,
                    size = Random.nextFloat() * 40f + 20f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val newSplashesList = mutableListOf<SoftSplash>()
        val newMistsList = mutableListOf<ScreenMist>()

        while (true) {
            withFrameMillis {
                newSplashesList.clear()
                newMistsList.clear()

                mistPhase = (mistPhase + 0.005f) % 6.28f

                drops = drops.map { drop ->
                    val nextZ = drop.z - drop.speed
                    if (nextZ <= 0f) {
                        newSplashesList.add(
                            SoftSplash(
                                x = drop.x,
                                y = drop.y,
                                progress = 0f,
                                size = drop.size * 3f
                            )
                        )

                        if (Random.nextFloat() > 0.85f) {
                            newMistsList.add(
                                ScreenMist(
                                    x = drop.x + Random.nextFloat() * 20f - 10f,
                                    y = drop.y + Random.nextFloat() * 20f - 10f,
                                    alpha = 0.4f,
                                    size = Random.nextFloat() * 15f + 8f
                                )
                            )
                        }

                        drop.copy(
                            z = 1f,
                            x = Random.nextFloat() * screenWidth,
                            y = Random.nextFloat() * screenHeight,
                            speed = Random.nextFloat() * 0.012f + 0.008f,
                            drift = Random.nextFloat() * 0.5f - 0.25f
                        )
                    } else {
                        drop.copy(z = nextZ)
                    }
                }

                splashes = (splashes + newSplashesList).mapNotNull { splash ->
                    val next = splash.progress + 0.03f
                    if (next >= 1f) null else splash.copy(progress = next)
                }

                mists = (mists + newMistsList).mapNotNull { mist ->
                    val nextAlpha = mist.alpha - 0.001f
                    if (nextAlpha <= 0.05f) {
                        mist.copy(
                            x = Random.nextFloat() * screenWidth,
                            y = Random.nextFloat() * screenHeight,
                            alpha = Random.nextFloat() * 0.3f + 0.1f
                        )
                    } else {
                        mist.copy(alpha = nextAlpha)
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        val mistOverlayAlpha = (sin(mistPhase) * 0.03f + 0.05f).coerceIn(0f, 0.08f)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = mistOverlayAlpha),
                            Color.Transparent,
                            Color.White.copy(alpha = mistOverlayAlpha * 0.5f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            mists.forEach { mist ->
                val pulsingAlpha = mist.alpha * (0.8f + sin(mistPhase + mist.x * 0.01f) * 0.2f)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            mistColor.copy(alpha = pulsingAlpha),
                            mistColor.copy(alpha = pulsingAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(mist.x, mist.y),
                        radius = mist.size
                    ),
                    topLeft = Offset(mist.x - mist.size, mist.y - mist.size),
                    size = Size(mist.size * 2f, mist.size * 2f)
                )
            }

            drops.sortedByDescending { it.z }.forEach { drop ->
                val proximity = 1f - drop.z
                val proximityEased = proximity * proximity
                val currentSize = drop.size * proximityEased * 8f
                val alpha = (proximityEased * 0.5f).coerceIn(0f, 0.5f)

                if (currentSize > 0.5f) {
                    val driftOffset = drop.drift * proximity * 30f

                    drawOval(
                        color = dropColor.copy(alpha = alpha * 0.3f),
                        topLeft = Offset(
                            drop.x + driftOffset - currentSize * 0.8f,
                            drop.y - currentSize * 0.8f
                        ),
                        size = Size(currentSize * 1.6f, currentSize * 1.6f)
                    )

                    drawOval(
                        color = dropColor.copy(alpha = alpha * 0.7f),
                        topLeft = Offset(
                            drop.x + driftOffset - currentSize / 2,
                            drop.y - currentSize / 2
                        ),
                        size = Size(currentSize, currentSize)
                    )

                    drawOval(
                        color = Color.White.copy(alpha = alpha * 0.5f),
                        topLeft = Offset(
                            drop.x + driftOffset - currentSize / 5,
                            drop.y - currentSize / 4
                        ),
                        size = Size(currentSize / 3f, currentSize / 4f)
                    )
                }
            }

            splashes.forEach { splash ->
                val inv = 1f - splash.progress
                val invEased = inv * inv
                val radius = splash.progress * 25f * splash.size

                drawCircle(
                    color = splashColor.copy(alpha = invEased * 0.2f),
                    radius = radius,
                    center = Offset(splash.x, splash.y),
                    style = Stroke(width = 1f * invEased + 0.3f)
                )

                drawCircle(
                    color = splashColor.copy(alpha = invEased * 0.1f),
                    radius = radius * 0.6f,
                    center = Offset(splash.x, splash.y),
                    style = Stroke(width = 0.8f * invEased)
                )
            }
        }
    }
}