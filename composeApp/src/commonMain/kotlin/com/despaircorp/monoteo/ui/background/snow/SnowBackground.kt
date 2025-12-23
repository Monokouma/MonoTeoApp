package com.despaircorp.monoteo.ui.background.snow

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

private data class Snowflake(
    val x: Float,
    val y: Float,
    var z: Float,
    val size: Float,
    val speed: Float,
    val drift: Float,
    var driftPhase: Float,
    val rotation: Float,
    var rotationPhase: Float,
    val type: Int
)

private data class SnowPile(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float
)

private data class ScreenSnow(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float,
    var meltProgress: Float
)

private data class FrostPatch(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float,
    val branches: List<FrostBranch>
)

private data class FrostBranch(
    val angle: Float,
    val length: Float,
    val subBranches: Int
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF8A9AAA),
        Color(0xFFA0AEBB),
        Color(0xFFB8C4D0),
        Color(0xFFCCD6E0),
        Color(0xFFDDE6EE)
    )
)

private val snowColorMid = Color(0xFFF8FAFF)
private val snowColorEdge = Color(0xFFE8F0F8)

@Suppress("EffectKeys")
@Composable
fun SnowBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var snowflakes by remember { mutableStateOf(emptyList<Snowflake>()) }
    var screenSnow by remember { mutableStateOf(emptyList<ScreenSnow>()) }
    var snowPiles by remember { mutableStateOf(emptyList<SnowPile>()) }
    var frostPatches by remember { mutableStateOf(emptyList<FrostPatch>()) }
    var windPhase by remember { mutableFloatStateOf(0f) }
    var ambientPulse by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            snowflakes = List(150) { i ->
                val layer = when {
                    i < 40 -> 0
                    i < 100 -> 1
                    else -> 2
                }
                Snowflake(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    z = Random.nextFloat(),
                    size = when (layer) {
                        0 -> Random.nextFloat() * 2f + 1f
                        1 -> Random.nextFloat() * 3f + 2f
                        else -> Random.nextFloat() * 5f + 3f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.008f + 0.004f
                        1 -> Random.nextFloat() * 0.012f + 0.008f
                        else -> Random.nextFloat() * 0.018f + 0.012f
                    },
                    drift = Random.nextFloat() * 40f + 20f,
                    driftPhase = Random.nextFloat() * 6.28f,
                    rotation = Random.nextFloat() * 0.05f + 0.02f,
                    rotationPhase = Random.nextFloat() * 6.28f,
                    type = Random.nextInt(4)
                )
            }

            frostPatches = List(12) {
                FrostPatch(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    alpha = Random.nextFloat() * 0.2f + 0.1f,
                    size = Random.nextFloat() * 100f + 50f,
                    branches = List(6) {
                        FrostBranch(
                            angle = it * 1.047f + Random.nextFloat() * 0.2f,
                            length = Random.nextFloat() * 0.4f + 0.6f,
                            subBranches = Random.nextInt(2, 5)
                        )
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val newScreenSnowList = mutableListOf<ScreenSnow>()
        val newSnowPilesList = mutableListOf<SnowPile>()

        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                newScreenSnowList.clear()
                newSnowPilesList.clear()

                windPhase = (windPhase + 0.012f) % 6.28f
                ambientPulse = (ambientPulse + 0.006f) % 6.28f

                snowflakes = snowflakes.map { flake ->
                    val nextZ = flake.z - flake.speed
                    flake.driftPhase += 0.025f
                    flake.rotationPhase += flake.rotation

                    if (nextZ <= 0f) {
                        if (Random.nextFloat() > 0.4f) {
                            newScreenSnowList.add(
                                ScreenSnow(
                                    x = flake.x,
                                    y = flake.y,
                                    alpha = 0.9f,
                                    size = flake.size * 1.8f,
                                    meltProgress = 0f
                                )
                            )
                        }

                        if (Random.nextFloat() > 0.8f) {
                            newSnowPilesList.add(
                                SnowPile(
                                    x = flake.x,
                                    y = flake.y,
                                    alpha = 0.4f,
                                    size = flake.size * 4f
                                )
                            )
                        }

                        flake.copy(
                            z = 1f,
                            x = Random.nextFloat() * screenWidth,
                            y = Random.nextFloat() * screenHeight,
                            driftPhase = Random.nextFloat() * 6.28f
                        )
                    } else {
                        flake.copy(
                            z = nextZ,
                            driftPhase = flake.driftPhase,
                            rotationPhase = flake.rotationPhase
                        )
                    }
                }

                screenSnow = (screenSnow + newScreenSnowList).mapNotNull { snow ->
                    val nextMelt = snow.meltProgress + 0.0015f
                    val nextAlpha = snow.alpha - 0.0008f
                    if (nextAlpha <= 0f || nextMelt >= 1f) null
                    else snow.copy(alpha = nextAlpha, meltProgress = nextMelt)
                }

                snowPiles = (snowPiles + newSnowPilesList).mapNotNull { pile ->
                    val nextAlpha = pile.alpha - 0.0003f
                    if (nextAlpha <= 0f) null else pile.copy(alpha = nextAlpha)
                }

                frostPatches = frostPatches.map { frost ->
                    val pulseAlpha = frost.alpha * (0.85f + sin(ambientPulse + frost.x * 0.01f) * 0.15f)
                    frost.copy(alpha = pulseAlpha.coerceIn(0.05f, 0.25f))
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val ambientAlpha = (sin(ambientPulse) * 0.04f + 0.06f).coerceIn(0f, 0.1f)
            val windStrength = sin(windPhase) * 0.5f + 0.5f

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = ambientAlpha),
                        Color.Transparent,
                        Color.White.copy(alpha = ambientAlpha * 0.7f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(screenWidth * 0.3f, screenHeight * 0.2f),
                    radius = (screenHeight * 0.8f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            frostPatches.forEach { frost ->
                frost.branches.forEach { branch ->
                    val endX = frost.x + cos(branch.angle) * frost.size * branch.length
                    val endY = frost.y + sin(branch.angle) * frost.size * branch.length

                    drawLine(
                        color = Color.White.copy(alpha = frost.alpha * 0.8f),
                        start = Offset(frost.x, frost.y),
                        end = Offset(endX, endY),
                        strokeWidth = 2f,
                        cap = StrokeCap.Round
                    )

                    for (i in 0 until branch.subBranches) {
                        val t = (i + 1f) / (branch.subBranches + 1f)
                        val branchX = frost.x + (endX - frost.x) * t
                        val branchY = frost.y + (endY - frost.y) * t
                        val subAngle1 = branch.angle + 0.5f
                        val subAngle2 = branch.angle - 0.5f
                        val subLength = frost.size * branch.length * 0.35f * (1f - t * 0.5f)

                        drawLine(
                            color = Color.White.copy(alpha = frost.alpha * 0.6f),
                            start = Offset(branchX, branchY),
                            end = Offset(
                                branchX + cos(subAngle1) * subLength,
                                branchY + sin(subAngle1) * subLength
                            ),
                            strokeWidth = 1.5f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White.copy(alpha = frost.alpha * 0.6f),
                            start = Offset(branchX, branchY),
                            end = Offset(
                                branchX + cos(subAngle2) * subLength,
                                branchY + sin(subAngle2) * subLength
                            ),
                            strokeWidth = 1.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = frost.alpha * 0.5f),
                            Color.Transparent
                        ),
                        center = Offset(frost.x, frost.y),
                        radius = (frost.size * 0.4f).coerceAtLeast(1f)
                    ),
                    radius = (frost.size * 0.4f).coerceAtLeast(1f),
                    center = Offset(frost.x, frost.y)
                )
            }

            snowPiles.forEach { pile ->
                val radius = pile.size.coerceAtLeast(1f)
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = pile.alpha),
                            snowColorMid.copy(alpha = pile.alpha * 0.7f),
                            Color.Transparent
                        ),
                        center = Offset(pile.x, pile.y),
                        radius = radius
                    ),
                    topLeft = Offset(pile.x - radius, pile.y - radius * 0.5f),
                    size = Size(radius * 2f, radius)
                )
            }

            snowflakes.filter { it.type <= 1 }.sortedByDescending { it.z }.forEach { flake ->
                drawSnowflake(flake, windStrength, 0.7f)
            }

            snowflakes.filter { it.type == 2 }.sortedByDescending { it.z }.forEach { flake ->
                drawSnowflake(flake, windStrength, 0.85f)
            }

            snowflakes.filter { it.type == 3 }.sortedByDescending { it.z }.forEach { flake ->
                drawSnowflake(flake, windStrength, 1f)
            }

            screenSnow.forEach { snow ->
                val meltFactor = 1f - snow.meltProgress
                val currentSize = (snow.size * meltFactor).coerceAtLeast(0.1f)

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = snow.alpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(snow.x, snow.y),
                        radius = (currentSize * 4f).coerceAtLeast(1f)
                    ),
                    topLeft = Offset(snow.x - currentSize * 4f, snow.y - currentSize * 4f),
                    size = Size(currentSize * 8f, currentSize * 8f)
                )

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = snow.alpha),
                            snowColorMid.copy(alpha = snow.alpha * 0.85f),
                            snowColorEdge.copy(alpha = snow.alpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(snow.x - currentSize * 0.2f, snow.y - currentSize * 0.2f),
                        radius = (currentSize * 1.2f).coerceAtLeast(1f)
                    ),
                    topLeft = Offset(snow.x - currentSize * 1.1f, snow.y - currentSize * 0.9f),
                    size = Size(currentSize * 2.2f, currentSize * 1.8f)
                )

                drawOval(
                    color = Color.White,
                    topLeft = Offset(snow.x - currentSize * 0.4f, snow.y - currentSize * 0.45f),
                    size = Size(currentSize * 0.5f, currentSize * 0.35f)
                )

                if (snow.meltProgress > 0.4f) {
                    val dripAlpha = (snow.meltProgress - 0.4f) * snow.alpha * 0.8f
                    drawOval(
                        color = Color.White.copy(alpha = dripAlpha),
                        topLeft = Offset(snow.x - currentSize * 0.12f, snow.y + currentSize * 0.6f),
                        size = Size(currentSize * 0.25f, currentSize * 0.6f * snow.meltProgress)
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSnowflake(
    flake: Snowflake,
    windStrength: Float,
    maxAlpha: Float
) {
    val proximity = 1f - flake.z
    val proximityEased = proximity * proximity
    val currentSize = (flake.size * proximityEased * 5f).coerceAtLeast(0.1f)
    val alpha = (proximityEased * maxAlpha).coerceIn(0f, maxAlpha)

    if (currentSize < 1f) return

    val driftOffset = sin(flake.driftPhase) * flake.drift * proximity * windStrength
    val x = flake.x + driftOffset
    val y = flake.y

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha * 0.25f),
                Color.Transparent
            ),
            center = Offset(x, y),
            radius = (currentSize * 4f).coerceAtLeast(1f)
        ),
        radius = (currentSize * 4f).coerceAtLeast(1f),
        center = Offset(x, y)
    )

    when (flake.type) {
        0 -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha),
                        snowColorMid.copy(alpha = alpha * 0.8f),
                        Color.Transparent
                    ),
                    center = Offset(x, y),
                    radius = currentSize.coerceAtLeast(1f)
                ),
                radius = currentSize.coerceAtLeast(1f),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = (currentSize * 0.5f).coerceAtLeast(0.5f),
                center = Offset(x - currentSize * 0.15f, y - currentSize * 0.15f)
            )
        }

        1 -> {
            for (i in 0 until 6) {
                val angle = flake.rotationPhase + i * 1.047f
                val endX = x + cos(angle) * currentSize
                val endY = y + sin(angle) * currentSize

                drawLine(
                    color = Color.White.copy(alpha = alpha),
                    start = Offset(x, y),
                    end = Offset(endX, endY),
                    strokeWidth = (currentSize * 0.18f).coerceAtLeast(0.5f),
                    cap = StrokeCap.Round
                )

                val midX = x + cos(angle) * currentSize * 0.6f
                val midY = y + sin(angle) * currentSize * 0.6f
                val branchAngle1 = angle + 0.5f
                val branchAngle2 = angle - 0.5f
                val branchLength = currentSize * 0.45f

                drawLine(
                    color = Color.White.copy(alpha = alpha * 0.9f),
                    start = Offset(midX, midY),
                    end = Offset(
                        midX + cos(branchAngle1) * branchLength,
                        midY + sin(branchAngle1) * branchLength
                    ),
                    strokeWidth = (currentSize * 0.12f).coerceAtLeast(0.5f),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White.copy(alpha = alpha * 0.9f),
                    start = Offset(midX, midY),
                    end = Offset(
                        midX + cos(branchAngle2) * branchLength,
                        midY + sin(branchAngle2) * branchLength
                    ),
                    strokeWidth = (currentSize * 0.12f).coerceAtLeast(0.5f),
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.7f),
                radius = (currentSize * 0.25f).coerceAtLeast(0.5f),
                center = Offset(x, y)
            )
        }

        2 -> {
            for (i in 0 until 8) {
                val angle = flake.rotationPhase + i * 0.785f
                val length = if (i % 2 == 0) currentSize else currentSize * 0.65f

                drawLine(
                    color = Color.White.copy(alpha = alpha),
                    start = Offset(x, y),
                    end = Offset(
                        x + cos(angle) * length,
                        y + sin(angle) * length
                    ),
                    strokeWidth = (currentSize * 0.15f).coerceAtLeast(0.5f),
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.8f),
                radius = (currentSize * 0.3f).coerceAtLeast(0.5f),
                center = Offset(x, y)
            )
        }

        3 -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = alpha),
                        snowColorMid.copy(alpha = alpha * 0.85f),
                        snowColorEdge.copy(alpha = alpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(x - currentSize * 0.12f, y - currentSize * 0.12f),
                    radius = (currentSize * 1.1f).coerceAtLeast(1f)
                ),
                radius = (currentSize * 1.1f).coerceAtLeast(1f),
                center = Offset(x, y)
            )

            drawOval(
                color = Color.White,
                topLeft = Offset(x - currentSize * 0.55f, y - currentSize * 0.6f),
                size = Size((currentSize * 0.55f).coerceAtLeast(0.5f), (currentSize * 0.4f).coerceAtLeast(0.5f))
            )

            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.6f),
                radius = (currentSize * 0.18f).coerceAtLeast(0.5f),
                center = Offset(x + currentSize * 0.35f, y + currentSize * 0.3f)
            )
        }
    }
}