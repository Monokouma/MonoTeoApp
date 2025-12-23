package com.despaircorp.monoteo.ui.background.sand

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SandWave(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float
)

private data class SandParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float,
    val turbulence: Float
)

private data class SandGrain(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speed: Float,
    var spinPhase: Float
)

private data class WindGust(
    var x: Float,
    val y: Float,
    val length: Float,
    val width: Float,
    var alpha: Float,
    val speed: Float,
    var wavePhase: Float
)

private data class SandDrift(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    var alpha: Float,
    val speed: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFC4956A),
        Color(0xFFD4A57A),
        Color(0xFFE4B58A),
        Color(0xFFF0C59A),
        Color(0xFFFAD5AA)
    )
)

private val sandColorDark = Color(0xFFB08050)
private val sandColorMid = Color(0xFFD0A070)
private val sandColorLight = Color(0xFFE8C090)
private val sandColorBright = Color(0xFFF5D8B0)
private val windColor = Color(0xFFFAE8D0)

@Suppress("EffectKeys")
@Composable
fun SandBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var sandWaves by remember { mutableStateOf(emptyList<SandWave>()) }
    var sandParticles by remember { mutableStateOf(emptyList<SandParticle>()) }
    var sandGrains by remember { mutableStateOf(emptyList<SandGrain>()) }
    var windGusts by remember { mutableStateOf(emptyList<WindGust>()) }
    var sandDrifts by remember { mutableStateOf(emptyList<SandDrift>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }
    var stormIntensity by remember { mutableFloatStateOf(0.6f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            sandWaves = List(14) { i ->
                val layer = i % 3
                SandWave(
                    x = Random.nextFloat() * screenWidth * 2.5f - screenWidth * 0.75f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.4f
                        1 -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.25f
                        else -> Random.nextFloat() * screenHeight * 0.4f + screenHeight * 0.5f
                    },
                    width = Random.nextFloat() * screenWidth * 1.4f + screenWidth * 0.7f,
                    height = Random.nextFloat() * screenHeight * 0.3f + screenHeight * 0.12f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.1f + 0.05f
                        1 -> Random.nextFloat() * 0.15f + 0.08f
                        else -> Random.nextFloat() * 0.22f + 0.12f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.6f + 0.3f
                        1 -> Random.nextFloat() * 1f + 0.5f
                        else -> Random.nextFloat() * 1.5f + 0.8f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            sandParticles = List(80) {
                SandParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 70f + 35f,
                    alpha = Random.nextFloat() * 0.18f + 0.06f,
                    speedX = Random.nextFloat() * 2f + 0.8f,
                    speedY = Random.nextFloat() * 0.6f - 0.3f,
                    phase = Random.nextFloat() * 6.28f,
                    turbulence = Random.nextFloat() * 2.5f + 1f
                )
            }

            sandGrains = List(120) {
                SandGrain(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 4f + 1f,
                    alpha = Random.nextFloat() * 0.8f + 0.2f,
                    speed = Random.nextFloat() * 4f + 1.5f,
                    spinPhase = Random.nextFloat() * 6.28f
                )
            }

            windGusts = List(18) {
                WindGust(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = Random.nextFloat() * screenHeight,
                    length = Random.nextFloat() * 250f + 120f,
                    width = Random.nextFloat() * 12f + 4f,
                    alpha = Random.nextFloat() * 0.2f + 0.08f,
                    speed = Random.nextFloat() * 5f + 2.5f,
                    wavePhase = Random.nextFloat() * 6.28f
                )
            }

            sandDrifts = List(10) {
                SandDrift(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = screenHeight * 0.6f + Random.nextFloat() * screenHeight * 0.4f,
                    width = Random.nextFloat() * screenWidth * 0.6f + screenWidth * 0.3f,
                    height = Random.nextFloat() * 60f + 30f,
                    alpha = Random.nextFloat() * 0.25f + 0.1f,
                    speed = Random.nextFloat() * 1.2f + 0.4f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.012f) % 6.28f
                stormIntensity = (sin(globalPhase * 0.25f) * 0.35f + 0.65f).coerceIn(0.3f, 1f)

                sandWaves = sandWaves.map { wave ->
                    wave.phase += 0.01f
                    var newX = wave.x + wave.speed * stormIntensity
                    if (newX > screenWidth + wave.width * 0.5f) {
                        newX = -wave.width
                    }
                    wave.copy(x = newX, phase = wave.phase)
                }

                sandParticles = sandParticles.map { particle ->
                    particle.phase += 0.025f
                    var newX = particle.x + particle.speedX * stormIntensity
                    var newY = particle.y + particle.speedY + sin(particle.phase * particle.turbulence) * 0.6f

                    if (newX > screenWidth + particle.size) {
                        newX = -particle.size
                        newY = Random.nextFloat() * screenHeight
                    }

                    particle.copy(
                        x = newX,
                        y = newY.coerceIn(-particle.size, screenHeight + particle.size),
                        phase = particle.phase
                    )
                }

                sandGrains = sandGrains.map { grain ->
                    grain.spinPhase += 0.1f
                    var newX = grain.x + grain.speed * stormIntensity
                    var newY = grain.y + sin(grain.spinPhase) * 0.8f + cos(grain.spinPhase * 0.7f) * 0.4f

                    if (newX > screenWidth + 15f) {
                        newX = -15f
                        newY = Random.nextFloat() * screenHeight
                    }
                    if (newY < -15f) newY = screenHeight + 15f
                    if (newY > screenHeight + 15f) newY = -15f

                    grain.copy(x = newX, y = newY, spinPhase = grain.spinPhase)
                }

                windGusts = windGusts.map { gust ->
                    gust.wavePhase += 0.02f
                    var newX = gust.x + gust.speed * stormIntensity

                    if (newX > screenWidth + gust.length) {
                        newX = -gust.length
                    }

                    gust.copy(x = newX, wavePhase = gust.wavePhase)
                }

                sandDrifts = sandDrifts.map { drift ->
                    var newX = drift.x + drift.speed * stormIntensity

                    if (newX > screenWidth + drift.width * 0.5f) {
                        newX = -drift.width
                    }

                    drift.copy(x = newX)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val hazeAlpha = (sin(globalPhase * 0.4f) * 0.08f + 0.15f).coerceIn(0f, 0.23f) * stormIntensity

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        sandColorLight.copy(alpha = hazeAlpha * 0.6f),
                        sandColorMid.copy(alpha = hazeAlpha * 0.3f),
                        Color.Transparent,
                        sandColorMid.copy(alpha = hazeAlpha * 0.4f),
                        sandColorDark.copy(alpha = hazeAlpha * 0.8f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sandColorBright.copy(alpha = hazeAlpha * 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(screenWidth * 0.75f, screenHeight * 0.25f),
                    radius = (screenHeight * 0.65f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            sandWaves.filter { it.layer == 0 }.forEach { wave ->
                val pulseAlpha = wave.baseAlpha * (0.7f + sin(wave.phase) * 0.3f) * stormIntensity
                drawSandWave(wave, pulseAlpha, sandColorDark)
            }

            sandParticles.filter { it.alpha < 0.12f }.forEach { particle ->
                val pulseAlpha = particle.alpha * (0.6f + sin(particle.phase) * 0.4f) * stormIntensity
                val radius = particle.size.coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColorDark.copy(alpha = pulseAlpha),
                            sandColorDark.copy(alpha = pulseAlpha * 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(particle.x - radius, particle.y - radius * 0.65f),
                    size = Size(radius * 2f, radius * 1.3f)
                )
            }

            windGusts.filter { it.alpha < 0.14f }.forEach { gust ->
                drawWindGust(gust, stormIntensity)
            }

            sandWaves.filter { it.layer == 1 }.forEach { wave ->
                val pulseAlpha = wave.baseAlpha * (0.75f + sin(wave.phase + 1f) * 0.25f) * stormIntensity
                drawSandWave(wave, pulseAlpha, sandColorMid)
            }

            sandParticles.filter { it.alpha >= 0.12f }.forEach { particle ->
                val pulseAlpha = particle.alpha * (0.65f + sin(particle.phase) * 0.35f) * stormIntensity
                val radius = particle.size.coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColorMid.copy(alpha = pulseAlpha),
                            sandColorMid.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(particle.x - radius, particle.y - radius * 0.6f),
                    size = Size(radius * 2f, radius * 1.2f)
                )
            }

            windGusts.filter { it.alpha >= 0.14f }.forEach { gust ->
                drawWindGust(gust, stormIntensity)
            }

            sandDrifts.forEach { drift ->
                val driftAlpha = drift.alpha * stormIntensity

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColorLight.copy(alpha = driftAlpha),
                            sandColorMid.copy(alpha = driftAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(drift.x + drift.width / 2, drift.y),
                        radius = (drift.width / 2).coerceAtLeast(1f)
                    ),
                    topLeft = Offset(drift.x, drift.y - drift.height / 2),
                    size = Size(drift.width, drift.height)
                )
            }

            sandWaves.filter { it.layer == 2 }.forEach { wave ->
                val pulseAlpha = wave.baseAlpha * (0.8f + sin(wave.phase + 2f) * 0.2f) * stormIntensity
                drawSandWave(wave, pulseAlpha, sandColorLight)
            }

            sandGrains.forEach { grain ->
                val spinAlpha = grain.alpha * (0.4f + sin(grain.spinPhase) * 0.6f) * stormIntensity
                val spinSize = grain.size * (0.7f + sin(grain.spinPhase * 1.5f) * 0.3f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColorBright.copy(alpha = spinAlpha * 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(grain.x, grain.y),
                        radius = (spinSize * 3.5f).coerceAtLeast(1f)
                    ),
                    radius = (spinSize * 3.5f).coerceAtLeast(1f),
                    center = Offset(grain.x, grain.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColorBright.copy(alpha = spinAlpha),
                            sandColorLight.copy(alpha = spinAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(grain.x - spinSize * 0.12f, grain.y - spinSize * 0.12f),
                        radius = spinSize.coerceAtLeast(0.5f)
                    ),
                    radius = spinSize.coerceAtLeast(0.5f),
                    center = Offset(grain.x, grain.y)
                )
            }

            val edgeSand = (sin(globalPhase * 0.35f) * 0.1f + 0.18f).coerceIn(0f, 0.28f) * stormIntensity

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        sandColorLight.copy(alpha = edgeSand * 0.5f),
                        sandColorLight.copy(alpha = edgeSand * 0.15f),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = screenWidth * 0.3f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth * 0.3f, screenHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        sandColorMid.copy(alpha = edgeSand * 0.4f),
                        sandColorMid.copy(alpha = edgeSand)
                    ),
                    startX = screenWidth * 0.7f,
                    endX = screenWidth
                ),
                topLeft = Offset(screenWidth * 0.7f, 0f),
                size = Size(screenWidth * 0.3f, screenHeight)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        sandColorLight.copy(alpha = edgeSand * 0.45f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = screenHeight * 0.28f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight * 0.28f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        sandColorDark.copy(alpha = edgeSand * 0.85f)
                    ),
                    startY = screenHeight * 0.72f,
                    endY = screenHeight
                ),
                topLeft = Offset(0f, screenHeight * 0.72f),
                size = Size(screenWidth, screenHeight * 0.28f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSandWave(
    wave: SandWave,
    alpha: Float,
    color: Color
) {
    val centerX = wave.x + wave.width / 2
    val centerY = wave.y + wave.height / 2
    val warp = sin(wave.phase) * 0.12f
    val radius = (wave.width / 2).coerceAtLeast(1f)

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
        topLeft = Offset(wave.x, wave.y),
        size = Size(wave.width * (1f + warp), wave.height)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(centerX - wave.width * 0.18f, centerY - wave.height * 0.1f),
            radius = (wave.width * 0.32f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            wave.x + wave.width * 0.06f,
            wave.y + wave.height * 0.12f
        ),
        size = Size(wave.width * 0.48f, wave.height * 0.6f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.38f),
                Color.Transparent
            ),
            center = Offset(centerX + wave.width * 0.15f, centerY + wave.height * 0.06f),
            radius = (wave.width * 0.26f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            wave.x + wave.width * 0.48f,
            wave.y + wave.height * 0.28f
        ),
        size = Size(wave.width * 0.38f, wave.height * 0.52f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWindGust(
    gust: WindGust,
    intensity: Float
) {
    val waveY = sin(gust.wavePhase) * 12f
    val gustAlpha = gust.alpha * intensity * (0.6f + sin(gust.wavePhase * 0.5f) * 0.4f)

    val path = Path().apply {
        moveTo(gust.x, gust.y + waveY)

        val segments = 6
        val segmentLength = gust.length / segments
        for (i in 1..segments) {
            val segX = gust.x + segmentLength * i
            val segWave = sin(gust.wavePhase + i * 0.6f) * 10f
            quadraticTo(
                gust.x + segmentLength * (i - 0.5f),
                gust.y + waveY + sin(gust.wavePhase + (i - 0.5f) * 0.6f) * 14f,
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
                windColor.copy(alpha = gustAlpha * 0.4f),
                windColor.copy(alpha = gustAlpha),
                windColor.copy(alpha = gustAlpha * 0.6f),
                Color.Transparent
            ),
            startX = gust.x,
            endX = gust.x + gust.length
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = gust.width,
            cap = StrokeCap.Round
        )
    )
}