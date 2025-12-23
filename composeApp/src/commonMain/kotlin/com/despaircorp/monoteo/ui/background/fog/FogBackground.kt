package com.despaircorp.monoteo.ui.background.fog

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

private data class FogBank(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float
)

private data class FogWisp(
    var x: Float,
    var y: Float,
    val size: Float,
    val baseAlpha: Float,
    val speedX: Float,
    val speedY: Float,
    var phase: Float
)

private data class MoistureDroplet(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float,
    var pulsePhase: Float
)

private data class FogTendril(
    var x: Float,
    val y: Float,
    val length: Float,
    val thickness: Float,
    var alpha: Float,
    val speed: Float,
    var wavePhase: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF6A7580),
        Color(0xFF7A8590),
        Color(0xFF8A95A0),
        Color(0xFF9AA5B0),
        Color(0xFFAAB5C0)
    )
)

private val fogColorDark = Color(0xFFAAB5C0)
private val fogColorMid = Color(0xFFC0CAD5)
private val fogColorLight = Color(0xFFD8E0E8)
private val fogColorWhite = Color(0xFFECF0F5)
private val dropletColor = Color(0xFFE0E8F0)

@Suppress("EffectKeys")
@Composable
fun FogBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var fogBanks by remember { mutableStateOf(emptyList<FogBank>()) }
    var fogWisps by remember { mutableStateOf(emptyList<FogWisp>()) }
    var moistureDroplets by remember { mutableStateOf(emptyList<MoistureDroplet>()) }
    var fogTendrils by remember { mutableStateOf(emptyList<FogTendril>()) }
    var globalPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            fogBanks = List(15) { i ->
                val layer = i % 4
                FogBank(
                    x = Random.nextFloat() * screenWidth * 2.5f - screenWidth * 0.75f,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.4f
                        1 -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.15f
                        2 -> Random.nextFloat() * screenHeight * 0.5f + screenHeight * 0.3f
                        else -> Random.nextFloat() * screenHeight * 0.4f + screenHeight * 0.5f
                    },
                    width = Random.nextFloat() * screenWidth * 1.5f + screenWidth * 0.8f,
                    height = Random.nextFloat() * screenHeight * 0.3f + screenHeight * 0.12f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.08f + 0.04f
                        1 -> Random.nextFloat() * 0.12f + 0.06f
                        2 -> Random.nextFloat() * 0.16f + 0.08f
                        else -> Random.nextFloat() * 0.2f + 0.1f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.08f + 0.02f
                        1 -> Random.nextFloat() * 0.12f + 0.04f
                        2 -> Random.nextFloat() * 0.18f + 0.06f
                        else -> Random.nextFloat() * 0.25f + 0.1f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            fogWisps = List(50) {
                FogWisp(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    size = Random.nextFloat() * 120f + 60f,
                    baseAlpha = Random.nextFloat() * 0.1f + 0.03f,
                    speedX = Random.nextFloat() * 0.15f + 0.03f,
                    speedY = Random.nextFloat() * 0.06f - 0.03f,
                    phase = Random.nextFloat() * 6.28f
                )
            }

            moistureDroplets = List(45) {
                MoistureDroplet(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    alpha = Random.nextFloat() * 0.5f + 0.2f,
                    size = Random.nextFloat() * 4f + 2f,
                    pulsePhase = Random.nextFloat() * 6.28f
                )
            }

            fogTendrils = List(12) {
                FogTendril(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = Random.nextFloat() * screenHeight,
                    length = Random.nextFloat() * 300f + 150f,
                    thickness = Random.nextFloat() * 40f + 20f,
                    alpha = Random.nextFloat() * 0.15f + 0.05f,
                    speed = Random.nextFloat() * 0.2f + 0.05f,
                    wavePhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.004f) % 6.28f

                fogBanks = fogBanks.map { bank ->
                    bank.phase += 0.005f
                    var newX = bank.x + bank.speed
                    if (newX > screenWidth + bank.width * 0.5f) {
                        newX = -bank.width
                    }
                    bank.copy(x = newX, phase = bank.phase)
                }

                fogWisps = fogWisps.map { wisp ->
                    wisp.phase += 0.012f
                    var newX = wisp.x + wisp.speedX
                    var newY = wisp.y + wisp.speedY + sin(wisp.phase) * 0.1f

                    if (newX > screenWidth + wisp.size) newX = -wisp.size
                    if (newY < -wisp.size) newY = screenHeight + wisp.size
                    if (newY > screenHeight + wisp.size) newY = -wisp.size

                    wisp.copy(x = newX, y = newY, phase = wisp.phase)
                }

                moistureDroplets = moistureDroplets.map { droplet ->
                    droplet.pulsePhase += 0.025f
                    droplet.copy(pulsePhase = droplet.pulsePhase)
                }

                fogTendrils = fogTendrils.map { tendril ->
                    tendril.wavePhase += 0.015f
                    var newX = tendril.x + tendril.speed
                    if (newX > screenWidth + tendril.length) {
                        newX = -tendril.length
                    }
                    tendril.copy(x = newX, wavePhase = tendril.wavePhase)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val breatheAlpha = (sin(globalPhase) * 0.06f + 0.12f).coerceIn(0f, 0.18f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        fogColorLight.copy(alpha = breatheAlpha),
                        fogColorMid.copy(alpha = breatheAlpha * 0.4f),
                        Color.Transparent,
                        fogColorMid.copy(alpha = breatheAlpha * 0.5f),
                        fogColorLight.copy(alpha = breatheAlpha * 0.8f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            fogBanks.filter { it.layer == 0 }.forEach { bank ->
                val pulseAlpha = bank.baseAlpha * (0.8f + sin(bank.phase) * 0.2f)
                drawFogBank(bank, pulseAlpha, fogColorDark)
            }

            fogWisps.filter { it.baseAlpha < 0.06f }.forEach { wisp ->
                val pulseAlpha = wisp.baseAlpha * (0.7f + sin(wisp.phase) * 0.3f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            fogColorDark.copy(alpha = pulseAlpha),
                            fogColorDark.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(wisp.x, wisp.y),
                        radius = wisp.size.coerceAtLeast(1f)
                    ),
                    radius = wisp.size.coerceAtLeast(1f),
                    center = Offset(wisp.x, wisp.y)
                )
            }

            fogTendrils.filter { it.alpha < 0.1f }.forEach { tendril ->
                drawFogTendril(tendril, fogColorDark)
            }

            fogBanks.filter { it.layer == 1 }.forEach { bank ->
                val pulseAlpha = bank.baseAlpha * (0.85f + sin(bank.phase + 0.8f) * 0.15f)
                drawFogBank(bank, pulseAlpha, fogColorMid)
            }

            fogWisps.filter { it.baseAlpha >= 0.06f && it.baseAlpha < 0.1f }.forEach { wisp ->
                val pulseAlpha = wisp.baseAlpha * (0.75f + sin(wisp.phase) * 0.25f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            fogColorMid.copy(alpha = pulseAlpha),
                            fogColorMid.copy(alpha = pulseAlpha * 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(wisp.x, wisp.y),
                        radius = wisp.size.coerceAtLeast(1f)
                    ),
                    radius = wisp.size.coerceAtLeast(1f),
                    center = Offset(wisp.x, wisp.y)
                )
            }

            fogBanks.filter { it.layer == 2 }.forEach { bank ->
                val pulseAlpha = bank.baseAlpha * (0.9f + sin(bank.phase + 1.6f) * 0.1f)
                drawFogBank(bank, pulseAlpha, fogColorLight)
            }

            fogTendrils.filter { it.alpha >= 0.1f }.forEach { tendril ->
                drawFogTendril(tendril, fogColorLight)
            }

            fogWisps.filter { it.baseAlpha >= 0.1f }.forEach { wisp ->
                val pulseAlpha = wisp.baseAlpha * (0.8f + sin(wisp.phase) * 0.2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            fogColorLight.copy(alpha = pulseAlpha),
                            fogColorLight.copy(alpha = pulseAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(wisp.x, wisp.y),
                        radius = wisp.size.coerceAtLeast(1f)
                    ),
                    radius = wisp.size.coerceAtLeast(1f),
                    center = Offset(wisp.x, wisp.y)
                )
            }

            fogBanks.filter { it.layer == 3 }.forEach { bank ->
                val pulseAlpha = bank.baseAlpha * (0.92f + sin(bank.phase + 2.4f) * 0.08f)
                drawFogBank(bank, pulseAlpha, fogColorWhite)
            }

            moistureDroplets.forEach { droplet ->
                val pulseAlpha = droplet.alpha * (0.5f + sin(droplet.pulsePhase) * 0.5f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            fogColorWhite.copy(alpha = pulseAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(droplet.x, droplet.y),
                        radius = (droplet.size * 4f).coerceAtLeast(1f)
                    ),
                    radius = (droplet.size * 4f).coerceAtLeast(1f),
                    center = Offset(droplet.x, droplet.y)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = pulseAlpha),
                            dropletColor.copy(alpha = pulseAlpha * 0.6f),
                            Color.Transparent
                        ),
                        center = Offset(droplet.x - droplet.size * 0.15f, droplet.y - droplet.size * 0.15f),
                        radius = droplet.size.coerceAtLeast(0.5f)
                    ),
                    radius = droplet.size.coerceAtLeast(0.5f),
                    center = Offset(droplet.x, droplet.y)
                )

                drawCircle(
                    color = Color.White.copy(alpha = pulseAlpha * 0.7f),
                    radius = (droplet.size * 0.3f).coerceAtLeast(0.3f),
                    center = Offset(droplet.x - droplet.size * 0.2f, droplet.y - droplet.size * 0.2f)
                )
            }

            val edgeFog = (sin(globalPhase * 0.6f) * 0.08f + 0.14f).coerceIn(0f, 0.22f)

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        fogColorWhite.copy(alpha = edgeFog),
                        fogColorLight.copy(alpha = edgeFog * 0.4f),
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
                        fogColorLight.copy(alpha = edgeFog * 0.5f),
                        fogColorWhite.copy(alpha = edgeFog * 0.9f)
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
                        fogColorWhite.copy(alpha = edgeFog * 0.7f),
                        fogColorLight.copy(alpha = edgeFog * 0.3f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = screenHeight * 0.35f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight * 0.35f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        fogColorLight.copy(alpha = edgeFog * 0.4f),
                        fogColorWhite.copy(alpha = edgeFog * 0.85f)
                    ),
                    startY = screenHeight * 0.65f,
                    endY = screenHeight
                ),
                topLeft = Offset(0f, screenHeight * 0.65f),
                size = Size(screenWidth, screenHeight * 0.35f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFogBank(
    bank: FogBank,
    alpha: Float,
    color: Color
) {
    val centerX = bank.x + bank.width / 2
    val centerY = bank.y + bank.height / 2
    val radius = (bank.width / 2).coerceAtLeast(1f)

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = alpha * 0.6f),
                color.copy(alpha = alpha * 0.25f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = radius
        ),
        topLeft = Offset(bank.x, bank.y),
        size = Size(bank.width, bank.height)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(centerX - bank.width * 0.18f, centerY - bank.height * 0.12f),
            radius = (bank.width * 0.35f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            bank.x + bank.width * 0.08f,
            bank.y + bank.height * 0.12f
        ),
        size = Size(bank.width * 0.5f, bank.height * 0.65f)
    )

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.4f),
                Color.Transparent
            ),
            center = Offset(centerX + bank.width * 0.12f, centerY + bank.height * 0.08f),
            radius = (bank.width * 0.28f).coerceAtLeast(1f)
        ),
        topLeft = Offset(
            bank.x + bank.width * 0.45f,
            bank.y + bank.height * 0.25f
        ),
        size = Size(bank.width * 0.4f, bank.height * 0.55f)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFogTendril(
    tendril: FogTendril,
    color: Color
) {
    val waveOffset = sin(tendril.wavePhase) * 20f
    val pulseAlpha = tendril.alpha * (0.7f + sin(tendril.wavePhase * 0.5f) * 0.3f)

    val path = Path().apply {
        moveTo(tendril.x, tendril.y + waveOffset)

        val segments = 8
        val segmentLength = tendril.length / segments
        for (i in 1..segments) {
            val segX = tendril.x + segmentLength * i
            val segWave = sin(tendril.wavePhase + i * 0.5f) * 15f
            quadraticTo(
                tendril.x + segmentLength * (i - 0.5f),
                tendril.y + waveOffset + sin(tendril.wavePhase + (i - 0.5f) * 0.5f) * 20f,
                segX,
                tendril.y + segWave
            )
        }
    }

    drawPath(
        path = path,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                color.copy(alpha = pulseAlpha * 0.5f),
                color.copy(alpha = pulseAlpha),
                color.copy(alpha = pulseAlpha * 0.5f),
                Color.Transparent
            ),
            startX = tendril.x,
            endX = tendril.x + tendril.length
        ),
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = tendril.thickness,
            cap = StrokeCap.Round
        )
    )
}