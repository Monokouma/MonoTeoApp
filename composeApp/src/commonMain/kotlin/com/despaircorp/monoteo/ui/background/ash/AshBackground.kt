package com.despaircorp.monoteo.ui.background.ash

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

private data class AshCloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float
)

private data class AshParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float,
    val tumble: Float
)

private data class AshFlake(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedY: Float,
    val drift: Float,
    var driftPhase: Float,
    var rotationPhase: Float
)

private data class EmberSpark(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedY: Float,
    var pulsePhase: Float,
    var life: Float
)

private data class SmokeWisp(
    var x: Float,
    var y: Float,
    val size: Float,
    val baseAlpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A1816),
        Color(0xFF2A2624),
        Color(0xFF3A3634),
        Color(0xFF4A4644),
        Color(0xFF5A5654)
    )
)

private val ashColorDark = Color(0xFF3A3836)
private val ashColorMid = Color(0xFF5A5856)
private val ashColorLight = Color(0xFF7A7876)
private val ashColorPale = Color(0xFF9A9896)
private val emberColor = Color(0xFFFF6030)
private val emberGlow = Color(0xFFFFAA60)
private val smokeColor = Color(0xFF4A4846)

@Suppress("EffectKeys")
@Composable
fun AshBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var ashClouds by remember { mutableStateOf(emptyList<AshCloud>()) }
    var ashParticles by remember { mutableStateOf(emptyList<AshParticle>()) }
    var ashFlakes by remember { mutableStateOf(emptyList<AshFlake>()) }
    var emberSparks by remember { mutableStateOf(emptyList<EmberSpark>()) }
    var smokeWisps by remember { mutableStateOf(emptyList<SmokeWisp>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            ashClouds = List(12) { i ->
                val layer = i % 3
                AshCloud(
                    x = Random.nextFloat() * screenWidth * 2.5f - screenWidth * 0.75f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.45f
                        1 -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.2f
                        else -> Random.nextFloat() * screenHeight * 0.45f + screenHeight * 0.45f
                    },
                    width = Random.nextFloat() * screenWidth * 1.3f + screenWidth * 0.6f,
                    height = Random.nextFloat() * screenHeight * 0.28f + screenHeight * 0.12f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.1f + 0.05f
                        1 -> Random.nextFloat() * 0.15f + 0.08f
                        else -> Random.nextFloat() * 0.2f + 0.1f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.2f + 0.08f
                        1 -> Random.nextFloat() * 0.35f + 0.15f
                        else -> Random.nextFloat() * 0.5f + 0.25f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            ashParticles = List(60) {
                AshParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 50f + 25f,
                    alpha = Random.nextFloat() * 0.15f + 0.05f,
                    speedX = Random.nextFloat() * 0.4f + 0.1f,
                    speedY = Random.nextFloat() * 0.5f + 0.2f,
                    phase = Random.nextFloat() * 6.28f,
                    tumble = Random.nextFloat() * 1.5f + 0.5f
                )
            }

            ashFlakes = List(100) {
                AshFlake(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 5f + 2f,
                    alpha = Random.nextFloat() * 0.7f + 0.3f,
                    speedY = Random.nextFloat() * 1.2f + 0.4f,
                    drift = Random.nextFloat() * 25f + 10f,
                    driftPhase = Random.nextFloat() * 6.28f,
                    rotationPhase = Random.nextFloat() * 6.28f
                )
            }

            emberSparks = List(20) {
                EmberSpark(
                    x = Random.nextFloat() * screenWidth,
                    y = screenHeight + Random.nextFloat() * 100f,
                    size = Random.nextFloat() * 4f + 2f,
                    alpha = Random.nextFloat() * 0.8f + 0.2f,
                    speedY = -(Random.nextFloat() * 1.5f + 0.8f),
                    pulsePhase = Random.nextFloat() * 6.28f,
                    life = 1f
                )
            }

            smokeWisps = List(25) {
                SmokeWisp(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 100f + 50f,
                    baseAlpha = Random.nextFloat() * 0.12f + 0.04f,
                    speedX = Random.nextFloat() * 0.3f + 0.05f,
                    speedY = -(Random.nextFloat() * 0.25f + 0.1f),
                    phase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.008f) % 6.28f

                ashClouds = ashClouds.map { cloud ->
                    cloud.phase += 0.006f
                    var newX = cloud.x + cloud.speed
                    if (newX > screenWidth + cloud.width * 0.5f) {
                        newX = -cloud.width
                    }
                    cloud.copy(x = newX, phase = cloud.phase)
                }

                ashParticles = ashParticles.map { particle ->
                    particle.phase += 0.018f
                    var newX = particle.x + particle.speedX + sin(particle.phase * particle.tumble) * 0.4f
                    var newY = particle.y + particle.speedY

                    if (newY > screenHeight + particle.size) {
                        newY = -particle.size
                        newX = Random.nextFloat() * screenWidth
                    }
                    if (newX > screenWidth + particle.size) newX = -particle.size
                    if (newX < -particle.size) newX = screenWidth + particle.size

                    particle.copy(x = newX, y = newY, phase = particle.phase)
                }

                ashFlakes = ashFlakes.map { flake ->
                    flake.driftPhase += 0.025f
                    flake.rotationPhase += 0.04f
                    var newX = flake.x + sin(flake.driftPhase) * flake.drift * 0.015f
                    var newY = flake.y + flake.speedY

                    if (newY > screenHeight + 15f) {
                        newY = -15f
                        newX = Random.nextFloat() * screenWidth
                    }

                    flake.copy(x = newX, y = newY, driftPhase = flake.driftPhase, rotationPhase = flake.rotationPhase)
                }

                emberSparks = emberSparks.map { spark ->
                    spark.pulsePhase += 0.12f
                    var newY = spark.y + spark.speedY
                    val newLife = spark.life - 0.003f
                    var newAlpha = spark.alpha

                    if (newLife <= 0f || newY < -20f) {
                        newY = screenHeight + Random.nextFloat() * 50f
                        newAlpha = Random.nextFloat() * 0.8f + 0.2f
                        spark.copy(
                            x = Random.nextFloat() * screenWidth,
                            y = newY,
                            alpha = newAlpha,
                            life = 1f,
                            pulsePhase = spark.pulsePhase
                        )
                    } else {
                        spark.copy(y = newY, life = newLife, pulsePhase = spark.pulsePhase)
                    }
                }

                smokeWisps = smokeWisps.map { wisp ->
                    wisp.phase += 0.01f
                    var newX = wisp.x + wisp.speedX
                    var newY = wisp.y + wisp.speedY

                    if (newY < -wisp.size) {
                        newY = screenHeight + wisp.size
                        newX = Random.nextFloat() * screenWidth
                    }
                    if (newX > screenWidth + wisp.size) newX = -wisp.size

                    wisp.copy(x = newX, y = newY, phase = wisp.phase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val hazeAlpha = (sin(globalPhase * 0.4f) * 0.06f + 0.1f).coerceIn(0f, 0.16f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ashColorMid.copy(alpha = hazeAlpha * 0.5f),
                        Color.Transparent,
                        ashColorDark.copy(alpha = hazeAlpha * 0.7f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        emberGlow.copy(alpha = hazeAlpha * 0.12f),
                        emberColor.copy(alpha = hazeAlpha * 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(screenWidth * 0.5f, screenHeight * 0.92f),
                    radius = (screenHeight * 0.55f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            ashClouds.filter { it.layer == 0 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.75f + sin(cloud.phase) * 0.25f)
                drawAshCloud(cloud, pulseAlpha, ashColorDark)
            }

            smokeWisps.filter { it.baseAlpha < 0.08f }.forEach { wisp ->
                val pulseAlpha = wisp.baseAlpha * (0.65f + sin(wisp.phase) * 0.35f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            smokeColor.copy(alpha = pulseAlpha),
                            smokeColor.copy(alpha = pulseAlpha * 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(wisp.x, wisp.y),
                        radius = wisp.size.coerceAtLeast(1f)
                    ),
                    radius = wisp.size.coerceAtLeast(1f),
                    center = Offset(wisp.x, wisp.y)
                )
            }

            ashParticles.filter { it.alpha < 0.1f }.forEach { particle ->
                val pulseAlpha = particle.alpha * (0.6f + sin(particle.phase) * 0.4f)
                val radius = particle.size.coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ashColorDark.copy(alpha = pulseAlpha),
                            ashColorDark.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(particle.x - radius, particle.y - radius * 0.7f),
                    size = Size(radius * 2f, radius * 1.4f)
                )
            }

            ashClouds.filter { it.layer == 1 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.8f + sin(cloud.phase + 1f) * 0.2f)
                drawAshCloud(cloud, pulseAlpha, ashColorMid)
            }

            smokeWisps.filter { it.baseAlpha >= 0.08f }.forEach { wisp ->
                val pulseAlpha = wisp.baseAlpha * (0.7f + sin(wisp.phase) * 0.3f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            smokeColor.copy(alpha = pulseAlpha),
                            smokeColor.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(wisp.x, wisp.y),
                        radius = wisp.size.coerceAtLeast(1f)
                    ),
                    radius = wisp.size.coerceAtLeast(1f),
                    center = Offset(wisp.x, wisp.y)
                )
            }

            ashParticles.filter { it.alpha >= 0.1f }.forEach { particle ->
                val pulseAlpha = particle.alpha * (0.65f + sin(particle.phase) * 0.35f)
                val radius = particle.size.coerceAtLeast(1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ashColorMid.copy(alpha = pulseAlpha),
                            ashColorMid.copy(alpha = pulseAlpha * 0.35f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = radius
                    ),
                    topLeft = Offset(particle.x - radius, particle.y - radius * 0.65f),
                    size = Size(radius * 2f, radius * 1.3f)
                )
            }

            ashClouds.filter { it.layer == 2 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.85f + sin(cloud.phase + 2f) * 0.15f)
                drawAshCloud(cloud, pulseAlpha, ashColorLight)
            }

            emberSparks.forEach { spark ->
                val pulseAlpha = spark.alpha * spark.life * (0.5f + sin(spark.pulsePhase) * 0.5f)
                val pulseSize = spark.size * (0.8f + sin(spark.pulsePhase * 2f) * 0.2f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            emberGlow.copy(alpha = pulseAlpha * 0.35f),
                            emberColor.copy(alpha = pulseAlpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(spark.x, spark.y),
                        radius = (pulseSize * 5f).coerceAtLeast(1f)
                    ),
                    radius = (pulseSize * 5f).coerceAtLeast(1f),
                    center = Offset(spark.x, spark.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            emberGlow.copy(alpha = pulseAlpha),
                            emberColor.copy(alpha = pulseAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(spark.x, spark.y),
                        radius = (pulseSize * 2f).coerceAtLeast(1f)
                    ),
                    radius = (pulseSize * 2f).coerceAtLeast(1f),
                    center = Offset(spark.x, spark.y)
                )

                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * 0.85f),
                    radius = (pulseSize * 0.5f).coerceAtLeast(0.5f),
                    center = Offset(spark.x, spark.y)
                )
            }

            ashFlakes.forEach { flake ->
                val flickerAlpha = flake.alpha * (0.5f + sin(flake.rotationPhase) * 0.5f)
                val rotatedSize = flake.size * (0.6f + cos(flake.rotationPhase) * 0.4f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ashColorPale.copy(alpha = flickerAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(flake.x, flake.y),
                        radius = (rotatedSize * 3f).coerceAtLeast(1f)
                    ),
                    radius = (rotatedSize * 3f).coerceAtLeast(1f),
                    center = Offset(flake.x, flake.y)
                )

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ashColorPale.copy(alpha = flickerAlpha),
                            ashColorLight.copy(alpha = flickerAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(flake.x, flake.y),
                        radius = rotatedSize.coerceAtLeast(0.5f)
                    ),
                    topLeft = Offset(flake.x - rotatedSize, flake.y - rotatedSize * 0.7f),
                    size = Size((rotatedSize * 2f).coerceAtLeast(1f), (rotatedSize * 1.4f).coerceAtLeast(1f))
                )
            }

            val edgeAsh = (sin(globalPhase * 0.5f) * 0.07f + 0.12f).coerceIn(0f, 0.19f)

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ashColorMid.copy(alpha = edgeAsh * 0.6f),
                        ashColorMid.copy(alpha = edgeAsh * 0.2f),
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
                        ashColorMid.copy(alpha = edgeAsh * 0.25f),
                        ashColorMid.copy(alpha = edgeAsh * 0.7f)
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
                        ashColorLight.copy(alpha = edgeAsh * 0.5f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = screenHeight * 0.3f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight * 0.3f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        ashColorDark.copy(alpha = edgeAsh * 0.4f),
                        ashColorDark.copy(alpha = edgeAsh * 0.85f)
                    ),
                    startY = screenHeight * 0.7f,
                    endY = screenHeight
                ),
                topLeft = Offset(0f, screenHeight * 0.7f),
                size = Size(screenWidth, screenHeight * 0.3f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAshCloud(
    cloud: AshCloud,
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
                color.copy(alpha = alpha * 0.55f),
                Color.Transparent
            ),
            center = Offset(centerX - cloud.width * 0.18f, centerY - cloud.height * 0.12f),
            radius = (cloud.width * 0.3f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.07f,
            cloud.y + cloud.height * 0.12f
        ),
        size = Size(cloud.width * 0.45f, cloud.height * 0.6f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.4f),
                Color.Transparent
            ),
            center = Offset(centerX + cloud.width * 0.14f, centerY + cloud.height * 0.08f),
            radius = (cloud.width * 0.25f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            cloud.x + cloud.width * 0.48f,
            cloud.y + cloud.height * 0.26f
        ),
        size = Size(cloud.width * 0.38f, cloud.height * 0.52f)
    )
}