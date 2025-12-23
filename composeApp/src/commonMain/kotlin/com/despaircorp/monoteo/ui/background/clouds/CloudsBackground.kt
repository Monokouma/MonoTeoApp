package com.despaircorp.monoteo.ui.background.clouds

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

private data class Cloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float,
    val puffs: List<CloudPuff>
)

private data class CloudPuff(
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alphaMultiplier: Float
)

private data class HighCloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    var alpha: Float,
    val speed: Float,
    var phase: Float
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

private data class LightRay(
    val x: Float,
    val width: Float,
    val length: Float,
    var alpha: Float,
    var pulsePhase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF5A8FBA),
        Color(0xFF7AA8CC),
        Color(0xFF9AC0DD),
        Color(0xFFB8D4E8),
        Color(0xFFD0E4F2),
        Color(0xFFE4F0F8)
    )
)

private val cloudColorWhite = Color(0xFFFFFFFF)
private val cloudColorLight = Color(0xFFF5F8FA)
private val cloudColorMid = Color(0xFFE0E8EE)
private val cloudColorShadow = Color(0xFFB8C8D4)
private val cloudColorDark = Color(0xFF9AB0C0)
private val highCloudColor = Color(0xFFF8FCFF)
private val particleColor = Color(0xFFFFFFFF)
private val rayColor = Color(0xFFFFFDE8)

@Suppress("EffectKeys")
@Composable
fun CloudsBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var clouds by remember { mutableStateOf(emptyList<Cloud>()) }
    var highClouds by remember { mutableStateOf(emptyList<HighCloud>()) }
    var floatingParticles by remember { mutableStateOf(emptyList<FloatingParticle>()) }
    var lightRays by remember { mutableStateOf(emptyList<LightRay>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            clouds = List(12) { i ->
                val layer = i % 3
                val baseWidth = when (layer) {
                    0 -> Random.nextFloat() * screenWidth * 0.4f + screenWidth * 0.25f
                    1 -> Random.nextFloat() * screenWidth * 0.5f + screenWidth * 0.3f
                    else -> Random.nextFloat() * screenWidth * 0.6f + screenWidth * 0.35f
                }
                val baseHeight = baseWidth * (Random.nextFloat() * 0.3f + 0.25f)

                Cloud(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.25f + screenHeight * 0.05f
                        1 -> Random.nextFloat() * screenHeight * 0.3f + screenHeight * 0.2f
                        else -> Random.nextFloat() * screenHeight * 0.35f + screenHeight * 0.4f
                    },
                    width = baseWidth,
                    height = baseHeight,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.3f + 0.5f
                        1 -> Random.nextFloat() * 0.25f + 0.6f
                        else -> Random.nextFloat() * 0.2f + 0.7f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.15f + 0.05f
                        1 -> Random.nextFloat() * 0.25f + 0.1f
                        else -> Random.nextFloat() * 0.4f + 0.15f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f,
                    puffs = List(Random.nextInt(4, 8)) {
                        CloudPuff(
                            offsetX = Random.nextFloat() * 0.8f - 0.4f,
                            offsetY = Random.nextFloat() * 0.6f - 0.3f,
                            size = Random.nextFloat() * 0.4f + 0.3f,
                            alphaMultiplier = Random.nextFloat() * 0.3f + 0.7f
                        )
                    }
                )
            }

            highClouds = List(8) {
                HighCloud(
                    x = Random.nextFloat() * screenWidth * 2.5f - screenWidth * 0.75f,
                    y = Random.nextFloat() * screenHeight * 0.2f,
                    width = Random.nextFloat() * screenWidth * 0.8f + screenWidth * 0.4f,
                    height = Random.nextFloat() * 25f + 10f,
                    alpha = Random.nextFloat() * 0.2f + 0.1f,
                    speed = Random.nextFloat() * 0.08f + 0.02f,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            floatingParticles = List(30) {
                FloatingParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 2f + 1f,
                    alpha = Random.nextFloat() * 0.4f + 0.2f,
                    speedX = Random.nextFloat() * 0.2f - 0.1f,
                    speedY = Random.nextFloat() * 0.15f - 0.075f,
                    shimmerPhase = Random.nextFloat() * 6.28f
                )
            }

            lightRays = List(5) {
                LightRay(
                    x = screenWidth * (0.3f + Random.nextFloat() * 0.5f),
                    width = Random.nextFloat() * 80f + 40f,
                    length = screenHeight * (Random.nextFloat() * 0.4f + 0.3f),
                    alpha = Random.nextFloat() * 0.08f + 0.03f,
                    pulsePhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.006f) % 6.28f

                clouds = clouds.map { cloud ->
                    cloud.phase += 0.008f
                    var newX = cloud.x + cloud.speed

                    if (newX > screenWidth + cloud.width * 0.6f) {
                        newX = -cloud.width
                    }

                    cloud.copy(x = newX, phase = cloud.phase)
                }

                highClouds = highClouds.map { cloud ->
                    cloud.phase += 0.005f
                    var newX = cloud.x + cloud.speed

                    if (newX > screenWidth + cloud.width * 0.5f) {
                        newX = -cloud.width
                    }

                    cloud.copy(x = newX, phase = cloud.phase)
                }

                floatingParticles = floatingParticles.map { particle ->
                    particle.shimmerPhase += 0.03f
                    var newX = particle.x + particle.speedX
                    var newY = particle.y + particle.speedY

                    if (newX < -10f) newX = screenWidth + 10f
                    if (newX > screenWidth + 10f) newX = -10f
                    if (newY < -10f) newY = screenHeight + 10f
                    if (newY > screenHeight + 10f) newY = -10f

                    particle.copy(x = newX, y = newY, shimmerPhase = particle.shimmerPhase)
                }

                lightRays = lightRays.map { ray ->
                    ray.pulsePhase += 0.012f
                    ray.copy(pulsePhase = ray.pulsePhase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val atmosphereAlpha = (sin(globalPhase * 0.4f) * 0.03f + 0.05f).coerceIn(0f, 0.08f)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = atmosphereAlpha * 0.4f),
                        Color.Transparent,
                        Color.White.copy(alpha = atmosphereAlpha * 0.3f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            lightRays.forEach { ray ->
                val pulseAlpha = ray.alpha * (0.5f + sin(ray.pulsePhase) * 0.5f)

                val rayPath = Path().apply {
                    moveTo(ray.x - ray.width * 0.3f, 0f)
                    lineTo(ray.x + ray.width * 0.3f, 0f)
                    lineTo(ray.x + ray.width * 0.8f, ray.length)
                    lineTo(ray.x - ray.width * 0.8f, ray.length)
                    close()
                }

                drawPath(
                    path = rayPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            rayColor.copy(alpha = pulseAlpha),
                            rayColor.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = ray.length
                    )
                )
            }

            highClouds.forEach { cloud ->
                val wispAlpha = cloud.alpha * (0.7f + sin(cloud.phase) * 0.3f)
                val waveOffset = sin(cloud.phase * 2f) * 5f

                drawOval(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            highCloudColor.copy(alpha = wispAlpha * 0.5f),
                            highCloudColor.copy(alpha = wispAlpha),
                            highCloudColor.copy(alpha = wispAlpha * 0.6f),
                            Color.Transparent
                        ),
                        startX = cloud.x,
                        endX = cloud.x + cloud.width
                    ),
                    topLeft = Offset(cloud.x, cloud.y + waveOffset),
                    size = Size(cloud.width, cloud.height)
                )
            }

            clouds.filter { it.layer == 0 }.sortedBy { it.x }.forEach { cloud ->
                drawCloud(cloud, cloudColorLight, cloudColorMid, cloudColorShadow)
            }

            clouds.filter { it.layer == 1 }.sortedBy { it.x }.forEach { cloud ->
                drawCloud(cloud, cloudColorWhite, cloudColorLight, cloudColorMid)
            }

            clouds.filter { it.layer == 2 }.sortedBy { it.x }.forEach { cloud ->
                drawCloud(cloud, cloudColorWhite, cloudColorLight, cloudColorMid)
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

            val horizonHaze = (sin(globalPhase * 0.3f) * 0.03f + 0.06f).coerceIn(0f, 0.09f)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        cloudColorLight.copy(alpha = horizonHaze * 0.4f),
                        cloudColorLight.copy(alpha = horizonHaze)
                    ),
                    startY = screenHeight * 0.8f,
                    endY = screenHeight
                ),
                topLeft = Offset(0f, screenHeight * 0.8f),
                size = Size(screenWidth, screenHeight * 0.2f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCloud(
    cloud: Cloud,
    colorTop: Color,
    colorMid: Color,
    colorShadow: Color
) {
    val centerX = cloud.x + cloud.width / 2
    val centerY = cloud.y + cloud.height / 2
    val breathe = 1f + sin(cloud.phase) * 0.03f
    val alpha = cloud.baseAlpha

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                colorShadow.copy(alpha = alpha * 0.15f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY + cloud.height * 0.3f),
            radius = (cloud.width * 0.5f * breathe).coerceAtLeast(1f)
        ),
        topLeft = Offset(cloud.x, cloud.y + cloud.height * 0.2f),
        size = Size(cloud.width * breathe, cloud.height * 0.8f)
    )

    cloud.puffs.forEach { puff ->
        val puffX = centerX + puff.offsetX * cloud.width * 0.5f
        val puffY = centerY + puff.offsetY * cloud.height * 0.5f
        val puffRadius = cloud.height * puff.size * breathe
        val puffAlpha = alpha * puff.alphaMultiplier

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colorMid.copy(alpha = puffAlpha * 0.4f),
                    Color.Transparent
                ),
                center = Offset(puffX, puffY),
                radius = (puffRadius * 1.5f).coerceAtLeast(1f)
            ),
            radius = (puffRadius * 1.5f).coerceAtLeast(1f),
            center = Offset(puffX, puffY)
        )
    }

    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(
                colorTop.copy(alpha = alpha * 0.9f),
                colorMid.copy(alpha = alpha * 0.7f),
                colorShadow.copy(alpha = alpha * 0.5f)
            ),
            startY = cloud.y,
            endY = cloud.y + cloud.height
        ),
        topLeft = Offset(cloud.x + cloud.width * 0.1f, cloud.y + cloud.height * 0.15f),
        size = Size(cloud.width * 0.8f * breathe, cloud.height * 0.7f)
    )

    cloud.puffs.forEach { puff ->
        val puffX = centerX + puff.offsetX * cloud.width * 0.45f
        val puffY = centerY + puff.offsetY * cloud.height * 0.4f - cloud.height * 0.1f
        val puffRadius = cloud.height * puff.size * 0.9f * breathe
        val puffAlpha = alpha * puff.alphaMultiplier

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colorTop.copy(alpha = puffAlpha),
                    colorMid.copy(alpha = puffAlpha * 0.6f),
                    colorShadow.copy(alpha = puffAlpha * 0.2f),
                    Color.Transparent
                ),
                center = Offset(puffX - puffRadius * 0.15f, puffY - puffRadius * 0.15f),
                radius = puffRadius.coerceAtLeast(1f)
            ),
            radius = puffRadius.coerceAtLeast(1f),
            center = Offset(puffX, puffY)
        )

        drawCircle(
            color = Color.White.copy(alpha = puffAlpha * 0.6f),
            radius = (puffRadius * 0.35f).coerceAtLeast(0.5f),
            center = Offset(puffX - puffRadius * 0.25f, puffY - puffRadius * 0.25f)
        )
    }

    val topHighlightY = cloud.y + cloud.height * 0.2f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.5f),
                Color.White.copy(alpha = alpha * 0.2f),
                Color.Transparent
            ),
            center = Offset(centerX - cloud.width * 0.1f, topHighlightY),
            radius = (cloud.width * 0.3f).coerceAtLeast(1f)
        ),
        topLeft = Offset(centerX - cloud.width * 0.35f, topHighlightY - cloud.height * 0.15f),
        size = Size(cloud.width * 0.5f, cloud.height * 0.35f)
    )

    val bottomShadowY = cloud.y + cloud.height * 0.65f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                colorShadow.copy(alpha = alpha * 0.35f),
                colorShadow.copy(alpha = alpha * 0.15f),
                Color.Transparent
            ),
            center = Offset(centerX, bottomShadowY),
            radius = (cloud.width * 0.35f).coerceAtLeast(1f)
        ),
        topLeft = Offset(centerX - cloud.width * 0.4f, bottomShadowY - cloud.height * 0.15f),
        size = Size(cloud.width * 0.8f, cloud.height * 0.4f)
    )
}