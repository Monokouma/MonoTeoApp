package com.despaircorp.monoteo.ui.background.clear

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

private data class SunRay(
    val angle: Float,
    val length: Float,
    val width: Float,
    var alpha: Float,
    var pulsePhase: Float
)

private data class LensFlare(
    val x: Float,
    val y: Float,
    val size: Float,
    var alpha: Float,
    val color: Color
)

private data class FloatingParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var shimmerPhase: Float
)

private data class HeatWave(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    var phase: Float,
    val speed: Float,
    val alpha: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1E90FF),
        Color(0xFF3AA0FF),
        Color(0xFF5CB0FF),
        Color(0xFF7EC8FF),
        Color(0xFFA0D8FF),
        Color(0xFFC0E8FF)
    )
)

private val sunColorCore = Color(0xFFFFFFE0)
private val sunColorMid = Color(0xFFFFEE80)
private val sunColorOuter = Color(0xFFFFDD40)
private val sunColorGlow = Color(0xFFFFCC00)
private val rayColor = Color(0xFFFFEEAA)
private val flareColorBlue = Color(0xFF80C0FF)
private val flareColorGreen = Color(0xFFAAFFCC)
private val flareColorPink = Color(0xFFFFAACC)
private val particleColor = Color(0xFFFFFFFF)

@Suppress("EffectKeys")
@Composable
fun ClearBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var sunRays by remember { mutableStateOf(emptyList<SunRay>()) }
    var lensFlares by remember { mutableStateOf(emptyList<LensFlare>()) }
    var floatingParticles by remember { mutableStateOf(emptyList<FloatingParticle>()) }
    var heatWaves by remember { mutableStateOf(emptyList<HeatWave>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }
    var sunPulse by remember { mutableFloatStateOf(0f) }

    val sunX by remember(screenWidth) { mutableFloatStateOf(screenWidth * 0.75f) }
    val sunY by remember(screenHeight) { mutableFloatStateOf(screenHeight * 0.18f) }
    val sunRadius = 60f

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            sunRays = List(16) { i ->
                SunRay(
                    angle = i * (6.28f / 16f) + Random.nextFloat() * 0.1f,
                    length = Random.nextFloat() * 200f + 150f,
                    width = Random.nextFloat() * 30f + 15f,
                    alpha = Random.nextFloat() * 0.15f + 0.08f,
                    pulsePhase = Random.nextFloat() * 6.28f
                )
            }

            val flareColors = listOf(flareColorBlue, flareColorGreen, flareColorPink, sunColorGlow)
            lensFlares = List(8) { i ->
                val t = (i + 1) / 9f
                LensFlare(
                    x = sunX - (sunX - screenWidth * 0.3f) * t,
                    y = sunY + (screenHeight * 0.5f - sunY) * t,
                    size = Random.nextFloat() * 40f + 15f,
                    alpha = Random.nextFloat() * 0.25f + 0.1f,
                    color = flareColors[i % flareColors.size]
                )
            }

            floatingParticles = List(40) {
                FloatingParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 3f + 1f,
                    alpha = Random.nextFloat() * 0.5f + 0.2f,
                    speedX = Random.nextFloat() * 0.3f - 0.15f,
                    speedY = Random.nextFloat() * 0.2f - 0.1f,
                    shimmerPhase = Random.nextFloat() * 6.28f
                )
            }

            heatWaves = List(6) {
                HeatWave(
                    x = Random.nextFloat() * screenWidth * 1.5f - screenWidth * 0.25f,
                    y = screenHeight * 0.7f + Random.nextFloat() * screenHeight * 0.25f,
                    width = Random.nextFloat() * screenWidth * 0.5f + screenWidth * 0.3f,
                    height = Random.nextFloat() * 30f + 15f,
                    phase = Random.nextFloat() * 6.28f,
                    speed = Random.nextFloat() * 0.3f + 0.1f,
                    alpha = Random.nextFloat() * 0.06f + 0.02f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.008f) % 6.28f
                sunPulse = (sunPulse + 0.015f) % 6.28f

                sunRays = sunRays.map { ray ->
                    ray.pulsePhase += 0.02f
                    ray.copy(pulsePhase = ray.pulsePhase)
                }

                floatingParticles = floatingParticles.map { particle ->
                    particle.shimmerPhase += 0.04f
                    var newX = particle.x + particle.speedX
                    var newY = particle.y + particle.speedY

                    if (newX < -10f) newX = screenWidth + 10f
                    if (newX > screenWidth + 10f) newX = -10f
                    if (newY < -10f) newY = screenHeight + 10f
                    if (newY > screenHeight + 10f) newY = -10f

                    particle.copy(x = newX, y = newY, shimmerPhase = particle.shimmerPhase)
                }

                heatWaves = heatWaves.map { wave ->
                    wave.phase += 0.03f
                    var newX = wave.x + wave.speed

                    if (newX > screenWidth + wave.width * 0.5f) {
                        newX = -wave.width
                    }

                    wave.copy(x = newX, phase = wave.phase)
                }

                lensFlares = lensFlares.map { flare ->
                    val pulseAlpha = flare.alpha * (0.6f + sin(globalPhase + flare.x * 0.01f) * 0.4f)
                    flare.copy(alpha = pulseAlpha.coerceIn(0.05f, 0.35f))
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val sunCenterX = screenWidth * 0.75f
            val sunCenterY = screenHeight * 0.18f

            val atmosphereAlpha = (sin(globalPhase * 0.5f) * 0.03f + 0.06f).coerceIn(0f, 0.09f)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = atmosphereAlpha * 0.5f),
                        Color.Transparent,
                        Color.White.copy(alpha = atmosphereAlpha * 0.3f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            val outerGlowPulse = 1f + sin(sunPulse) * 0.08f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sunColorGlow.copy(alpha = 0.03f),
                        sunColorGlow.copy(alpha = 0.015f),
                        Color.Transparent
                    ),
                    center = Offset(sunCenterX, sunCenterY),
                    radius = (sunRadius * 8f * outerGlowPulse).coerceAtLeast(1f)
                ),
                radius = (sunRadius * 8f * outerGlowPulse).coerceAtLeast(1f),
                center = Offset(sunCenterX, sunCenterY)
            )

            sunRays.forEach { ray ->
                val pulseAlpha = ray.alpha * (0.6f + sin(ray.pulsePhase) * 0.4f)
                val pulseLength = ray.length * (0.9f + sin(ray.pulsePhase * 0.5f) * 0.1f)

                val startX = sunCenterX + cos(ray.angle) * sunRadius * 1.2f
                val startY = sunCenterY + sin(ray.angle) * sunRadius * 1.2f
                val endX = sunCenterX + cos(ray.angle) * (sunRadius + pulseLength)
                val endY = sunCenterY + sin(ray.angle) * (sunRadius + pulseLength)

                val rayPath = Path().apply {
                    val perpX = -sin(ray.angle) * ray.width * 0.5f
                    val perpY = cos(ray.angle) * ray.width * 0.5f

                    moveTo(startX + perpX, startY + perpY)
                    lineTo(startX - perpX, startY - perpY)
                    lineTo(endX, endY)
                    close()
                }

                drawPath(
                    path = rayPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            rayColor.copy(alpha = pulseAlpha),
                            rayColor.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY)
                    )
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sunColorGlow.copy(alpha = 0.12f),
                        sunColorGlow.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(sunCenterX, sunCenterY),
                    radius = (sunRadius * 4f).coerceAtLeast(1f)
                ),
                radius = (sunRadius * 4f).coerceAtLeast(1f),
                center = Offset(sunCenterX, sunCenterY)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sunColorOuter.copy(alpha = 0.25f),
                        sunColorOuter.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(sunCenterX, sunCenterY),
                    radius = (sunRadius * 2.5f).coerceAtLeast(1f)
                ),
                radius = (sunRadius * 2.5f).coerceAtLeast(1f),
                center = Offset(sunCenterX, sunCenterY)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sunColorMid.copy(alpha = 0.5f),
                        sunColorOuter.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(sunCenterX, sunCenterY),
                    radius = (sunRadius * 1.6f).coerceAtLeast(1f)
                ),
                radius = (sunRadius * 1.6f).coerceAtLeast(1f),
                center = Offset(sunCenterX, sunCenterY)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sunColorCore,
                        sunColorMid,
                        sunColorOuter
                    ),
                    center = Offset(sunCenterX - sunRadius * 0.15f, sunCenterY - sunRadius * 0.15f),
                    radius = sunRadius.coerceAtLeast(1f)
                ),
                radius = sunRadius.coerceAtLeast(1f),
                center = Offset(sunCenterX, sunCenterY)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = (sunRadius * 0.5f).coerceAtLeast(1f),
                center = Offset(sunCenterX - sunRadius * 0.25f, sunCenterY - sunRadius * 0.25f)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = (sunRadius * 0.2f).coerceAtLeast(1f),
                center = Offset(sunCenterX + sunRadius * 0.3f, sunCenterY + sunRadius * 0.2f)
            )

            lensFlares.forEach { flare ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            flare.color.copy(alpha = flare.alpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(flare.x, flare.y),
                        radius = (flare.size * 3f).coerceAtLeast(1f)
                    ),
                    radius = (flare.size * 3f).coerceAtLeast(1f),
                    center = Offset(flare.x, flare.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            flare.color.copy(alpha = flare.alpha),
                            flare.color.copy(alpha = flare.alpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(flare.x, flare.y),
                        radius = flare.size.coerceAtLeast(1f)
                    ),
                    radius = flare.size.coerceAtLeast(1f),
                    center = Offset(flare.x, flare.y)
                )
            }

            heatWaves.forEach { wave ->
                val waveOffset = sin(wave.phase) * 8f

                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = wave.alpha),
                            Color.Transparent
                        ),
                        startY = wave.y - wave.height / 2 + waveOffset,
                        endY = wave.y + wave.height / 2 + waveOffset
                    ),
                    topLeft = Offset(wave.x, wave.y - wave.height / 2 + waveOffset),
                    size = Size(wave.width, wave.height)
                )
            }

            floatingParticles.forEach { particle ->
                val shimmerAlpha = particle.alpha * (0.3f + sin(particle.shimmerPhase) * 0.7f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            particleColor.copy(alpha = shimmerAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = (particle.size * 4f).coerceAtLeast(1f)
                    ),
                    radius = (particle.size * 4f).coerceAtLeast(1f),
                    center = Offset(particle.x, particle.y)
                )

                drawCircle(
                    color = particleColor.copy(alpha = shimmerAlpha),
                    radius = particle.size.coerceAtLeast(0.5f),
                    center = Offset(particle.x, particle.y)
                )
            }

            val horizonGlow = (sin(globalPhase * 0.3f) * 0.04f + 0.08f).coerceIn(0f, 0.12f)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        sunColorGlow.copy(alpha = horizonGlow * 0.3f),
                        sunColorGlow.copy(alpha = horizonGlow)
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