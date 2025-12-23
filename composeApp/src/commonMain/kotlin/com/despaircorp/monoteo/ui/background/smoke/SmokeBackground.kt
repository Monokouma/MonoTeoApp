package com.despaircorp.monoteo.ui.background.smoke

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import kotlin.math.sin
import kotlin.random.Random

private data class SmokeCloud(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speedX: Float,
    val speedY: Float,
    val layer: Int,
    var phase: Float,
    var rotation: Float
)

private data class SmokeParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    val baseAlpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float,
    val turbulence: Float
)

private data class AshParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedY: Float,
    val drift: Float,
    var driftPhase: Float
)

private data class EmberGlow(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float,
    var pulsePhase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF2A2420),
        Color(0xFF3D3530),
        Color(0xFF504840),
        Color(0xFF635850),
        Color(0xFF756860)
    )
)

private val smokeColorDark = Color(0xFF3A3632)
private val smokeColorMid = Color(0xFF5A5550)
private val smokeColorLight = Color(0xFF8A8580)
private val ashColor = Color(0xFF6A6560)
private val emberColor = Color(0xFFFF6B35)
private val emberGlowColor = Color(0xFFFFAA55)

@Composable
fun SmokeBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var smokeClouds by remember { mutableStateOf(emptyList<SmokeCloud>()) }
    var smokeParticles by remember { mutableStateOf(emptyList<SmokeParticle>()) }
    var ashParticles by remember { mutableStateOf(emptyList<AshParticle>()) }
    var emberGlows by remember { mutableStateOf(emptyList<EmberGlow>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            smokeClouds = List(15) { i ->
                val layer = i % 3
                SmokeCloud(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = when (layer) {
                        0 -> screenHeight + Random.nextFloat() * screenHeight * 0.3f
                        1 -> screenHeight * 0.7f + Random.nextFloat() * screenHeight * 0.4f
                        else -> screenHeight * 0.5f + Random.nextFloat() * screenHeight * 0.5f
                    },
                    width = Random.nextFloat() * screenWidth * 0.8f + screenWidth * 0.4f,
                    height = Random.nextFloat() * screenHeight * 0.35f + screenHeight * 0.15f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.12f + 0.06f
                        1 -> Random.nextFloat() * 0.18f + 0.1f
                        else -> Random.nextFloat() * 0.22f + 0.12f
                    },
                    speedX = Random.nextFloat() * 0.4f - 0.2f,
                    speedY = when (layer) {
                        0 -> -(Random.nextFloat() * 0.3f + 0.15f)
                        1 -> -(Random.nextFloat() * 0.5f + 0.25f)
                        else -> -(Random.nextFloat() * 0.7f + 0.35f)
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f,
                    rotation = Random.nextFloat() * 0.02f - 0.01f
                )
            }

            smokeParticles = List(60) {
                SmokeParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 100f + 50f,
                    baseAlpha = Random.nextFloat() * 0.15f + 0.05f,
                    speedX = Random.nextFloat() * 0.6f - 0.3f,
                    speedY = -(Random.nextFloat() * 0.4f + 0.2f),
                    phase = Random.nextFloat() * 6.28f,
                    turbulence = Random.nextFloat() * 2f + 1f
                )
            }

            ashParticles = List(40) {
                AshParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 3f + 1f,
                    alpha = Random.nextFloat() * 0.6f + 0.3f,
                    speedY = -(Random.nextFloat() * 0.8f + 0.3f),
                    drift = Random.nextFloat() * 30f + 15f,
                    driftPhase = Random.nextFloat() * 6.28f
                )
            }

            emberGlows = List(12) {
                EmberGlow(
                    x = Random.nextFloat() * screenWidth,
                    y = screenHeight * 0.7f + Random.nextFloat() * screenHeight * 0.3f,
                    alpha = Random.nextFloat() * 0.4f + 0.2f,
                    size = Random.nextFloat() * 8f + 4f,
                    pulsePhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.008f) % 6.28f

                smokeClouds = smokeClouds.map { cloud ->
                    cloud.phase += 0.012f
                    var newX = cloud.x + cloud.speedX + sin(cloud.phase) * 0.5f
                    var newY = cloud.y + cloud.speedY

                    if (newY < -cloud.height) {
                        newY = screenHeight + cloud.height * 0.5f
                        newX = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f
                    }

                    cloud.copy(x = newX, y = newY, phase = cloud.phase)
                }

                smokeParticles = smokeParticles.map { particle ->
                    particle.phase += 0.02f
                    var newX = particle.x + particle.speedX + sin(particle.phase * particle.turbulence) * 0.8f
                    var newY = particle.y + particle.speedY

                    if (newY < -particle.size) {
                        newY = screenHeight + particle.size
                        newX = Random.nextFloat() * screenWidth
                    }
                    if (newX < -particle.size) newX = screenWidth + particle.size
                    if (newX > screenWidth + particle.size) newX = -particle.size

                    particle.copy(x = newX, y = newY, phase = particle.phase)
                }

                ashParticles = ashParticles.map { ash ->
                    ash.driftPhase += 0.03f
                    var newX = ash.x + sin(ash.driftPhase) * ash.drift * 0.02f
                    var newY = ash.y + ash.speedY

                    if (newY < -10f) {
                        newY = screenHeight + 10f
                        newX = Random.nextFloat() * screenWidth
                    }

                    ash.copy(x = newX, y = newY, driftPhase = ash.driftPhase)
                }

                emberGlows = emberGlows.map { ember ->
                    ember.pulsePhase += 0.08f
                    ember.copy(pulsePhase = ember.pulsePhase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val hazeAlpha = (sin(globalPhase * 0.5f) * 0.06f + 0.1f).coerceIn(0f, 0.16f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        smokeColorLight.copy(alpha = hazeAlpha * 0.5f),
                        Color.Transparent,
                        smokeColorDark.copy(alpha = hazeAlpha * 0.8f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        emberGlowColor.copy(alpha = hazeAlpha * 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(screenWidth * 0.5f, screenHeight * 0.9f),
                    radius = (screenHeight * 0.6f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            smokeClouds.filter { it.layer == 0 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.7f + sin(cloud.phase) * 0.3f)
                drawSmokeCloud(cloud, pulseAlpha, smokeColorDark)
            }

            smokeParticles.filter { it.baseAlpha < 0.1f }.forEach { particle ->
                val pulseAlpha = particle.baseAlpha * (0.6f + sin(particle.phase) * 0.4f)
                val swirl = sin(particle.phase * 0.5f) * 0.2f
                val radius = (particle.size * (1f + swirl)).coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            smokeColorDark.copy(alpha = pulseAlpha),
                            smokeColorDark.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(
                        particle.x - radius,
                        particle.y - radius * 0.8f
                    ),
                    size = Size(radius * 2f, radius * 1.6f)
                )
            }

            smokeClouds.filter { it.layer == 1 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.75f + sin(cloud.phase + 1f) * 0.25f)
                drawSmokeCloud(cloud, pulseAlpha, smokeColorMid)
            }

            smokeParticles.filter { it.baseAlpha >= 0.1f }.forEach { particle ->
                val pulseAlpha = particle.baseAlpha * (0.65f + sin(particle.phase) * 0.35f)
                val swirl = sin(particle.phase * 0.7f) * 0.15f
                val radius = (particle.size * (1f + swirl)).coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            smokeColorMid.copy(alpha = pulseAlpha),
                            smokeColorMid.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(
                        particle.x - radius,
                        particle.y - radius * 0.7f
                    ),
                    size = Size(radius * 2f, radius * 1.4f)
                )
            }

            smokeClouds.filter { it.layer == 2 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.8f + sin(cloud.phase + 2f) * 0.2f)
                drawSmokeCloud(cloud, pulseAlpha, smokeColorLight)
            }

            emberGlows.forEach { ember ->
                val pulseAlpha = ember.alpha * (0.4f + sin(ember.pulsePhase) * 0.6f)
                val flickerSize = (ember.size * (0.8f + sin(ember.pulsePhase * 3f) * 0.2f)).coerceAtLeast(1f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            emberGlowColor.copy(alpha = pulseAlpha * 0.3f),
                            emberColor.copy(alpha = pulseAlpha * 0.1f),
                            Color.Transparent
                        ),
                        center = Offset(ember.x, ember.y),
                        radius = flickerSize * 6f
                    ),
                    radius = flickerSize * 6f,
                    center = Offset(ember.x, ember.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            emberGlowColor.copy(alpha = pulseAlpha),
                            emberColor.copy(alpha = pulseAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(ember.x, ember.y),
                        radius = flickerSize * 2f
                    ),
                    radius = flickerSize * 2f,
                    center = Offset(ember.x, ember.y)
                )

                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * 0.8f),
                    radius = flickerSize * 0.4f,
                    center = Offset(ember.x, ember.y)
                )
            }

            ashParticles.forEach { ash ->
                val flickerAlpha = ash.alpha * (0.5f + sin(ash.driftPhase * 2f) * 0.5f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ashColor.copy(alpha = flickerAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(ash.x, ash.y),
                        radius = (ash.size * 3f).coerceAtLeast(1f)
                    ),
                    radius = (ash.size * 3f).coerceAtLeast(1f),
                    center = Offset(ash.x, ash.y)
                )

                drawCircle(
                    color = ashColor.copy(alpha = flickerAlpha),
                    radius = ash.size.coerceAtLeast(0.5f),
                    center = Offset(ash.x, ash.y)
                )

                if (ash.y > screenHeight * 0.6f && Random.nextFloat() > 0.95f) {
                    drawCircle(
                        color = emberColor.copy(alpha = flickerAlpha * 0.4f),
                        radius = (ash.size * 0.6f).coerceAtLeast(0.5f),
                        center = Offset(ash.x, ash.y)
                    )
                }
            }

            val bottomHaze = (sin(globalPhase * 0.3f) * 0.08f + 0.15f).coerceIn(0f, 0.23f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        smokeColorDark.copy(alpha = bottomHaze * 0.5f),
                        smokeColorDark.copy(alpha = bottomHaze)
                    ),
                    startY = screenHeight * 0.5f,
                    endY = screenHeight
                ),
                topLeft = Offset(0f, screenHeight * 0.5f),
                size = Size(screenWidth, screenHeight * 0.5f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        smokeColorLight.copy(alpha = bottomHaze * 0.4f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = screenHeight * 0.35f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight * 0.35f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSmokeCloud(
    cloud: SmokeCloud,
    alpha: Float,
    color: Color
) {
    val centerX = cloud.x + cloud.width / 2
    val centerY = cloud.y + cloud.height / 2
    val warp = sin(cloud.phase) * 0.1f
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
                color.copy(alpha = alpha * 0.6f),
                Color.Transparent
            ),
            center = Offset(centerX - cloud.width * 0.2f, centerY - cloud.height * 0.15f),
            radius = (cloud.width * 0.35f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.05f,
            cloud.y + cloud.height * 0.1f
        ),
        size = Size(cloud.width * 0.5f, cloud.height * 0.6f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.45f),
                Color.Transparent
            ),
            center = Offset(centerX + cloud.width * 0.15f, centerY + cloud.height * 0.1f),
            radius = (cloud.width * 0.28f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.45f,
            cloud.y + cloud.height * 0.25f
        ),
        size = Size(cloud.width * 0.4f, cloud.height * 0.55f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.3f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY - cloud.height * 0.3f),
            radius = (cloud.width * 0.2f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.35f,
            cloud.y - cloud.height * 0.1f
        ),
        size = Size(cloud.width * 0.3f, cloud.height * 0.4f)
    )
}