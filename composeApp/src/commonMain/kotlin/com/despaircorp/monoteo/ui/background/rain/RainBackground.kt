package com.despaircorp.monoteo.ui.background.rain

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
import kotlin.math.sin
import kotlin.random.Random

private data class RainDrop(
    val x: Float,
    val y: Float,
    var z: Float,
    val size: Float,
    val speed: Float,
    val angle: Float
)

private data class Impact(
    val x: Float,
    val y: Float,
    var progress: Float,
    val intensity: Float,
    val particles: List<Particle>
)

private data class Particle(
    val angle: Float,
    val velocity: Float,
    val size: Float
)

private data class ScreenDrop(
    val x: Float,
    var y: Float,
    var alpha: Float,
    val size: Float,
    val slideSpeed: Float,
    var wobblePhase: Float
)

private data class WaterTrail(
    val x: Float,
    val startY: Float,
    var endY: Float,
    var alpha: Float,
    val width: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A1E28),
        Color(0xFF252A38),
        Color(0xFF303848),
        Color(0xFF3A4558)
    )
)

private val dropColorCore = Color(0xFFD0E0F0)
private val dropColorEdge = Color(0xFF7090B0)
private val impactColor = Color(0xFFA0C0D8)
private val screenDropColor = Color(0xFFB8D0E8)
private val trailColor = Color(0xFF5080A0)

@Suppress("EffectKeys")
@Composable
fun RainBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var drops by remember { mutableStateOf(emptyList<RainDrop>()) }
    var impacts by remember { mutableStateOf(emptyList<Impact>()) }
    var screenDrops by remember { mutableStateOf(emptyList<ScreenDrop>()) }
    var trails by remember { mutableStateOf(emptyList<WaterTrail>()) }
    var ambientPulse by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            drops = List(120) {
                RainDrop(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight,
                    z = Random.nextFloat(),
                    size = Random.nextFloat() * 2f + 1f,
                    speed = Random.nextFloat() * 0.028f + 0.02f,
                    angle = Random.nextFloat() * 0.2f - 0.1f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val newImpactsList = mutableListOf<Impact>()
        val newScreenDropsList = mutableListOf<ScreenDrop>()
        val newTrailsList = mutableListOf<WaterTrail>()

        while (true) {
            withFrameMillis {
                newImpactsList.clear()
                newScreenDropsList.clear()
                newTrailsList.clear()

                ambientPulse = (ambientPulse + 0.006f) % 6.28f

                drops = drops.map { drop ->
                    val nextZ = drop.z - drop.speed
                    if (nextZ <= 0f) {
                        val impactIntensity = drop.size / 2.5f
                        newImpactsList.add(
                            Impact(
                                x = drop.x,
                                y = drop.y,
                                progress = 0f,
                                intensity = impactIntensity,
                                particles = List(8) {
                                    Particle(
                                        angle = Random.nextFloat() * 6.28f,
                                        velocity = Random.nextFloat() * 10f + 3f,
                                        size = Random.nextFloat() * 2f + 1f
                                    )
                                }
                            )
                        )

                        if (Random.nextFloat() > 0.75f) {
                            newScreenDropsList.add(
                                ScreenDrop(
                                    x = drop.x,
                                    y = drop.y,
                                    alpha = 0.65f,
                                    size = Random.nextFloat() * 3.5f + 2.5f,
                                    slideSpeed = Random.nextFloat() * 1.8f + 0.6f,
                                    wobblePhase = Random.nextFloat() * 6.28f
                                )
                            )
                        }

                        drop.copy(
                            z = 1f,
                            x = Random.nextFloat() * screenWidth,
                            y = Random.nextFloat() * screenHeight,
                            speed = Random.nextFloat() * 0.028f + 0.02f,
                            angle = Random.nextFloat() * 0.2f - 0.1f
                        )
                    } else {
                        drop.copy(z = nextZ)
                    }
                }

                impacts = (impacts + newImpactsList).mapNotNull { impact ->
                    val next = impact.progress + 0.05f
                    if (next >= 1f) null else impact.copy(progress = next)
                }

                screenDrops = (screenDrops + newScreenDropsList).mapNotNull { sd ->
                    val nextY = sd.y + sd.slideSpeed
                    val nextAlpha = sd.alpha - 0.0025f
                    sd.wobblePhase += 0.12f

                    if (nextAlpha <= 0f || nextY > screenHeight + 20f) {
                        if (nextY > screenHeight * 0.25f && Random.nextFloat() > 0.6f) {
                            newTrailsList.add(
                                WaterTrail(
                                    x = sd.x,
                                    startY = sd.y - 25f,
                                    endY = sd.y,
                                    alpha = 0.25f,
                                    width = sd.size * 0.35f
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
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        val ambientAlpha = (sin(ambientPulse) * 0.02f + 0.025f).coerceIn(0f, 0.045f)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4466AA).copy(alpha = ambientAlpha),
                            Color.Transparent,
                            Color(0xFF334488).copy(alpha = ambientAlpha * 0.5f)
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            trails.forEach { trail ->
                drawLine(
                    color = trailColor.copy(alpha = trail.alpha * 0.6f),
                    start = Offset(trail.x, trail.startY),
                    end = Offset(trail.x, trail.endY),
                    strokeWidth = trail.width,
                    cap = StrokeCap.Round
                )
            }

            drops.sortedByDescending { it.z }.forEach { drop ->
                val proximity = 1f - drop.z
                val proximityEased = proximity * proximity * proximity
                val currentLength = drop.size * proximityEased * 20f
                val currentWidth = drop.size * proximityEased * 2.5f
                val alpha = (proximityEased * 0.55f).coerceIn(0f, 0.55f)

                if (currentLength > 2f) {
                    val startX = drop.x
                    val startY = drop.y - currentLength / 2
                    val endX = drop.x + drop.angle * currentLength
                    val endY = drop.y + currentLength / 2

                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                dropColorEdge.copy(alpha = alpha * 0.4f),
                                dropColorCore.copy(alpha = alpha),
                                dropColorCore.copy(alpha = alpha * 0.7f)
                            ),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY)
                        ),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = currentWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            impacts.forEach { impact ->
                val inv = 1f - impact.progress
                val invEased = inv * inv
                val baseRadius = impact.progress * 55f * impact.intensity

                drawCircle(
                    color = impactColor.copy(alpha = invEased * 0.3f),
                    radius = baseRadius,
                    center = Offset(impact.x, impact.y),
                    style = Stroke(width = 2f * invEased + 0.5f)
                )

                drawCircle(
                    color = impactColor.copy(alpha = invEased * 0.15f),
                    radius = baseRadius * 0.6f,
                    center = Offset(impact.x, impact.y),
                    style = Stroke(width = 1.2f * invEased)
                )

                impact.particles.forEach { p ->
                    val dist = impact.progress * p.velocity * 6f
                    val particleAlpha = invEased * 0.4f
                    val particleSize = p.size * invEased

                    val px = impact.x + kotlin.math.cos(p.angle) * dist
                    val py = impact.y + kotlin.math.sin(p.angle) * dist * 0.5f + impact.progress * 12f

                    if (particleSize > 0.3f) {
                        drawCircle(
                            color = Color.White.copy(alpha = particleAlpha),
                            radius = particleSize,
                            center = Offset(px, py)
                        )
                    }
                }

                if (impact.progress < 0.12f) {
                    val flashAlpha = (0.12f - impact.progress) * 5f
                    drawCircle(
                        color = Color.White.copy(alpha = flashAlpha * 0.2f * impact.intensity),
                        radius = 10f * impact.intensity,
                        center = Offset(impact.x, impact.y)
                    )
                }
            }

            screenDrops.forEach { sd ->
                val wobbleX = sin(sd.wobblePhase) * 1.5f
                val dx = sd.x + wobbleX

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            screenDropColor.copy(alpha = sd.alpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(dx, sd.y),
                        radius = sd.size * 2.5f
                    ),
                    topLeft = Offset(dx - sd.size * 2.5f, sd.y - sd.size * 2.5f),
                    size = Size(sd.size * 5f, sd.size * 5f)
                )

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = sd.alpha * 0.8f),
                            screenDropColor.copy(alpha = sd.alpha * 0.45f),
                            dropColorEdge.copy(alpha = sd.alpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(dx - sd.size * 0.15f, sd.y - sd.size * 0.15f),
                        radius = sd.size
                    ),
                    topLeft = Offset(dx - sd.size, sd.y - sd.size * 0.75f),
                    size = Size(sd.size * 2f, sd.size * 1.5f)
                )

                drawOval(
                    color = Color.White.copy(alpha = sd.alpha * 0.55f),
                    topLeft = Offset(dx - sd.size * 0.3f, sd.y - sd.size * 0.35f),
                    size = Size(sd.size * 0.35f, sd.size * 0.25f)
                )
            }
        }
    }
}