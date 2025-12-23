package com.despaircorp.monoteo.ui.background.dust

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

private data class DustCloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float
)

private data class DustParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float,
    val swirl: Float
)

private data class SandGrain(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speed: Float,
    val angle: Float,
    var tumblePhase: Float
)

private data class WindStreak(
    var x: Float,
    val y: Float,
    val length: Float,
    val width: Float,
    var alpha: Float,
    val speed: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF8B7355),
        Color(0xFFA08060),
        Color(0xFFB89070),
        Color(0xFFCCA080),
        Color(0xFFDDB090)
    )
)

private val dustColorDark = Color(0xFF9A8060)
private val dustColorMid = Color(0xFFBFA080)
private val dustColorLight = Color(0xFFDCC0A0)
private val sandColor = Color(0xFFE8D0B0)
private val windColor = Color(0xFFF0E0C8)

@Suppress("EffectKeys")
@Composable
fun DustBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var dustClouds by remember { mutableStateOf(emptyList<DustCloud>()) }
    var dustParticles by remember { mutableStateOf(emptyList<DustParticle>()) }
    var sandGrains by remember { mutableStateOf(emptyList<SandGrain>()) }
    var windStreaks by remember { mutableStateOf(emptyList<WindStreak>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }
    var windIntensity by remember { mutableFloatStateOf(0.5f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            dustClouds = List(12) { i ->
                val layer = i % 3
                DustCloud(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.5f
                        1 -> Random.nextFloat() * screenHeight * 0.6f + screenHeight * 0.2f
                        else -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.4f
                    },
                    width = Random.nextFloat() * screenWidth * 1.2f + screenWidth * 0.6f,
                    height = Random.nextFloat() * screenHeight * 0.35f + screenHeight * 0.15f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.12f + 0.06f
                        1 -> Random.nextFloat() * 0.18f + 0.1f
                        else -> Random.nextFloat() * 0.25f + 0.12f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.5f + 0.2f
                        1 -> Random.nextFloat() * 0.8f + 0.4f
                        else -> Random.nextFloat() * 1.2f + 0.6f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            dustParticles = List(70) {
                DustParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 80f + 40f,
                    alpha = Random.nextFloat() * 0.15f + 0.05f,
                    speedX = Random.nextFloat() * 1.5f + 0.5f,
                    speedY = Random.nextFloat() * 0.4f - 0.2f,
                    phase = Random.nextFloat() * 6.28f,
                    swirl = Random.nextFloat() * 2f + 1f
                )
            }

            sandGrains = List(100) {
                SandGrain(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 3f + 1f,
                    alpha = Random.nextFloat() * 0.7f + 0.3f,
                    speed = Random.nextFloat() * 3f + 1f,
                    angle = Random.nextFloat() * 0.4f - 0.2f,
                    tumblePhase = Random.nextFloat() * 6.28f
                )
            }

            windStreaks = List(15) {
                WindStreak(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = Random.nextFloat() * screenHeight,
                    length = Random.nextFloat() * 200f + 100f,
                    width = Random.nextFloat() * 8f + 2f,
                    alpha = Random.nextFloat() * 0.2f + 0.05f,
                    speed = Random.nextFloat() * 4f + 2f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.01f) % 6.28f
                windIntensity = (sin(globalPhase * 0.3f) * 0.3f + 0.7f).coerceIn(0.4f, 1f)

                dustClouds = dustClouds.map { cloud ->
                    cloud.phase += 0.008f
                    var newX = cloud.x + cloud.speed * windIntensity
                    if (newX > screenWidth + cloud.width * 0.5f) {
                        newX = -cloud.width
                    }
                    cloud.copy(x = newX, phase = cloud.phase)
                }

                dustParticles = dustParticles.map { particle ->
                    particle.phase += 0.02f
                    var newX = particle.x + particle.speedX * windIntensity
                    var newY = particle.y + particle.speedY + sin(particle.phase * particle.swirl) * 0.5f

                    if (newX > screenWidth + particle.size) {
                        newX = -particle.size
                        newY = Random.nextFloat() * screenHeight
                    }

                    particle.copy(
                        x = newX,
                        y = newY.coerceIn(0f, screenHeight),
                        phase = particle.phase
                    )
                }

                sandGrains = sandGrains.map { grain ->
                    grain.tumblePhase += 0.08f
                    var newX = grain.x + grain.speed * windIntensity
                    var newY = grain.y + sin(grain.tumblePhase) * 0.5f + grain.angle * grain.speed

                    if (newX > screenWidth + 10f) {
                        newX = -10f
                        newY = Random.nextFloat() * screenHeight
                    }
                    if (newY < -10f) newY = screenHeight + 10f
                    if (newY > screenHeight + 10f) newY = -10f

                    grain.copy(x = newX, y = newY, tumblePhase = grain.tumblePhase)
                }

                windStreaks = windStreaks.map { streak ->
                    var newX = streak.x + streak.speed * windIntensity
                    var newAlpha = streak.alpha

                    if (newX > screenWidth + streak.length) {
                        newX = -streak.length
                        newAlpha = Random.nextFloat() * 0.2f + 0.05f
                    }

                    streak.copy(x = newX, alpha = newAlpha)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val hazeAlpha = (sin(globalPhase * 0.5f) * 0.06f + 0.12f).coerceIn(0f, 0.18f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        dustColorLight.copy(alpha = hazeAlpha * 0.5f),
                        Color.Transparent,
                        dustColorDark.copy(alpha = hazeAlpha * 0.7f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sandColor.copy(alpha = hazeAlpha * 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(screenWidth * 0.7f, screenHeight * 0.3f),
                    radius = (screenHeight * 0.7f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            dustClouds.filter { it.layer == 0 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.7f + sin(cloud.phase) * 0.3f) * windIntensity
                drawDustCloud(cloud, pulseAlpha, dustColorDark)
            }

            dustParticles.filter { it.alpha < 0.1f }.forEach { particle ->
                val pulseAlpha = particle.alpha * (0.6f + sin(particle.phase) * 0.4f)
                val radius = particle.size.coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            dustColorDark.copy(alpha = pulseAlpha),
                            dustColorDark.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(particle.x - radius, particle.y - radius * 0.7f),
                    size = Size(radius * 2f, radius * 1.4f)
                )
            }

            windStreaks.filter { it.alpha < 0.12f }.forEach { streak ->
                val streakAlpha = streak.alpha * windIntensity

                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            windColor.copy(alpha = streakAlpha),
                            windColor.copy(alpha = streakAlpha * 0.5f),
                            Color.Transparent
                        ),
                        startX = streak.x,
                        endX = streak.x + streak.length
                    ),
                    start = Offset(streak.x, streak.y),
                    end = Offset(streak.x + streak.length, streak.y),
                    strokeWidth = streak.width,
                    cap = StrokeCap.Round
                )
            }

            dustClouds.filter { it.layer == 1 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.75f + sin(cloud.phase + 1f) * 0.25f) * windIntensity
                drawDustCloud(cloud, pulseAlpha, dustColorMid)
            }

            dustParticles.filter { it.alpha >= 0.1f }.forEach { particle ->
                val pulseAlpha = particle.alpha * (0.65f + sin(particle.phase) * 0.35f)
                val radius = particle.size.coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            dustColorMid.copy(alpha = pulseAlpha),
                            dustColorMid.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(particle.x - radius, particle.y - radius * 0.6f),
                    size = Size(radius * 2f, radius * 1.2f)
                )
            }

            windStreaks.filter { it.alpha >= 0.12f }.forEach { streak ->
                val streakAlpha = streak.alpha * windIntensity

                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            windColor.copy(alpha = streakAlpha * 0.8f),
                            windColor.copy(alpha = streakAlpha),
                            windColor.copy(alpha = streakAlpha * 0.6f),
                            Color.Transparent
                        ),
                        startX = streak.x,
                        endX = streak.x + streak.length
                    ),
                    start = Offset(streak.x, streak.y),
                    end = Offset(streak.x + streak.length, streak.y),
                    strokeWidth = streak.width,
                    cap = StrokeCap.Round
                )
            }

            dustClouds.filter { it.layer == 2 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.8f + sin(cloud.phase + 2f) * 0.2f) * windIntensity
                drawDustCloud(cloud, pulseAlpha, dustColorLight)
            }

            sandGrains.forEach { grain ->
                val tumbleAlpha = grain.alpha * (0.5f + sin(grain.tumblePhase) * 0.5f)
                val tumbleSize = grain.size * (0.8f + sin(grain.tumblePhase * 2f) * 0.2f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColor.copy(alpha = tumbleAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(grain.x, grain.y),
                        radius = (tumbleSize * 3f).coerceAtLeast(1f)
                    ),
                    radius = (tumbleSize * 3f).coerceAtLeast(1f),
                    center = Offset(grain.x, grain.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sandColor.copy(alpha = tumbleAlpha),
                            dustColorMid.copy(alpha = tumbleAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(grain.x - tumbleSize * 0.1f, grain.y - tumbleSize * 0.1f),
                        radius = tumbleSize.coerceAtLeast(0.5f)
                    ),
                    radius = tumbleSize.coerceAtLeast(0.5f),
                    center = Offset(grain.x, grain.y)
                )
            }

            val edgeDust = (sin(globalPhase * 0.4f) * 0.08f + 0.15f).coerceIn(0f, 0.23f) * windIntensity

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        dustColorLight.copy(alpha = edgeDust * 0.6f),
                        dustColorLight.copy(alpha = edgeDust * 0.2f),
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
                        dustColorMid.copy(alpha = edgeDust * 0.3f),
                        dustColorMid.copy(alpha = edgeDust)
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
                        dustColorLight.copy(alpha = edgeDust * 0.4f),
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
                        dustColorDark.copy(alpha = edgeDust * 0.8f)
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDustCloud(
    cloud: DustCloud,
    alpha: Float,
    color: Color
) {
    val centerX = cloud.x + cloud.width / 2
    val centerY = cloud.y + cloud.height / 2
    val warp = sin(cloud.phase) * 0.15f
    val radius = (cloud.width / 2).coerceAtLeast(1f)

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = alpha * 0.5f),
                color.copy(alpha = alpha * 0.15f),
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
                color.copy(alpha = alpha * 0.55f),
                Color.Transparent
            ),
            center = Offset(centerX - cloud.width * 0.2f, centerY - cloud.height * 0.1f),
            radius = (cloud.width * 0.3f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.08f,
            cloud.y + cloud.height * 0.15f
        ),
        size = Size(cloud.width * 0.45f, cloud.height * 0.55f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.4f),
                Color.Transparent
            ),
            center = Offset(centerX + cloud.width * 0.18f, centerY + cloud.height * 0.08f),
            radius = (cloud.width * 0.25f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.5f,
            cloud.y + cloud.height * 0.3f
        ),
        size = Size(cloud.width * 0.35f, cloud.height * 0.5f)
    )
}