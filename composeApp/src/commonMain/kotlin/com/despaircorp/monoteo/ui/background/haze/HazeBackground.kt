package com.despaircorp.monoteo.ui.background.haze

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

private data class HazeLayer(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val depth: Int,
    var phase: Float
)

private data class HazeParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    val baseAlpha: Float,
    val speedX: Float,
    var phase: Float
)

private data class SunRay(
    val x: Float,
    val angle: Float,
    val width: Float,
    val length: Float,
    var alpha: Float,
    var pulsePhase: Float
)

private data class DustMote(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    var shimmerPhase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFB8A090),
        Color(0xFFC8B0A0),
        Color(0xFFD8C0B0),
        Color(0xFFE8D0C0),
        Color(0xFFF0E0D0)
    )
)

private val hazeColorWarm = Color(0xFFE8D8C8)
private val hazeColorLight = Color(0xFFF5EBE0)
private val hazeColorGold = Color(0xFFFFE8D0)
private val sunColor = Color(0xFFFFE4B5)
private val dustColor = Color(0xFFFFF8E8)

@Suppress("EffectKeys")
@Composable
fun HazeBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var hazeLayers by remember { mutableStateOf(emptyList<HazeLayer>()) }
    var hazeParticles by remember { mutableStateOf(emptyList<HazeParticle>()) }
    var sunRays by remember { mutableStateOf(emptyList<SunRay>()) }
    var dustMotes by remember { mutableStateOf(emptyList<DustMote>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            hazeLayers = List(10) { i ->
                val depth = i % 3
                HazeLayer(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = when (depth) {
                        0 -> Random.nextFloat() * screenHeight * 0.5f
                        1 -> Random.nextFloat() * screenHeight * 0.6f + screenHeight * 0.2f
                        else -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.4f
                    },
                    width = Random.nextFloat() * screenWidth * 1.5f + screenWidth * 0.8f,
                    height = Random.nextFloat() * screenHeight * 0.4f + screenHeight * 0.2f,
                    baseAlpha = when (depth) {
                        0 -> Random.nextFloat() * 0.1f + 0.05f
                        1 -> Random.nextFloat() * 0.15f + 0.08f
                        else -> Random.nextFloat() * 0.2f + 0.1f
                    },
                    speed = when (depth) {
                        0 -> Random.nextFloat() * 0.1f + 0.03f
                        1 -> Random.nextFloat() * 0.15f + 0.05f
                        else -> Random.nextFloat() * 0.2f + 0.08f
                    },
                    depth = depth,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            hazeParticles = List(40) {
                HazeParticle(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 150f + 80f,
                    baseAlpha = Random.nextFloat() * 0.12f + 0.04f,
                    speedX = Random.nextFloat() * 0.2f + 0.05f,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            sunRays = List(8) {
                SunRay(
                    x = screenWidth * 0.7f + Random.nextFloat() * screenWidth * 0.3f,
                    angle = Random.nextFloat() * 0.4f - 0.2f,
                    width = Random.nextFloat() * 80f + 40f,
                    length = screenHeight * (Random.nextFloat() * 0.5f + 0.5f),
                    alpha = Random.nextFloat() * 0.15f + 0.05f,
                    pulsePhase = Random.nextFloat() * 6.28f
                )
            }

            dustMotes = List(50) {
                DustMote(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 3f + 1f,
                    alpha = Random.nextFloat() * 0.6f + 0.3f,
                    speedX = Random.nextFloat() * 0.3f - 0.15f,
                    speedY = Random.nextFloat() * 0.2f - 0.1f,
                    shimmerPhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.005f) % 6.28f

                hazeLayers = hazeLayers.map { layer ->
                    layer.phase += 0.006f
                    var newX = layer.x + layer.speed
                    if (newX > screenWidth + layer.width * 0.5f) {
                        newX = -layer.width
                    }
                    layer.copy(x = newX, phase = layer.phase)
                }

                hazeParticles = hazeParticles.map { particle ->
                    particle.phase += 0.008f
                    var newX = particle.x + particle.speedX
                    val newY = particle.y + sin(particle.phase) * 0.15f

                    if (newX > screenWidth + particle.size) {
                        newX = -particle.size
                    }

                    particle.copy(
                        x = newX,
                        y = newY.coerceIn(0f, screenHeight),
                        phase = particle.phase
                    )
                }

                sunRays = sunRays.map { ray ->
                    ray.pulsePhase += 0.015f
                    ray.copy(pulsePhase = ray.pulsePhase)
                }

                dustMotes = dustMotes.map { mote ->
                    mote.shimmerPhase += 0.05f
                    var newX = mote.x + mote.speedX
                    var newY = mote.y + mote.speedY

                    if (newX < -10f) newX = screenWidth + 10f
                    if (newX > screenWidth + 10f) newX = -10f
                    if (newY < -10f) newY = screenHeight + 10f
                    if (newY > screenHeight + 10f) newY = -10f

                    mote.copy(x = newX, y = newY, shimmerPhase = mote.shimmerPhase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val breatheAlpha = (sin(globalPhase) * 0.05f + 0.1f).coerceIn(0f, 0.15f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        hazeColorGold.copy(alpha = breatheAlpha * 0.6f),
                        Color.Transparent,
                        hazeColorWarm.copy(alpha = breatheAlpha * 0.4f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        sunColor.copy(alpha = breatheAlpha * 0.5f),
                        sunColor.copy(alpha = breatheAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(screenWidth * 0.8f, screenHeight * 0.15f),
                    radius = (screenHeight * 0.6f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            sunRays.forEach { ray ->
                val pulseAlpha = ray.alpha * (0.6f + sin(ray.pulsePhase) * 0.4f)
                val rayPath = Path().apply {
                    moveTo(ray.x, 0f)
                    lineTo(ray.x - ray.width / 2 + ray.angle * ray.length, ray.length)
                    lineTo(ray.x + ray.width / 2 + ray.angle * ray.length, ray.length)
                    close()
                }

                drawPath(
                    path = rayPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            sunColor.copy(alpha = pulseAlpha),
                            sunColor.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = ray.length
                    )
                )
            }

            hazeLayers.filter { it.depth == 0 }.forEach { layer ->
                val pulseAlpha = layer.baseAlpha * (0.8f + sin(layer.phase) * 0.2f)
                drawHazeLayer(layer, pulseAlpha, hazeColorWarm)
            }

            hazeParticles.filter { it.baseAlpha < 0.08f }.forEach { particle ->
                val pulseAlpha = particle.baseAlpha * (0.7f + sin(particle.phase) * 0.3f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            hazeColorWarm.copy(alpha = pulseAlpha),
                            hazeColorWarm.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = particle.size.coerceAtLeast(1f)
                    ),
                    radius = particle.size.coerceAtLeast(1f),
                    center = Offset(particle.x, particle.y)
                )
            }

            hazeLayers.filter { it.depth == 1 }.forEach { layer ->
                val pulseAlpha = layer.baseAlpha * (0.85f + sin(layer.phase + 1f) * 0.15f)
                drawHazeLayer(layer, pulseAlpha, hazeColorLight)
            }

            hazeParticles.filter { it.baseAlpha >= 0.08f }.forEach { particle ->
                val pulseAlpha = particle.baseAlpha * (0.75f + sin(particle.phase) * 0.25f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            hazeColorLight.copy(alpha = pulseAlpha),
                            hazeColorLight.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(particle.x, particle.y),
                        radius = particle.size.coerceAtLeast(1f)
                    ),
                    radius = particle.size.coerceAtLeast(1f),
                    center = Offset(particle.x, particle.y)
                )
            }

            hazeLayers.filter { it.depth == 2 }.forEach { layer ->
                val pulseAlpha = layer.baseAlpha * (0.9f + sin(layer.phase + 2f) * 0.1f)
                drawHazeLayer(layer, pulseAlpha, hazeColorGold)
            }

            dustMotes.forEach { mote ->
                val shimmerAlpha = mote.alpha * (0.4f + sin(mote.shimmerPhase) * 0.6f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            dustColor.copy(alpha = shimmerAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(mote.x, mote.y),
                        radius = (mote.size * 4f).coerceAtLeast(1f)
                    ),
                    radius = (mote.size * 4f).coerceAtLeast(1f),
                    center = Offset(mote.x, mote.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = shimmerAlpha),
                            dustColor.copy(alpha = shimmerAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(mote.x, mote.y),
                        radius = mote.size.coerceAtLeast(0.5f)
                    ),
                    radius = mote.size.coerceAtLeast(0.5f),
                    center = Offset(mote.x, mote.y)
                )
            }

            val edgeHaze = (sin(globalPhase * 0.7f) * 0.06f + 0.1f).coerceIn(0f, 0.16f)

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        hazeColorLight.copy(alpha = edgeHaze),
                        hazeColorLight.copy(alpha = edgeHaze * 0.3f),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = screenWidth * 0.35f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth * 0.35f, screenHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        hazeColorGold.copy(alpha = edgeHaze * 0.4f),
                        hazeColorGold.copy(alpha = edgeHaze * 0.8f)
                    ),
                    startX = screenWidth * 0.65f,
                    endX = screenWidth
                ),
                topLeft = Offset(screenWidth * 0.65f, 0f),
                size = Size(screenWidth * 0.35f, screenHeight)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        hazeColorGold.copy(alpha = edgeHaze * 0.5f),
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
                        hazeColorWarm.copy(alpha = edgeHaze * 0.6f)
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHazeLayer(
    layer: HazeLayer,
    alpha: Float,
    color: Color
) {
    val centerX = layer.x + layer.width / 2
    val centerY = layer.y + layer.height / 2
    val radius = (layer.width / 2).coerceAtLeast(1f)

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
        topLeft = Offset(layer.x, layer.y),
        size = Size(layer.width, layer.height)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(centerX - layer.width * 0.15f, centerY - layer.height * 0.1f),
            radius = (layer.width * 0.3f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            layer.x + layer.width * 0.1f,
            layer.y + layer.height * 0.15f
        ),
        size = Size(layer.width * 0.5f, layer.height * 0.6f)
    )
}