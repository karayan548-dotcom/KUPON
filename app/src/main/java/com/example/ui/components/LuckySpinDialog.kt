package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlayAmber
import com.example.ui.theme.PlayBlue
import com.example.ui.theme.PlayGreen
import com.example.ui.theme.PlayPointsGold
import com.example.ui.theme.PlayRed
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SpinPrize(
    val title: String,
    val creditBonus: Double,
    val pointsBonus: Int,
    val color: Color
)

@Composable
fun LuckySpinDialog(
    onDismiss: () -> Unit,
    onClaimReward: (title: String, credit: Double, points: Int) -> Unit
) {
    val prizes = remember {
        listOf(
            SpinPrize("$1.00 Credit", 1.0, 10, PlayBlue),
            SpinPrize("50 Points", 0.0, 50, PlayPointsGold),
            SpinPrize("$2.50 Credit", 2.50, 25, PlayGreen),
            SpinPrize("100 Points", 0.0, 100, PlayAmber),
            SpinPrize("$0.50 Credit", 0.50, 15, PlayRed),
            SpinPrize("Super 150 Pts", 0.0, 150, Color(0xFF7B1FA2))
        )
    }

    var isSpinning by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<SpinPrize?>(null) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isSpinning) onDismiss() },
        confirmButton = {
            if (wonPrize != null) {
                Button(
                    onClick = {
                        onClaimReward(wonPrize!!.title, wonPrize!!.creditBonus, wonPrize!!.pointsBonus)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlayGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("claim_spin_reward_button")
                ) {
                    Text(
                        text = "Claim ${wonPrize!!.title} & Add to Wallet",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            } else {
                Button(
                    onClick = {
                        if (!isSpinning) {
                            isSpinning = true
                            scope.launch {
                                val targetPrizeIndex = Random.nextInt(prizes.size)
                                val targetSectorAngle = 360f / prizes.size
                                // Total spin: 5 full turns + target offset
                                val targetRotation = 360f * 5 + (targetPrizeIndex * targetSectorAngle) + (targetSectorAngle / 2)

                                rotation.animateTo(
                                    targetValue = targetRotation,
                                    animationSpec = tween(
                                        durationMillis = 3200,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                                // Sector opposite pointer (at 270 deg or top 0 deg)
                                wonPrize = prizes[targetPrizeIndex]
                                isSpinning = false
                            }
                        }
                    },
                    enabled = !isSpinning,
                    colors = ButtonDefaults.buttonColors(containerColor = PlayBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("spin_wheel_button")
                ) {
                    Icon(imageVector = Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSpinning) "Spinning..." else "SPIN FOR FREE NOW!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Lucky Wheel",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Spin to win instant Play Credits & Points",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isSpinning && wonPrize == null) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel Container with Top Pointer
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .size(230.dp)
                            .rotate(rotation.value)
                    ) {
                        val canvasSize = size.minDimension
                        val radius = canvasSize / 2f
                        val center = Offset(radius, radius)
                        val sectorAngle = 360f / prizes.size

                        prizes.forEachIndexed { index, prize ->
                            drawArc(
                                color = prize.color,
                                startAngle = index * sectorAngle,
                                sweepAngle = sectorAngle,
                                useCenter = true,
                                size = Size(canvasSize, canvasSize),
                                style = Fill
                            )
                        }
                    }

                    // Inner decorative circle
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = PlayPointsGold,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Pointer at Top Center
                    Canvas(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(24.dp)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path = path, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Prize Announcement
                if (wonPrize != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PlayGreen.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PlayGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "CONGRATULATIONS!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = PlayGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You won ${wonPrize!!.title}!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Text(
                        text = "100% Guaranteed prize on every daily spin!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
