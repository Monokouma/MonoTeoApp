package com.despaircorp.monoteo.ui.background.tornado

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
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

private data class FunnelSegment(
    val y: Float,
    val baseRadius: Float,
    var rotationPhase: Float,
    val rotationSpeed: Float,
    val wobbleAmplitude: Float,
    val wobbleSpeed: Float
)

private data class VortexParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val orbitRadius: Float,
    var orbitPhase: Float,
    val orbitSpeed: Float,
    val verticalSpeed: Float,
    val layer: Int
)

private data class FlyingDebris(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    var orbitPhase: Float,
    val orbitSpeed: Float,
    val orbitRadius: Float,
    val verticalSpeed: Float,
    var spinPhase: Float,
    val spinSpeed: Float,
    val type: Int
)

private data class StormCloud(
    var x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val baseAlpha: Float,
    val speed: Float,
    val layer: Int,
    var phase: Float,
    val turbulence: Float
)

private data class GroundDebris(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speed: Float,
    var wobblePhase: Float
)

private data class LightningBolt(
    val startX: Float,
    val startY: Float,
    val segments: List<Offset>,
    var alpha: Float
)

private data class RainStreak(
    var x: Float,
    var y: Float,
    val length: Float,
    val width: Float,
    val speed: Float,
    val angle: Float,
    var alpha: Float
)

private val skyGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF050608),
        Color(0xFF0A0C10),
        Color(0xFF101418),
        Color(0xFF181C22),
        Color(0xFF202830),
        Color(0xFF283038)
    )
)

private val funnelColorCore = Color(0xFF1A1C20)
private val funnelColorMid = Color(0xFF2A2E34)
private val funnelColorOuter = Color(0xFF3A4048)
private val funnelColorHighlight = Color(0xFF505860)
private val debrisColor = Color(0xFF404850)
private val debrisColorLight = Color(0xFF606870)
private val cloudColorDark = Color(0xFF101418)
private val cloudColorMid = Color(0xFF1A1E24)
private val cloudColorLight = Color(0xFF242A32)
private val dustColor = Color(0xFF585850)
private val groundDustColor = Color(0xFF484840)
private val electricBlue = Color(0xFF6090FF)
private val rainColor = Color(0xFF8090A0)

@Suppress("EffectKeys")
@Composable
fun TornadoBackground(modifier: Modifier = Modifier) {
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    var funnelSegments by remember { mutableStateOf(emptyList<FunnelSegment>()) }
    var vortexParticles by remember { mutableStateOf(emptyList<VortexParticle>()) }
    var flyingDebris by remember { mutableStateOf(emptyList<FlyingDebris>()) }
    var stormClouds by remember { mutableStateOf(emptyList<StormCloud>()) }
    var groundDebris by remember { mutableStateOf(emptyList<GroundDebris>()) }
    var rainStreaks by remember { mutableStateOf(emptyList<RainStreak>()) }
    var lightning by remember { mutableStateOf<LightningBolt?>(null) }
    var globalPhase by remember { mutableFloatStateOf(0f) }
    var funnelSwayPhase by remember { mutableFloatStateOf(0f) }
    var intensityPhase by remember { mutableFloatStateOf(0f) }

    val funnelCenterX by remember(screenWidth) { derivedStateOf { screenWidth * 0.5f } }

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0) {
            funnelSegments = List(40) { i ->
                val t = i / 39f
                val tCurved = t.pow(1.3f)
                FunnelSegment(
                    y = screenHeight * 0.08f + tCurved * screenHeight * 0.85f,
                    baseRadius = 15f + tCurved.pow(1.8f) * 150f,
                    rotationPhase = Random.nextFloat() * 6.28f,
                    rotationSpeed = 0.2f - t * 0.12f,
                    wobbleAmplitude = 5f + t * 15f,
                    wobbleSpeed = 0.08f - t * 0.03f
                )
            }

            vortexParticles = List(120) {
                val t = Random.nextFloat()
                val layer = when {
                    Random.nextFloat() < 0.3f -> 0
                    Random.nextFloat() < 0.7f -> 1
                    else -> 2
                }
                VortexParticle(
                    x = 0f,
                    y = screenHeight * 0.1f + Random.nextFloat() * screenHeight * 0.85f,
                    size = when (layer) {
                        0 -> Random.nextFloat() * 3f + 1f
                        1 -> Random.nextFloat() * 5f + 2f
                        else -> Random.nextFloat() * 8f + 3f
                    },
                    alpha = Random.nextFloat() * 0.6f + 0.2f,
                    orbitRadius = 25f + t.pow(1.5f) * 140f,
                    orbitPhase = Random.nextFloat() * 6.28f,
                    orbitSpeed = 0.18f - t * 0.1f,
                    verticalSpeed = -(Random.nextFloat() * 2.5f + 0.8f),
                    layer = layer
                )
            }

            flyingDebris = List(40) {
                val t = Random.nextFloat()
                FlyingDebris(
                    x = 0f,
                    y = screenHeight * 0.15f + Random.nextFloat() * screenHeight * 0.75f,
                    size = Random.nextFloat() * 15f + 5f,
                    alpha = Random.nextFloat() * 0.8f + 0.2f,
                    orbitPhase = Random.nextFloat() * 6.28f,
                    orbitSpeed = 0.12f - t * 0.06f,
                    orbitRadius = 40f + t.pow(1.4f) * 120f,
                    verticalSpeed = -(Random.nextFloat() * 2f + 0.5f),
                    spinPhase = Random.nextFloat() * 6.28f,
                    spinSpeed = Random.nextFloat() * 0.3f + 0.1f,
                    type = Random.nextInt(5)
                )
            }

            stormClouds = List(18) { i ->
                val layer = i % 3
                StormCloud(
                    x = Random.nextFloat() * screenWidth * 3f - screenWidth,
                    y = when (layer) {
                        0 -> Random.nextFloat() * screenHeight * 0.12f
                        1 -> Random.nextFloat() * screenHeight * 0.15f + screenHeight * 0.03f
                        else -> Random.nextFloat() * screenHeight * 0.18f + screenHeight * 0.08f
                    },
                    width = Random.nextFloat() * screenWidth * 1.5f + screenWidth * 0.6f,
                    height = Random.nextFloat() * screenHeight * 0.12f + screenHeight * 0.06f,
                    baseAlpha = when (layer) {
                        0 -> Random.nextFloat() * 0.2f + 0.15f
                        1 -> Random.nextFloat() * 0.25f + 0.2f
                        else -> Random.nextFloat() * 0.3f + 0.25f
                    },
                    speed = when (layer) {
                        0 -> Random.nextFloat() * 1.5f + 0.8f
                        1 -> Random.nextFloat() * 2.5f + 1.2f
                        else -> Random.nextFloat() * 4f + 2f
                    },
                    layer = layer,
                    phase = Random.nextFloat() * 6.28f,
                    turbulence = Random.nextFloat() * 0.3f + 0.1f
                )
            }

            groundDebris = List(50) {
                GroundDebris(
                    x = Random.nextFloat() * screenWidth * 2f - screenWidth * 0.5f,
                    y = screenHeight * 0.88f + Random.nextFloat() * screenHeight * 0.12f,
                    size = Random.nextFloat() * 6f + 2f,
                    alpha = Random.nextFloat() * 0.6f + 0.2f,
                    speed = Random.nextFloat() * 6f + 2f,
                    wobblePhase = Random.nextFloat() * 6.28f
                )
            }

            rainStreaks = List(80) {
                RainStreak(
                    x = Random.nextFloat() * screenWidth * 1.5f - screenWidth * 0.25f,
                    y = Random.nextFloat() * screenHeight * 1.5f - screenHeight * 0.25f,
                    length = Random.nextFloat() * 40f + 20f,
                    width = Random.nextFloat() * 2f + 0.5f,
                    speed = Random.nextFloat() * 20f + 12f,
                    angle = Random.nextFloat() * 0.5f + 0.3f,
                    alpha = Random.nextFloat() * 0.25f + 0.1f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                if (screenWidth <= 0 || screenHeight <= 0) return@withFrameMillis

                globalPhase = (globalPhase + 0.015f) % 6.28f
                funnelSwayPhase = (funnelSwayPhase + 0.008f) % 6.28f
                intensityPhase = (intensityPhase + 0.005f) % 6.28f

                val intensity = 0.8f + sin(intensityPhase) * 0.2f

                funnelSegments = funnelSegments.map { segment ->
                    segment.rotationPhase += segment.rotationSpeed * intensity
                    segment.copy(rotationPhase = segment.rotationPhase)
                }

                vortexParticles = vortexParticles.map { particle ->
                    particle.orbitPhase += particle.orbitSpeed * intensity
                    var newY = particle.y + particle.verticalSpeed * intensity

                    if (newY < screenHeight * 0.05f) {
                        newY = screenHeight * 0.9f + Random.nextFloat() * screenHeight * 0.05f
                    }

                    val t = ((newY - screenHeight * 0.08f) / (screenHeight * 0.85f)).coerceIn(0f, 1f)
                    val tCurved = t.pow(1.3f)
                    val currentRadius = particle.orbitRadius * (0.15f + tCurved * 0.85f)
                    val funnelSway = sin(funnelSwayPhase) * 25f * tCurved

                    particle.copy(
                        x = funnelCenterX + funnelSway + cos(particle.orbitPhase) * currentRadius,
                        y = newY,
                        orbitPhase = particle.orbitPhase
                    )
                }

                flyingDebris = flyingDebris.map { debris ->
                    debris.orbitPhase += debris.orbitSpeed * intensity
                    debris.spinPhase += debris.spinSpeed * intensity
                    var newY = debris.y + debris.verticalSpeed * intensity

                    if (newY < screenHeight * 0.05f) {
                        newY = screenHeight * 0.85f + Random.nextFloat() * screenHeight * 0.1f
                    }

                    val t = ((newY - screenHeight * 0.08f) / (screenHeight * 0.85f)).coerceIn(0f, 1f)
                    val tCurved = t.pow(1.3f)
                    val currentRadius = debris.orbitRadius * (0.2f + tCurved * 0.8f)
                    val funnelSway = sin(funnelSwayPhase) * 25f * tCurved

                    debris.copy(
                        x = funnelCenterX + funnelSway + cos(debris.orbitPhase) * currentRadius,
                        y = newY,
                        orbitPhase = debris.orbitPhase,
                        spinPhase = debris.spinPhase
                    )
                }

                stormClouds = stormClouds.map { cloud ->
                    cloud.phase += 0.01f * intensity
                    var newX = cloud.x + cloud.speed * intensity

                    if (newX > screenWidth + cloud.width * 0.5f) {
                        newX = -cloud.width
                    }

                    cloud.copy(x = newX, phase = cloud.phase)
                }

                groundDebris = groundDebris.map { debris ->
                    debris.wobblePhase += 0.1f
                    var newX = debris.x + debris.speed * intensity

                    if (newX > screenWidth + 20f) {
                        newX = -20f
                    }

                    debris.copy(x = newX, wobblePhase = debris.wobblePhase)
                }

                rainStreaks = rainStreaks.map { streak ->
                    var newX = streak.x + streak.speed * streak.angle * intensity
                    var newY = streak.y + streak.speed * intensity

                    if (newY > screenHeight + streak.length || newX > screenWidth + streak.length) {
                        newX = -streak.length + Random.nextFloat() * screenWidth * 0.3f
                        newY = -streak.length - Random.nextFloat() * screenHeight * 0.3f
                    }

                    streak.copy(x = newX, y = newY)
                }

                lightning = if (Random.nextFloat() > 0.992f) {
                    val startX = funnelCenterX + Random.nextFloat() * 150f - 75f
                    val segments = mutableListOf<Offset>()
                    var currentX = startX
                    var currentY = screenHeight * 0.05f
                    val endY = screenHeight * (0.3f + Random.nextFloat() * 0.4f)

                    while (currentY < endY) {
                        currentX += Random.nextFloat() * 40f - 20f
                        currentY += Random.nextFloat() * 30f + 15f
                        segments.add(Offset(currentX, currentY))
                    }

                    LightningBolt(startX, screenHeight * 0.05f, segments, 1f)
                } else {
                    lightning?.let {
                        val newAlpha = it.alpha - 0.12f
                        if (newAlpha <= 0f) null else it.copy(alpha = newAlpha)
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(skyGradient)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            screenWidth = size.width
            screenHeight = size.height

            if (screenWidth <= 0 || screenHeight <= 0) return@Canvas

            val intensity = 0.8f + sin(intensityPhase) * 0.2f
            val ambientAlpha = (sin(globalPhase * 0.3f) * 0.04f + 0.08f).coerceIn(0f, 0.12f)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        cloudColorMid.copy(alpha = ambientAlpha * 0.5f),
                        Color.Transparent,
                        groundDustColor.copy(alpha = ambientAlpha * 0.6f)
                    )
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            stormClouds.sortedBy { it.layer }.forEach { cloud ->
                val turbulenceOffset = sin(cloud.phase * cloud.turbulence * 10f) * 8f
                val pulseAlpha = cloud.baseAlpha * (0.7f + sin(cloud.phase) * 0.3f) * intensity

                val color = when (cloud.layer) {
                    0 -> cloudColorDark
                    1 -> cloudColorMid
                    else -> cloudColorLight
                }

                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = pulseAlpha),
                            color.copy(alpha = pulseAlpha * 0.5f),
                            color.copy(alpha = pulseAlpha * 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(cloud.x + cloud.width / 2, cloud.y + cloud.height / 2 + turbulenceOffset),
                        radius = (cloud.width / 2).coerceAtLeast(1f)
                    ),
                    topLeft = Offset(cloud.x, cloud.y + turbulenceOffset),
                    size = Size(cloud.width, cloud.height)
                )
            }

            lightning?.let { bolt ->
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            electricBlue.copy(alpha = bolt.alpha * 0.25f),
                            electricBlue.copy(alpha = bolt.alpha * 0.1f),
                            Color.Transparent
                        ),
                        center = Offset(bolt.startX, screenHeight * 0.15f),
                        radius = (screenHeight * 0.6f).coerceAtLeast(1f)
                    ),
                    topLeft = Offset.Zero,
                    size = Size(screenWidth, screenHeight)
                )

                val path = Path().apply {
                    moveTo(bolt.startX, bolt.startY)
                    bolt.segments.forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }

                drawPath(
                    path = path,
                    color = electricBlue.copy(alpha = bolt.alpha * 0.4f),
                    style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = bolt.alpha * 0.8f),
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = bolt.alpha),
                    style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            rainStreaks.forEach { streak ->
                val endX = streak.x + streak.length * streak.angle
                val endY = streak.y + streak.length

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            rainColor.copy(alpha = streak.alpha * 0.3f * intensity),
                            rainColor.copy(alpha = streak.alpha * intensity),
                            rainColor.copy(alpha = streak.alpha * 0.5f * intensity)
                        ),
                        start = Offset(streak.x, streak.y),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(streak.x, streak.y),
                    end = Offset(endX, endY),
                    strokeWidth = streak.width,
                    cap = StrokeCap.Round
                )
            }

            funnelSegments.forEachIndexed { index, segment ->
                val t = index / (funnelSegments.size - 1f)
                val tCurved = t.pow(1.3f)
                val funnelSway = sin(funnelSwayPhase) * 25f * tCurved
                val wobble = sin(segment.rotationPhase * segment.wobbleSpeed * 20f) * segment.wobbleAmplitude
                val centerX = funnelCenterX + funnelSway + wobble

                val baseAlpha = (0.25f + tCurved * 0.35f) * intensity
                val radius = segment.baseRadius * (0.9f + sin(segment.rotationPhase) * 0.1f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            funnelColorCore.copy(alpha = baseAlpha * 0.8f),
                            funnelColorMid.copy(alpha = baseAlpha * 0.5f),
                            funnelColorOuter.copy(alpha = baseAlpha * 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(centerX, segment.y),
                        radius = (radius * 2f).coerceAtLeast(1f)
                    ),
                    radius = (radius * 2f).coerceAtLeast(1f),
                    center = Offset(centerX, segment.y)
                )

                val ringAlpha = baseAlpha * 0.7f
                drawCircle(
                    color = funnelColorMid.copy(alpha = ringAlpha),
                    radius = radius.coerceAtLeast(1f),
                    center = Offset(centerX, segment.y),
                    style = Stroke(width = 2f + tCurved * 5f)
                )

                if (index > 0 && index % 2 == 0) {
                    val prevSegment = funnelSegments[index - 1]
                    val prevT = (index - 1) / (funnelSegments.size - 1f)
                    val prevTCurved = prevT.pow(1.3f)
                    val prevSway = sin(funnelSwayPhase) * 25f * prevTCurved
                    val prevWobble = sin(prevSegment.rotationPhase * prevSegment.wobbleSpeed * 20f) * prevSegment.wobbleAmplitude
                    val prevCenterX = funnelCenterX + prevSway + prevWobble

                    for (i in 0 until 6) {
                        val angle = segment.rotationPhase + i * 1.047f
                        val prevAngle = prevSegment.rotationPhase + i * 1.047f

                        val startX = prevCenterX + cos(prevAngle) * prevSegment.baseRadius * 0.8f
                        val startY = prevSegment.y
                        val endX = centerX + cos(angle) * radius * 0.8f
                        val endY = segment.y

                        drawLine(
                            color = funnelColorOuter.copy(alpha = baseAlpha * 0.4f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 1f + tCurved * 2f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                if (t > 0.3f) {
                    val highlightAngle = segment.rotationPhase + 0.5f
                    val highlightX = centerX + cos(highlightAngle) * radius * 0.6f
                    val highlightY = segment.y + sin(highlightAngle) * radius * 0.2f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                funnelColorHighlight.copy(alpha = baseAlpha * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(highlightX, highlightY),
                            radius = (radius * 0.4f).coerceAtLeast(1f)
                        ),
                        radius = (radius * 0.4f).coerceAtLeast(1f),
                        center = Offset(highlightX, highlightY)
                    )
                }
            }

            vortexParticles.filter { it.layer == 0 }.sortedBy { it.y }.forEach { particle ->
                drawVortexParticle(particle, intensity, dustColor)
            }

            vortexParticles.filter { it.layer == 1 }.sortedBy { it.y }.forEach { particle ->
                drawVortexParticle(particle, intensity, dustColor)
            }

            flyingDebris.sortedBy { it.y }.forEach { debris ->
                drawFlyingDebris(debris, intensity)
            }

            vortexParticles.filter { it.layer == 2 }.sortedBy { it.y }.forEach { particle ->
                drawVortexParticle(particle, intensity, dustColor)
            }

            groundDebris.forEach { debris ->
                val wobbleY = sin(debris.wobblePhase) * 3f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            groundDustColor.copy(alpha = debris.alpha * 0.3f * intensity),
                            Color.Transparent
                        ),
                        center = Offset(debris.x, debris.y + wobbleY),
                        radius = (debris.size * 3f).coerceAtLeast(1f)
                    ),
                    radius = (debris.size * 3f).coerceAtLeast(1f),
                    center = Offset(debris.x, debris.y + wobbleY)
                )

                drawCircle(
                    color = groundDustColor.copy(alpha = debris.alpha * intensity),
                    radius = debris.size.coerceAtLeast(0.5f),
                    center = Offset(debris.x, debris.y + wobbleY)
                )
            }

            val baseGlowIntensity = (sin(globalPhase) * 0.03f + 0.06f).coerceIn(0f, 0.09f) * intensity
            val funnelBaseSway = sin(funnelSwayPhase) * 25f

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        groundDustColor.copy(alpha = baseGlowIntensity * 1.5f),
                        dustColor.copy(alpha = baseGlowIntensity * 0.8f),
                        Color.Transparent
                    ),
                    center = Offset(funnelCenterX + funnelBaseSway, screenHeight * 0.95f),
                    radius = (screenWidth * 0.45f).coerceAtLeast(1f)
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth, screenHeight)
            )

            val edgeAlpha = (sin(globalPhase * 0.4f) * 0.05f + 0.1f).coerceIn(0f, 0.15f) * intensity

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        cloudColorDark.copy(alpha = edgeAlpha),
                        Color.Transparent
                    ),
                    startX = 0f,
                    endX = screenWidth * 0.15f
                ),
                topLeft = Offset.Zero,
                size = Size(screenWidth * 0.15f, screenHeight)
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        cloudColorDark.copy(alpha = edgeAlpha)
                    ),
                    startX = screenWidth * 0.85f,
                    endX = screenWidth
                ),
                topLeft = Offset(screenWidth * 0.85f, 0f),
                size = Size(screenWidth * 0.15f, screenHeight)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawVortexParticle(
    particle: VortexParticle,
    intensity: Float,
    color: Color
) {
    val t = ((particle.y - size.height * 0.08f) / (size.height * 0.85f)).coerceIn(0f, 1f)
    val depthAlpha = particle.alpha * (0.4f + t * 0.6f) * intensity

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = depthAlpha * 0.25f),
                Color.Transparent
            ),
            center = Offset(particle.x, particle.y),
            radius = (particle.size * 3.5f).coerceAtLeast(1f)
        ),
        radius = (particle.size * 3.5f).coerceAtLeast(1f),
        center = Offset(particle.x, particle.y)
    )

    drawCircle(
        color = color.copy(alpha = depthAlpha),
        radius = particle.size.coerceAtLeast(0.5f),
        center = Offset(particle.x, particle.y)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFlyingDebris(
    debris: FlyingDebris,
    intensity: Float
) {
    val t = ((debris.y - size.height * 0.08f) / (size.height * 0.85f)).coerceIn(0f, 1f)
    val depthAlpha = debris.alpha * (0.5f + t * 0.5f) * intensity
    val spin = debris.spinPhase

    when (debris.type) {
        0 -> {
            val scale = 0.5f + sin(spin) * 0.5f
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        debrisColor.copy(alpha = depthAlpha),
                        debrisColor.copy(alpha = depthAlpha * 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(debris.x, debris.y),
                    radius = (debris.size * scale).coerceAtLeast(1f)
                ),
                topLeft = Offset(debris.x - debris.size * scale, debris.y - debris.size * scale * 0.4f),
                size = Size((debris.size * scale * 2f).coerceAtLeast(1f), (debris.size * scale * 0.8f).coerceAtLeast(1f))
            )
        }
        1 -> {
            val len = debris.size * 1.5f
            drawLine(
                color = debrisColorLight.copy(alpha = depthAlpha),
                start = Offset(debris.x - cos(spin) * len, debris.y - sin(spin) * len * 0.4f),
                end = Offset(debris.x + cos(spin) * len, debris.y + sin(spin) * len * 0.4f),
                strokeWidth = (debris.size * 0.35f).coerceAtLeast(1f),
                cap = StrokeCap.Round
            )
        }
        2 -> {
            val path = Path().apply {
                for (i in 0 until 4) {
                    val angle = spin + i * 1.57f
                    val px = debris.x + cos(angle) * debris.size
                    val py = debris.y + sin(angle) * debris.size * 0.6f
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(
                path = path,
                color = debrisColor.copy(alpha = depthAlpha)
            )
        }
        3 -> {
            val path = Path().apply {
                for (i in 0 until 3) {
                    val angle = spin + i * 2.094f
                    val px = debris.x + cos(angle) * debris.size
                    val py = debris.y + sin(angle) * debris.size * 0.7f
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(
                path = path,
                color = debrisColorLight.copy(alpha = depthAlpha)
            )
        }
        else -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        debrisColor.copy(alpha = depthAlpha),
                        debrisColor.copy(alpha = depthAlpha * 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(debris.x, debris.y),
                    radius = debris.size.coerceAtLeast(1f)
                ),
                radius = debris.size.coerceAtLeast(1f),
                center = Offset(debris.x, debris.y)
            )
        }
    }
}