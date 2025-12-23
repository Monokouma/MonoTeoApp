package com.despaircorp.monoteo.ui.background.squall

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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class RainStreak(
    var x: Float,
    var y: Float,
    val length: Float,
    val width: Float,
    var alpha: Float,
    val speed: Float,
    val angle: Float
)

private data class WindGust(
    var x: Float,
    val y: Float,
    val length: Float,
    val thickness: Float,
    var alpha: Float,
    val speed: Float,
    var wavePhase: Float
)

private data class StormCloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float
)

private data class Debris(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var spinPhase: Float,
    val type: Int
)

private data class Splash(
    val x: Float,
    val y: Float,
    var progress: Float,
    val intensity: Float
)

private data class WaterSheet(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    var alpha: Float,
    val speed: Float,
    var wavePhase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0A0C10),
        Color(0xFF141820),
        Color(0xFF1E2430),
        Color(0xFF283040),
        Color(0xFF323C50)
    )
)

private val rainColor = Color(0xFFB0C8E0)
private val windColor = Color(0xFF8AA8C8)
private val cloudColorDark = Color(0xFF202830)
private val cloudColorMid = Color(0xFF384050)
private val cloudColorLight = Color(0xFF506070)
private val debrisColor = Color(0xFF5A6070)
private val splashColor = Color(0xFFA0C0D8)

@Suppress("EffectKeys")
@Composable
fun SquallBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var rainStreaks by remember { mutableStateOf(emptyList<RainStreak>()) }
    var windGusts by remember { mutableStateOf(emptyList<WindGust>()) }
    var stormClouds by remember { mutableStateOf(emptyList<StormCloud>()) }
    var debris by remember { mutableStateOf(emptyList<Debris>()) }
    var splashes by remember { mutableStateOf(emptyList<Splash>()) }
    var waterSheets by remember { mutableStateOf(emptyList<WaterSheet>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }
    var stormIntensity by remember { mutableFloatStateOf(0.7f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            rainStreaks = List(150) {
                RainStreak(
                    x = Random.nextFloat() * screenWidth * 1.5f - screenWidth * 0.25f,
                    y = Random.nextFloat() * screenHeight * 1.5f - screenHeight * 0.25f,
                    length = Random.nextFloat() * 80f + 40f,
                    width = Random.nextFloat() * 3f + 1f,
                    alpha = Random.nextFloat() * 0.5f + 0.2f,
                    speed = Random.nextFloat() * 25f + 15f,
                    angle = Random.nextFloat() * 0.6f + 0.5f
                )
            }

            windGusts = List(20) {
                WindGust(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = Random.nextFloat() * screenHeight,
                    length = Random.nextFloat() * 350f + 150f,
                    thickness = Random.nextFloat() * 20f + 8f,
                    alpha = Random.nextFloat() * 0.2f + 0.08f,
                    speed = Random.nextFloat() * 12f + 6f,
                    wavePhase = Random.nextFloat() * 6.28f
                )
            }

            stormClouds = List(12) { i ->
                val layer = i % 3
                StormCloud(
                    x = Random.nextFloat() * screenWidth * 2.5f - screenWidth * 0.75f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.35f
                        1 -> Random.nextFloat() * screenHeight * 0.4f + screenHeight * 0.15f
                        else -> Random.nextFloat() * screenHeight * 0.4f + screenHeight * 0.35f
                    },
                    width = Random.nextFloat() * screenWidth * 1.2f + screenWidth * 0.5f,
                    height = Random.nextFloat() * screenHeight * 0.25f + screenHeight * 0.1f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.12f + 0.06f
                        1 -> Random.nextFloat() * 0.18f + 0.1f
                        else -> Random.nextFloat() * 0.25f + 0.12f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 1.5f + 0.8f
                        1 -> Random.nextFloat() * 2.5f + 1.2f
                        else -> Random.nextFloat() * 4f + 2f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            debris = List(30) {
                Debris(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 8f + 3f,
                    alpha = Random.nextFloat() * 0.7f + 0.3f,
                    speedX = Random.nextFloat() * 10f + 5f,
                    speedY = Random.nextFloat() * 4f - 2f,
                    spinPhase = Random.nextFloat() * 6.28f,
                    type = Random.nextInt(3)
                )
            }

            waterSheets = List(8) {
                WaterSheet(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = Random.nextFloat() * screenHeight,
                    width = Random.nextFloat() * 200f + 100f,
                    height = Random.nextFloat() * 40f + 20f,
                    alpha = Random.nextFloat() * 0.15f + 0.05f,
                    speed = Random.nextFloat() * 8f + 4f,
                    wavePhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val newSplashesList = mutableListOf<Splash>()

        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                newSplashesList.clear()

                globalPhase = (globalPhase + 0.015f) % 6.28f
                stormIntensity = (sin(globalPhase * 0.2f) * 0.25f + 0.75f).coerceIn(0.5f, 1f)

                rainStreaks = rainStreaks.map { streak ->
                    var newX = streak.x + streak.speed * streak.angle * stormIntensity
                    var newY = streak.y + streak.speed * stormIntensity

                    if (newY > screenHeight + streak.length || newX > screenWidth + streak.length) {
                        if (Random.nextFloat() > 0.7f) {
                            newSplashesList.add(
                                Splash(
                                    x = streak.x.coerceIn(0f, screenWidth),
                                    y = screenHeight * (0.7f + Random.nextFloat() * 0.3f),
                                    progress = 0f,
                                    intensity = streak.width / 3f
                                )
                            )
                        }
                        newX = -streak.length + Random.nextFloat() * screenWidth * 0.3f
                        newY = -streak.length - Random.nextFloat() * screenHeight * 0.3f
                    }

                    streak.copy(x = newX, y = newY)
                }

                splashes = (splashes + newSplashesList).mapNotNull { splash ->
                    val next = splash.progress + 0.08f
                    if (next >= 1f) null else splash.copy(progress = next)
                }

                windGusts = windGusts.map { gust ->
                    gust.wavePhase += 0.03f
                    var newX = gust.x + gust.speed * stormIntensity

                    if (newX > screenWidth + gust.length) {
                        newX = -gust.length
                    }

                    gust.copy(x = newX, wavePhase = gust.wavePhase)
                }

                stormClouds = stormClouds.map { cloud ->
                    cloud.phase += 0.008f
                    var newX = cloud.x + cloud.speed * stormIntensity

                    if (newX > screenWidth + cloud.width * 0.5f) {
                        newX = -cloud.width
                    }

                    cloud.copy(x = newX, phase = cloud.phase)
                }

                debris = debris.map { d ->
                    d.spinPhase += 0.15f
                    var newX = d.x + d.speedX * stormIntensity
                    var newY = d.y + d.speedY + sin(d.spinPhase) * 1.5f

                    if (newX > screenWidth + 20f) {
                        newX = -20f
                        newY = Random.nextFloat() * screenHeight
                    }
                    if (newY < -20f) newY = screenHeight + 20f
                    if (newY > screenHeight + 20f) newY = -20f

                    d.copy(x = newX, y = newY, spinPhase = d.spinPhase)
                }

                waterSheets = waterSheets.map { sheet ->
                    sheet.wavePhase += 0.025f
                    var newX = sheet.x + sheet.speed * stormIntensity

                    if (newX > screenWidth + sheet.width) {
                        newX = -sheet.width
                    }

                    sheet.copy(x = newX, wavePhase = sheet.wavePhase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val hazeAlpha = (sin(globalPhase * 0.3f) * 0.08f + 0.15f).coerceIn(0f, 0.23f) * stormIntensity

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        cloudColorLight.copy(alpha = hazeAlpha * 0.4f),
                        Color.Transparent,
                        cloudColorDark.copy(alpha = hazeAlpha * 0.6f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            stormClouds.filter { it.layer == 0 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.7f + sin(cloud.phase) * 0.3f) * stormIntensity
                drawStormCloud(cloud, pulseAlpha, cloudColorDark)
            }

            windGusts.filter { it.alpha < 0.14f }.forEach { gust ->
                drawWindGust(gust, stormIntensity, windColor)
            }

            stormClouds.filter { it.layer == 1 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.75f + sin(cloud.phase + 1f) * 0.25f) * stormIntensity
                drawStormCloud(cloud, pulseAlpha, cloudColorMid)
            }

            waterSheets.forEach { sheet ->
                val waveOffset = sin(sheet.wavePhase) * 8f
                val sheetAlpha = sheet.alpha * stormIntensity * (0.7f + sin(sheet.wavePhase * 0.5f) * 0.3f)

                drawOval(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            windColor.copy(alpha = sheetAlpha),
                            windColor.copy(alpha = sheetAlpha * 0.6f),
                            Color.Transparent
                        ),
                        startX = sheet.x,
                        endX = sheet.x + sheet.width
                    ),
                    topLeft = Offset(sheet.x, sheet.y + waveOffset - sheet.height / 2),
                    size = Size(sheet.width, sheet.height)
                )
            }

            windGusts.filter { it.alpha >= 0.14f }.forEach { gust ->
                drawWindGust(gust, stormIntensity, windColor)
            }

            stormClouds.filter { it.layer == 2 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.8f + sin(cloud.phase + 2f) * 0.2f) * stormIntensity
                drawStormCloud(cloud, pulseAlpha, cloudColorLight)
            }

            rainStreaks.forEach { streak ->
                val endX = streak.x + streak.length * streak.angle
                val endY = streak.y + streak.length

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            rainColor.copy(alpha = streak.alpha * 0.4f * stormIntensity),
                            rainColor.copy(alpha = streak.alpha * stormIntensity),
                            rainColor.copy(alpha = streak.alpha * 0.7f * stormIntensity)
                        ),
                        start = Offset(streak.x, streak.y),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(streak.x, streak.y),
                    end = Offset(endX, endY),
                    strokeWidth = streak.width,
                    cap = StrokeCap.Round
                )
            }

            splashes.forEach { splash ->
                val inv = 1f - splash.progress
                val invEased = inv * inv
                val radius = splash.progress * 40f * splash.intensity

                drawCircle(
                    color = splashColor.copy(alpha = invEased * 0.35f * stormIntensity),
                    radius = radius.coerceAtLeast(1f),
                    center = Offset(splash.x, splash.y),
                    style = Stroke(width = 2f * invEased + 0.5f)
                )

                drawCircle(
                    color = splashColor.copy(alpha = invEased * 0.15f * stormIntensity),
                    radius = (radius * 0.6f).coerceAtLeast(1f),
                    center = Offset(splash.x, splash.y),
                    style = Stroke(width = 1.5f * invEased)
                )
            }

            debris.forEach { d ->
                val spinAlpha = d.alpha * (0.5f + sin(d.spinPhase) * 0.5f) * stormIntensity
                val spinScale = 0.6f + cos(d.spinPhase * 1.5f) * 0.4f

                when (d.type) {
                    0 -> {
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    debrisColor.copy(alpha = spinAlpha),
                                    debrisColor.copy(alpha = spinAlpha * 0.4f),
                                    Color.Transparent
                                ),
                                center = Offset(d.x, d.y),
                                radius = (d.size * spinScale).coerceAtLeast(1f)
                            ),
                            topLeft = Offset(d.x - d.size * spinScale, d.y - d.size * spinScale * 0.5f),
                            size = Size((d.size * spinScale * 2f).coerceAtLeast(1f), (d.size * spinScale).coerceAtLeast(1f))
                        )
                    }
                    1 -> {
                        val angle = d.spinPhase
                        val len = d.size * 1.5f
                        drawLine(
                            color = debrisColor.copy(alpha = spinAlpha),
                            start = Offset(d.x - cos(angle) * len, d.y - sin(angle) * len),
                            end = Offset(d.x + cos(angle) * len, d.y + sin(angle) * len),
                            strokeWidth = (d.size * 0.3f).coerceAtLeast(1f),
                            cap = StrokeCap.Round
                        )
                    }
                    else -> {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    debrisColor.copy(alpha = spinAlpha),
                                    debrisColor.copy(alpha = spinAlpha * 0.3f),
                                    Color.Transparent
                                ),
                                center = Offset(d.x, d.y),
                                radius = (d.size * spinScale).coerceAtLeast(1f)
                            ),
                            radius = (d.size * spinScale).coerceAtLeast(1f),
                            center = Offset(d.x, d.y)
                        )
                    }
                }
            }

            val edgeStorm = (sin(globalPhase * 0.4f) * 0.1f + 0.18f).coerceIn(0f, 0.28f) * stormIntensity

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        cloudColorMid.copy(alpha = edgeStorm * 0.7f),
                        cloudColorMid.copy(alpha = edgeStorm * 0.2f),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = screenWidth * 0.25f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth * 0.25f, screenHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        cloudColorMid.copy(alpha = edgeStorm * 0.3f),
                        cloudColorMid.copy(alpha = edgeStorm * 0.9f)
                    ),
                    startX = screenWidth * 0.75f,
                    endX = screenWidth
                ),
                topLeft = Offset(screenWidth * 0.75f, 0f),
                size = Size(screenWidth * 0.25f, screenHeight)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        cloudColorDark.copy(alpha = edgeStorm * 0.6f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = screenHeight * 0.25f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight * 0.25f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        cloudColorDark.copy(alpha = edgeStorm * 0.5f),
                        cloudColorDark.copy(alpha = edgeStorm)
                    ),
                    startY = screenHeight * 0.75f,
                    endY = screenHeight
                ),
                topLeft = Offset(0f, screenHeight * 0.75f),
                size = Size(screenWidth, screenHeight * 0.25f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStormCloud(
    cloud: StormCloud,
    alpha: Float,
    color: Color
) {
    val centerX = cloud.x + cloud.width / 2
    val centerY = cloud.y + cloud.height / 2
    val warp = sin(cloud.phase) * 0.12f
    val radius = (cloud.width / 2).coerceAtLeast(1f)

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = alpha * 0.55f),
                color.copy(alpha = alpha * 0.18f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius
        ),
        topLeft = Offset(cloud.x, cloud.y),
        size = Size(cloud.width * (1f + warp), cloud.height)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(centerX - cloud.width * 0.2f, centerY - cloud.height * 0.1f),
            radius = (cloud.width * 0.3f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.05f,
            cloud.y + cloud.height * 0.1f
        ),
        size = Size(cloud.width * 0.45f, cloud.height * 0.6f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWindGust(
    gust: WindGust,
    intensity: Float,
    color: Color
) {
    val waveY = sin(gust.wavePhase) * 15f
    val gustAlpha = gust.alpha * intensity * (0.6f + sin(gust.wavePhase * 0.5f) * 0.4f)

    val path = Path().apply {
        moveTo(gust.x, gust.y + waveY)

        val segments = 8
        val segmentLength = gust.length / segments
        for (i in 1..segments) {
            val segX = gust.x + segmentLength * i
            val segWave = sin(gust.wavePhase + i * 0.5f) * 12f
            quadraticTo(
                gust.x + segmentLength * (i - 0.5f),
                gust.y + waveY + sin(gust.wavePhase + (i - 0.5f) * 0.5f) * 18f,
                segX,
                gust.y + segWave
            )
        }
    }

    drawPath(
        path = path,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                color.copy(alpha = gustAlpha * 0.5f),
                color.copy(alpha = gustAlpha),
                color.copy(alpha = gustAlpha * 0.6f),
                Color.Transparent
            ),
            startX = gust.x,
            endX = gust.x + gust.length
        ),
        style = Stroke(
            width = gust.thickness,
            cap = StrokeCap.Round
        )
    )
}