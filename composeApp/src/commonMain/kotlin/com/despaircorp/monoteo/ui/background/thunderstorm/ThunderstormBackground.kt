package com.despaircorp.monoteo.ui.background.thunderstorm

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
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class RainDrop(
    val x: Float,
    val y: Float,
    var z: Float,
    val size: Float,
    val speed: Float,
    val angle: Float,
    val layer: Int
)

private data class Impact(
    val x: Float,
    val y: Float,
    var progress: Float,
    val intensity: Float,
    val particles: List<Particle>,
    val secondaryRipples: List<SecondaryRipple>
)

private data class Particle(
    val angle: Float,
    val velocity: Float,
    val size: Float,
    val drag: Float,
    val gravity: Float
)

private data class SecondaryRipple(
    val offsetX: Float,
    val offsetY: Float,
    val delay: Float,
    val scale: Float
)

private data class ScreenDrop(
    val x: Float,
    var y: Float,
    var alpha: Float,
    val size: Float,
    val slideSpeed: Float,
    val wobble: Float,
    var wobblePhase: Float,
    val distortion: Float
)

private data class WaterTrail(
    val x: Float,
    val startY: Float,
    var endY: Float,
    var alpha: Float,
    val width: Float,
    val segments: List<Float>
)

private data class ScreenWater(
    val x: Float,
    val y: Float,
    var alpha: Float,
    val size: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF000000),
        Color(0xFF010102),
        Color(0xFF020204),
        Color(0xFF030308),
        Color(0xFF04040C)
    )
)

private val dropColorCore = Color(0xFFE8F4FF)
private val dropColorMid = Color(0xFFB0D0F0)
private val dropColorEdge = Color(0xFF6090C0)
private val impactColor = Color(0xFFC0E0FF)
private val screenDropColor = Color(0xFFD8EEFF)
private val trailColor = Color(0xFF5888B8)
private val electricBlue = Color(0xFF88CCFF)
private val electricPurple = Color(0xFFAA88FF)

@Composable
fun ThunderstormBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var drops by remember { mutableStateOf(emptyList<RainDrop>()) }
    var impacts by remember { mutableStateOf(emptyList<Impact>()) }
    var screenDrops by remember { mutableStateOf(emptyList<ScreenDrop>()) }
    var trails by remember { mutableStateOf(emptyList<WaterTrail>()) }
    var screenWater by remember { mutableStateOf(emptyList<ScreenWater>()) }
    var lightningAlpha by remember { mutableFloatStateOf(0f) }
    var lightningCenter by remember { mutableStateOf(Offset.Zero) }
    var secondaryFlash by remember { mutableFloatStateOf(0f) }
    var ambientPulse by remember { mutableFloatStateOf(0f) }
    var stormIntensity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            drops = List(150) { i ->
                val layer = when {
                    i < 40 -> 0
                    i < 100 -> 1
                    else -> 2
                }
                RainDrop(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    z = Random.nextFloat(),
                    size = when (layer) {
                        0 -> Random.nextFloat() * 1.5f + 0.5f
                        1 -> Random.nextFloat() * 2f + 1f
                        else -> Random.nextFloat() * 3f + 1.5f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 0.02f + 0.015f
                        1 -> Random.nextFloat() * 0.03f + 0.025f
                        else -> Random.nextFloat() * 0.04f + 0.035f
                    },
                    angle = Random.nextFloat() * 0.35f - 0.175f,
                    layer = layer
                )
            }
            screenWater = List(15) {
                ScreenWater(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    alpha = Random.nextFloat() * 0.15f + 0.05f,
                    size = Random.nextFloat() * 60f + 30f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val newImpactsList = mutableListOf<Impact>()
        val newScreenDropsList = mutableListOf<ScreenDrop>()
        val newTrailsList = mutableListOf<WaterTrail>()
        val newScreenWaterList = mutableListOf<ScreenWater>()

        while (true) {
            withFrameMillis {
                newImpactsList.clear()
                newScreenDropsList.clear()
                newTrailsList.clear()
                newScreenWaterList.clear()

                ambientPulse = (ambientPulse + 0.01f) % 6.28f
                stormIntensity = (sin(ambientPulse * 0.3f) * 0.3f + 0.7f).coerceIn(0.5f, 1f)

                drops = drops.map { drop ->
                    val nextZ = drop.z - drop.speed * stormIntensity
                    if (nextZ <= 0f) {
                        val impactIntensity = drop.size / 2.5f
                        newImpactsList.add(
                            Impact(
                                x = drop.x,
                                y = drop.y,
                                progress = 0f,
                                intensity = impactIntensity,
                                particles = List(12) {
                                    Particle(
                                        angle = Random.nextFloat() * 6.28f,
                                        velocity = Random.nextFloat() * 15f + 5f,
                                        size = Random.nextFloat() * 3f + 1f,
                                        drag = Random.nextFloat() * 0.2f + 0.8f,
                                        gravity = Random.nextFloat() * 0.5f + 0.3f
                                    )
                                },
                                secondaryRipples = List(3) {
                                    SecondaryRipple(
                                        offsetX = Random.nextFloat() * 20f - 10f,
                                        offsetY = Random.nextFloat() * 20f - 10f,
                                        delay = Random.nextFloat() * 0.2f,
                                        scale = Random.nextFloat() * 0.4f + 0.3f
                                    )
                                }
                            )
                        )

                        if (Random.nextFloat() > 0.6f) {
                            newScreenDropsList.add(
                                ScreenDrop(
                                    x = drop.x,
                                    y = drop.y,
                                    alpha = 0.75f,
                                    size = Random.nextFloat() * 5f + 3f,
                                    slideSpeed = Random.nextFloat() * 2.5f + 1f,
                                    wobble = Random.nextFloat() * 3f + 1f,
                                    wobblePhase = Random.nextFloat() * 6.28f,
                                    distortion = Random.nextFloat() * 0.3f + 0.1f
                                )
                            )
                        }

                        if (Random.nextFloat() > 0.9f) {
                            newScreenWaterList.add(
                                ScreenWater(
                                    x = drop.x,
                                    y = drop.y,
                                    alpha = 0.2f,
                                    size = Random.nextFloat() * 40f + 20f
                                )
                            )
                        }

                        drop.copy(
                            z = 1f,
                            x = Random.nextFloat() * screenWidth,
                            y = Random.nextFloat() * screenHeight,
                            speed = when (drop.layer) {
                                0 -> Random.nextFloat() * 0.02f + 0.015f
                                1 -> Random.nextFloat() * 0.03f + 0.025f
                                else -> Random.nextFloat() * 0.04f + 0.035f
                            },
                            angle = Random.nextFloat() * 0.35f - 0.175f
                        )
                    } else {
                        drop.copy(z = nextZ)
                    }
                }

                impacts = (impacts + newImpactsList).mapNotNull { impact ->
                    val next = impact.progress + 0.045f
                    if (next >= 1f) null else impact.copy(progress = next)
                }

                screenDrops = (screenDrops + newScreenDropsList).mapNotNull { sd ->
                    val nextY = sd.y + sd.slideSpeed
                    val nextAlpha = sd.alpha - 0.0015f
                    sd.wobblePhase += 0.18f

                    if (nextAlpha <= 0f || nextY > screenHeight + 20f) {
                        if (nextY > screenHeight * 0.25f && Random.nextFloat() > 0.4f) {
                            newTrailsList.add(
                                WaterTrail(
                                    x = sd.x,
                                    startY = sd.y - 40f,
                                    endY = sd.y,
                                    alpha = 0.35f,
                                    width = sd.size * 0.5f,
                                    segments = List(5) { Random.nextFloat() * 4f - 2f }
                                )
                            )
                        }
                        null
                    } else {
                        sd.copy(y = nextY, alpha = nextAlpha, wobblePhase = sd.wobblePhase)
                    }
                }

                trails = (trails + newTrailsList).mapNotNull { trail ->
                    val nextAlpha = trail.alpha - 0.006f
                    if (nextAlpha <= 0f) null else trail.copy(alpha = nextAlpha)
                }

                screenWater = (screenWater + newScreenWaterList).mapNotNull { sw ->
                    val nextAlpha = sw.alpha - 0.001f
                    if (nextAlpha <= 0f) null else sw.copy(alpha = nextAlpha)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(3000, 8000))

            lightningCenter = Offset(
                Random.nextFloat() * screenWidth,
                Random.nextFloat() * screenHeight * 0.25f
            )

            lightningAlpha = 1f
            delay(20)
            lightningAlpha = 0.15f
            delay(45)
            lightningAlpha = 0.95f
            secondaryFlash = 0.5f
            delay(30)
            lightningAlpha = 0.2f
            secondaryFlash = 0.8f
            delay(55)
            lightningAlpha = 0.85f
            secondaryFlash = 0.3f
            delay(25)
            lightningAlpha = 0.1f
            secondaryFlash = 0f
            delay(70)

            if (Random.nextFloat() > 0.25f) {
                delay(Random.nextLong(100, 350))
                lightningAlpha = 0.75f
                secondaryFlash = 0.4f
                delay(25)
                lightningAlpha = 0.25f
                delay(50)
                lightningAlpha = 0.6f
                delay(35)
                lightningAlpha = 0.15f
                secondaryFlash = 0f
                delay(60)

                if (Random.nextFloat() > 0.5f) {
                    delay(Random.nextLong(80, 200))
                    lightningAlpha = 0.4f
                    delay(30)
                    lightningAlpha = 0.1f
                    delay(40)
                }
            }

            lightningAlpha = 0f
            secondaryFlash = 0f
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        if (lightningAlpha > 0f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                electricBlue.copy(alpha = lightningAlpha * 0.6f),
                                electricPurple.copy(alpha = lightningAlpha * 0.3f),
                                Color(0xFF4466AA).copy(alpha = lightningAlpha * 0.15f),
                                Color(0xFF223355).copy(alpha = lightningAlpha * 0.05f),
                                Color.Transparent
                            ),
                            center = lightningCenter,
                            radius = screenHeight * 1.8f
                        )
                    )
            )

            if (secondaryFlash > 0f) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = secondaryFlash * 0.25f),
                                    electricBlue.copy(alpha = secondaryFlash * 0.1f),
                                    Color.Transparent
                                ),
                                center = Offset(
                                    lightningCenter.x + screenWidth * 0.3f,
                                    lightningCenter.y + screenHeight * 0.1f
                                ),
                                radius = screenHeight * 0.8f
                            )
                        )
                )
            }
        }

        val ambientAlpha = (sin(ambientPulse) * 0.02f + 0.02f).coerceIn(0f, 0.04f)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1133AA).copy(alpha = ambientAlpha * stormIntensity),
                            Color.Transparent,
                            Color(0xFF112266).copy(alpha = ambientAlpha * 0.5f * stormIntensity)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            screenWater.forEach { sw ->
                val pulseAlpha = sw.alpha * (0.7f + sin(ambientPulse + sw.x * 0.01f) * 0.3f)
                val glowBoost = if (lightningAlpha > 0.3f) lightningAlpha * 0.3f else 0f
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            screenDropColor.copy(alpha = (pulseAlpha + glowBoost) * 0.4f),
                            screenDropColor.copy(alpha = (pulseAlpha + glowBoost) * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(sw.x, sw.y),
                        radius = sw.size
                    ),
                    topLeft = Offset(sw.x - sw.size, sw.y - sw.size),
                    size = Size(sw.size * 2f, sw.size * 2f)
                )
            }

            trails.forEach { trail ->
                val path = Path().apply {
                    moveTo(trail.x, trail.startY)
                    val segmentHeight = (trail.endY - trail.startY) / trail.segments.size
                    trail.segments.forEachIndexed { index, offset ->
                        val y = trail.startY + segmentHeight * (index + 1)
                        lineTo(trail.x + offset, y)
                    }
                }
                drawPath(
                    path = path,
                    color = trailColor.copy(alpha = trail.alpha * 0.6f),
                    style = Stroke(width = trail.width, cap = StrokeCap.Round)
                )
            }

            drops.filter { it.layer == 0 }.sortedByDescending { it.z }.forEach { drop ->
                drawRainDrop(drop, 0.4f, lightningAlpha)
            }

            drops.filter { it.layer == 1 }.sortedByDescending { it.z }.forEach { drop ->
                drawRainDrop(drop, 0.55f, lightningAlpha)
            }

            impacts.forEach { impact ->
                val inv = 1f - impact.progress
                val invEased = inv * inv
                val baseRadius = impact.progress * 80f * impact.intensity
                val glowBoost = if (lightningAlpha > 0.3f) lightningAlpha * 0.2f else 0f

                drawCircle(
                    color = impactColor.copy(alpha = (invEased * 0.4f + glowBoost)),
                    radius = baseRadius,
                    center = Offset(impact.x, impact.y),
                    style = Stroke(width = 3f * invEased + 0.5f)
                )

                drawCircle(
                    color = impactColor.copy(alpha = invEased * 0.2f),
                    radius = baseRadius * 0.6f,
                    center = Offset(impact.x, impact.y),
                    style = Stroke(width = 2f * invEased)
                )

                drawCircle(
                    color = impactColor.copy(alpha = invEased * 0.1f),
                    radius = baseRadius * 1.4f,
                    center = Offset(impact.x, impact.y),
                    style = Stroke(width = 1f * invEased)
                )

                impact.secondaryRipples.forEach { ripple ->
                    val rippleProgress = (impact.progress - ripple.delay).coerceIn(0f, 1f)
                    if (rippleProgress > 0f) {
                        val rippleInv = 1f - rippleProgress
                        drawCircle(
                            color = impactColor.copy(alpha = rippleInv * rippleInv * 0.25f),
                            radius = rippleProgress * 50f * ripple.scale * impact.intensity,
                            center = Offset(impact.x + ripple.offsetX, impact.y + ripple.offsetY),
                            style = Stroke(width = 1.5f * rippleInv)
                        )
                    }
                }

                impact.particles.forEach { p ->
                    val particleProgress = impact.progress * p.drag
                    val dist = particleProgress * p.velocity * 9f
                    val gravityOffset = particleProgress * particleProgress * p.gravity * 50f
                    val particleAlpha = invEased * 0.55f
                    val particleSize = p.size * invEased

                    val px = impact.x + cos(p.angle) * dist
                    val py = impact.y + sin(p.angle) * dist * 0.5f + gravityOffset

                    if (particleSize > 0.3f) {
                        drawCircle(
                            color = Color.White.copy(alpha = particleAlpha),
                            radius = particleSize,
                            center = Offset(px, py)
                        )
                        drawCircle(
                            color = dropColorCore.copy(alpha = particleAlpha * 0.35f),
                            radius = particleSize * 2f,
                            center = Offset(px, py)
                        )
                    }
                }

                if (impact.progress < 0.12f) {
                    val flashAlpha = (0.12f - impact.progress) * 5f
                    drawCircle(
                        color = Color.White.copy(alpha = flashAlpha * 0.3f * impact.intensity),
                        radius = 15f * impact.intensity,
                        center = Offset(impact.x, impact.y)
                    )
                }
            }

            drops.filter { it.layer == 2 }.sortedByDescending { it.z }.forEach { drop ->
                drawRainDrop(drop, 0.7f, lightningAlpha)
            }

            screenDrops.forEach { sd ->
                val wobbleX = sin(sd.wobblePhase) * sd.wobble
                val wobbleX2 = sin(sd.wobblePhase * 1.3f) * sd.wobble * 0.5f
                val dx = sd.x + wobbleX + wobbleX2
                val glowBoost = if (lightningAlpha > 0.3f) lightningAlpha * 0.5f else 0f

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            screenDropColor.copy(alpha = (sd.alpha + glowBoost) * 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(dx, sd.y),
                        radius = sd.size * 4f
                    ),
                    topLeft = Offset(dx - sd.size * 4f, sd.y - sd.size * 4f),
                    size = Size(sd.size * 8f, sd.size * 8f)
                )

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = (sd.alpha + glowBoost) * 0.9f),
                            screenDropColor.copy(alpha = (sd.alpha + glowBoost) * 0.55f),
                            dropColorMid.copy(alpha = (sd.alpha + glowBoost) * 0.3f),
                            dropColorEdge.copy(alpha = (sd.alpha + glowBoost) * 0.1f),
                            Color.Transparent
                        ),
                        center = Offset(dx - sd.size * 0.25f, sd.y - sd.size * 0.25f),
                        radius = sd.size * 1.2f
                    ),
                    topLeft = Offset(dx - sd.size * 1.1f, sd.y - sd.size * 0.9f),
                    size = Size(sd.size * 2.2f, sd.size * 1.8f)
                )

                drawOval(
                    color = Color.White.copy(alpha = (sd.alpha + glowBoost) * 0.7f),
                    topLeft = Offset(dx - sd.size * 0.4f, sd.y - sd.size * 0.45f),
                    size = Size(sd.size * 0.45f, sd.size * 0.35f)
                )

                drawOval(
                    color = Color.White.copy(alpha = (sd.alpha + glowBoost) * 0.3f),
                    topLeft = Offset(dx + sd.size * 0.2f, sd.y + sd.size * 0.1f),
                    size = Size(sd.size * 0.2f, sd.size * 0.15f)
                )
            }
        }

        if (lightningAlpha > 0.85f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = (lightningAlpha - 0.85f) * 0.25f))
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRainDrop(
    drop: RainDrop,
    maxAlpha: Float,
    lightningAlpha: Float
) {
    val proximity = 1f - drop.z
    val proximityEased = proximity * proximity * proximity
    val currentLength = drop.size * proximityEased * 28f
    val currentWidth = drop.size * proximityEased * 3.5f
    val alpha = (proximityEased * maxAlpha).coerceIn(0f, maxAlpha)
    val glowBoost = if (lightningAlpha > 0.3f) lightningAlpha * 0.15f else 0f

    if (currentLength > 2f) {
        val startX = drop.x
        val startY = drop.y - currentLength / 2
        val endX = drop.x + drop.angle * currentLength
        val endY = drop.y + currentLength / 2

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    dropColorEdge.copy(alpha = (alpha + glowBoost) * 0.35f),
                    dropColorMid.copy(alpha = (alpha + glowBoost) * 0.7f),
                    dropColorCore.copy(alpha = alpha + glowBoost),
                    dropColorCore.copy(alpha = (alpha + glowBoost) * 0.85f)
                ),
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            ),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = currentWidth,
            cap = StrokeCap.Round
        )

        if (proximity > 0.7f) {
            drawLine(
                color = Color.White.copy(alpha = (alpha + glowBoost) * 0.4f * (proximity - 0.7f) / 0.3f),
                start = Offset(startX, startY + currentLength * 0.6f),
                end = Offset(endX, endY),
                strokeWidth = currentWidth * 0.4f,
                cap = StrokeCap.Round
            )
        }
    }
}