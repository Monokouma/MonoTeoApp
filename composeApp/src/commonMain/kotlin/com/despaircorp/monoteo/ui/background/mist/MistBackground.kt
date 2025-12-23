package com.despaircorp.monoteo.ui.background.mist

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

private data class MistCloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float
)

private data class FloatingMist(
    var x: Float,
    var y: Float,
    val size: Float,
    val baseAlpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float
)

private data class CondensationDrop(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float,
    var pulsePhase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF8899AA),
        Color(0xFF99AABB),
        Color(0xFFAABBCC),
        Color(0xFFBBCCDD),
        Color(0xFFCCDDEE)
    )
)

private val mistWhite = Color(0xFFFFFFFF)
private val mistLight = Color(0xFFF0F4F8)
private val mistMid = Color(0xFFE0E8F0)

@Composable
fun MistBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var mistClouds by remember { mutableStateOf(emptyList<MistCloud>()) }
    var floatingMist by remember { mutableStateOf(emptyList<FloatingMist>()) }
    var condensationDrops by remember { mutableStateOf(emptyList<CondensationDrop>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            mistClouds = List(18) { i ->
                val layer = i % 3
                MistCloud(
                    x = Random.nextFloat() * screenWidth * 2.5f - screenWidth * 0.75f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.4f
                        1 -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.25f
                        else -> Random.nextFloat() * screenHeight * 0.4f + screenHeight * 0.5f
                    },
                    width = Random.nextFloat() * screenWidth * 1.2f + screenWidth * 0.6f,
                    height = Random.nextFloat() * screenHeight * 0.25f + screenHeight * 0.1f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.08f + 0.04f
                        1 -> Random.nextFloat() * 0.12f + 0.06f
                        else -> Random.nextFloat() * 0.15f + 0.08f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.15f + 0.05f
                        1 -> Random.nextFloat() * 0.25f + 0.1f
                        else -> Random.nextFloat() * 0.4f + 0.15f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            floatingMist = List(50) {
                FloatingMist(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 120f + 60f,
                    baseAlpha = Random.nextFloat() * 0.08f + 0.02f,
                    speedX = Random.nextFloat() * 0.3f + 0.05f,
                    speedY = Random.nextFloat() * 0.1f - 0.05f,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            condensationDrops = List(35) {
                CondensationDrop(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    alpha = Random.nextFloat() * 0.5f + 0.3f,
                    size = Random.nextFloat() * 5f + 2f,
                    pulsePhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                globalPhase = (globalPhase + 0.006f) % 6.28f

                mistClouds = mistClouds.map { cloud ->
                    cloud.phase += 0.008f
                    var newX = cloud.x + cloud.speed
                    if (newX > screenWidth + cloud.width * 0.5f) {
                        newX = -cloud.width
                    }
                    cloud.copy(x = newX, phase = cloud.phase)
                }

                floatingMist = floatingMist.map { mist ->
                    mist.phase += 0.015f
                    var newX = mist.x + mist.speedX
                    var newY = mist.y + mist.speedY + sin(mist.phase) * 0.2f

                    if (newX > screenWidth + mist.size) newX = -mist.size
                    if (newY < -mist.size) newY = screenHeight + mist.size
                    if (newY > screenHeight + mist.size) newY = -mist.size

                    mist.copy(x = newX, y = newY, phase = mist.phase)
                }

                condensationDrops = condensationDrops.map { drop ->
                    drop.pulsePhase += 0.03f
                    drop.copy(pulsePhase = drop.pulsePhase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        val breatheAlpha = (sin(globalPhase) * 0.06f + 0.1f).coerceIn(0f, 0.16f)

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            mistWhite.copy(alpha = breatheAlpha * 0.8f),
                            Color.Transparent,
                            mistWhite.copy(alpha = breatheAlpha * 0.5f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            mistClouds.filter { it.layer == 0 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.8f + sin(cloud.phase) * 0.2f)
                drawMistCloud(cloud, pulseAlpha, mistMid)
            }

            floatingMist.filter { it.baseAlpha < 0.05f }.forEach { mist ->
                val pulseAlpha = mist.baseAlpha * (0.7f + sin(mist.phase) * 0.3f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            mistMid.copy(alpha = pulseAlpha),
                            mistMid.copy(alpha = pulseAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(mist.x, mist.y),
                        radius = mist.size
                    ),
                    radius = mist.size,
                    center = Offset(mist.x, mist.y)
                )
            }

            mistClouds.filter { it.layer == 1 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.85f + sin(cloud.phase + 1f) * 0.15f)
                drawMistCloud(cloud, pulseAlpha, mistLight)
            }

            floatingMist.filter { it.baseAlpha >= 0.05f }.forEach { mist ->
                val pulseAlpha = mist.baseAlpha * (0.75f + sin(mist.phase) * 0.25f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            mistLight.copy(alpha = pulseAlpha),
                            mistLight.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(mist.x, mist.y),
                        radius = mist.size
                    ),
                    radius = mist.size,
                    center = Offset(mist.x, mist.y)
                )
            }

            mistClouds.filter { it.layer == 2 }.forEach { cloud ->
                val pulseAlpha = cloud.baseAlpha * (0.9f + sin(cloud.phase + 2f) * 0.1f)
                drawMistCloud(cloud, pulseAlpha, mistWhite)
            }

            condensationDrops.forEach { drop ->
                val pulseAlpha = drop.alpha * (0.6f + sin(drop.pulsePhase) * 0.4f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            mistWhite.copy(alpha = pulseAlpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(drop.x, drop.y),
                        radius = drop.size * 4f
                    ),
                    radius = drop.size * 4f,
                    center = Offset(drop.x, drop.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = pulseAlpha),
                            mistLight.copy(alpha = pulseAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(drop.x - drop.size * 0.15f, drop.y - drop.size * 0.15f),
                        radius = drop.size
                    ),
                    radius = drop.size,
                    center = Offset(drop.x, drop.y)
                )

                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * 0.8f),
                    radius = drop.size * 0.3f,
                    center = Offset(drop.x - drop.size * 0.2f, drop.y - drop.size * 0.2f)
                )
            }

            val edgeAlpha = (sin(globalPhase * 0.5f) * 0.08f + 0.12f).coerceIn(0f, 0.2f)

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        mistWhite.copy(alpha = edgeAlpha),
                        mistWhite.copy(alpha = edgeAlpha * 0.3f),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = screenWidth * 0.4f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth * 0.4f, screenHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        mistWhite.copy(alpha = edgeAlpha * 0.4f),
                        mistWhite.copy(alpha = edgeAlpha * 0.9f)
                    ),
                    startX = screenWidth * 0.6f,
                    endX = screenWidth
                ),
                topLeft = Offset(screenWidth * 0.6f, 0f),
                size = Size(screenWidth * 0.4f, screenHeight)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        mistWhite.copy(alpha = edgeAlpha * 0.7f),
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
                        mistWhite.copy(alpha = edgeAlpha * 0.8f)
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

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMistCloud(
    cloud: MistCloud,
    alpha: Float,
    color: Color
) {
    val centerX = cloud.x + cloud.width / 2
    val centerY = cloud.y + cloud.height / 2

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = alpha * 0.6f),
                color.copy(alpha = alpha * 0.2f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = cloud.width / 2
        ),
        topLeft = Offset(cloud.x, cloud.y),
        size = Size(cloud.width, cloud.height)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(centerX - cloud.width * 0.2f, centerY - cloud.height * 0.1f),
            radius = cloud.width * 0.3f
        ),
        topLeft = Offset(cloud.x + cloud.width * 0.1f, cloud.y + cloud.height * 0.2f),
        size = Size(cloud.width * 0.5f, cloud.height * 0.6f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.4f),
                Color.Transparent
            ),
            center = Offset(centerX + cloud.width * 0.15f, centerY + cloud.height * 0.05f),
            radius = cloud.width * 0.25f
        ),
        topLeft = Offset(cloud.x + cloud.width * 0.4f, cloud.y + cloud.height * 0.3f),
        size = Size(cloud.width * 0.4f, cloud.height * 0.5f)
    )
}