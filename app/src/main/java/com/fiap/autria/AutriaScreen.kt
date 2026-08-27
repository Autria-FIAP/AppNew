package com.fiap.autria

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val Night = Color(0xFF050A12)
private val Card = Color(0xFF101725)
private val Purple = Color(0xFF8B4DFF)
private val PurpleDark = Color(0xFF5221AF)
private val Danger = Color(0xFFFF3D3D)
private val Safe = Color(0xFF5EE578)
private val Secondary = Color(0xFFC4C4D5)

data class NavigationState(
    val connected: Boolean = false,
    val battery: Int = 100,
    val distanceCm: Int? = null,
    val sensorValid: Boolean = false,
    val action: String = "WAITING",
    val guideMessage: String = "Conectando ao back-end...",
    val urgent: Boolean = false,
    val speaking: Boolean = false,
    val leftFree: Boolean = true,
    val centerFree: Boolean = true,
    val rightFree: Boolean = true,
    val audioEnabled: Boolean = true,
    val volumePercent: Int = 70,
    val vibrationEnabled: Boolean = true,
    val vibrationPattern: String = "short",
    val backendOnline: Boolean = false,
)

@Composable
fun AutriaApp(viewModel: AutriaViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notice by viewModel.notice.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(notice) {
        notice?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    AutriaScreen(
        state = state,
        onAudioChange = viewModel::setAudioEnabled,
        onEmergency = viewModel::triggerEmergency,
    )
}

@Composable
private fun AutriaScreen(
    state: NavigationState,
    onAudioChange: (Boolean) -> Unit,
    onEmergency: () -> Unit,
) {
    MaterialTheme(colorScheme = darkColorScheme(background = Night, surface = Card, primary = Purple)) {
        Surface(color = Night, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Spacer(Modifier.height(30.dp))
                    Header()
                    ConnectionCard(state.connected, state.battery)
                    StopAlert(state)
                    DistanceCard(state.distanceCm, state.sensorValid)
                    DirectionRow(state)
                    VoiceCard(state.audioEnabled, onAudioChange)
                    EmergencyButton(onEmergency)
                    Spacer(Modifier.height(4.dp))
                }
                BottomNavigation()
            }
        }
    }
}

@Composable
private fun Header() {
    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Menu, "Abrir menu", tint = Color.White, modifier = Modifier.size(34.dp))
        Spacer(Modifier.weight(1f))
        LogoMark(46.dp)
        Spacer(Modifier.width(10.dp))
        Column {
            Text("AUTRIA", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text("NAVEGAÇÃO ASSISTIDA", color = Secondary, fontSize = 11.sp, letterSpacing = .8.sp)
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(PurpleDark, Purple))), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Headphones, "Áudio", tint = Color.White, modifier = Modifier.size(29.dp))
        }
    }
}

@Composable
private fun ConnectionCard(connected: Boolean, battery: Int) {
    AppCard(Modifier.fillMaxWidth(), gradient = true) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).background(Color(0xFF24204B), CircleShape), contentAlignment = Alignment.Center) { GlassesIcon(Color.White) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(if (connected) "Óculos conectado" else "Óculos desconectado", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(if (connected) "Link estável" else "Verifique a conexão", color = Color(0xFFC3A5FF), fontSize = 16.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Bateria", color = Color.White, fontSize = 13.sp)
                Text("$battery%", color = Color.White, fontSize = 23.sp)
            }
            Spacer(Modifier.width(8.dp))
            BatteryIcon(battery)
        }
    }
}

@Composable
private fun StopAlert(state: NavigationState) {
    val isStop = state.action == "STOP" || state.urgent
    val title = when (state.action) {
        "LEFT" -> "ESQUERDA"
        "RIGHT" -> "DIREITA"
        "FORWARD" -> "EM FRENTE"
        "STOP" -> "PARE"
        else -> if (state.backendOnline) "AGUARDE" else "SEM CONEXÃO"
    }
    val colors = if (isStop) {
        listOf(Color(0xFFFF4E42), Color(0xFFB91418))
    } else {
        listOf(Color(0xFF7136D8), Color(0xFF281251))
    }
    Box(
        Modifier.fillMaxWidth().height(252.dp).clip(RoundedCornerShape(22.dp))
            .background(Brush.radialGradient(colors, radius = 650f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.PanTool, null, tint = Color.White, modifier = Modifier.size(72.dp))
            Text(title, color = Color.White, fontSize = 48.sp, lineHeight = 54.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text(state.guideMessage, color = Color.White, fontSize = 19.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.clip(CircleShape).background(Color(0x663D0000)).padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VolumeUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(7.dp)); Text(if (state.speaking && state.audioEnabled) "FALANDO" else "MONITORANDO", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DistanceCard(distance: Int?, valid: Boolean) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("DISTÂNCIA FRONTAL", color = Purple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(72.dp).border(2.dp, Color(0xFF302652), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Sensors, null, tint = Purple, modifier = Modifier.size(46.dp))
                }
                Spacer(Modifier.width(18.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(distance?.toString() ?: "--", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                        if (distance != null) Text(" cm", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Text(if (valid) "Leitura do sensor" else "Sensor sem leitura", color = Secondary, fontSize = 16.sp)
                }
                val close = valid && distance != null && distance <= 30
                Text(if (close) "MUITO PRÓXIMO" else if (valid) "SEGURO" else "OFFLINE", color = if (close) Color(0xFFFF5B5B) else if (valid) Safe else Secondary, fontWeight = FontWeight.Bold, modifier = Modifier.border(1.dp, if (close) Color(0xFF7B2328) else Color(0xFF294B36), CircleShape).padding(horizontal = 12.dp, vertical = 10.dp))
            }
        }
    }
}

@Composable
private fun DirectionRow(state: NavigationState) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DirectionCard("ESQUERDA", state.leftFree, Icons.Default.ArrowBack, Modifier.weight(1f))
        DirectionCard("CENTRO", state.centerFree, Icons.Default.PriorityHigh, Modifier.weight(1f))
        DirectionCard("DIREITA", state.rightFree, Icons.Default.ArrowForward, Modifier.weight(1f))
    }
}

@Composable
private fun DirectionCard(label: String, free: Boolean, icon: ImageVector, modifier: Modifier) {
    AppCard(modifier.height(142.dp)) {
        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Box(Modifier.size(62.dp).border(4.dp, if (free) Color(0xFF493282) else Danger, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = if (free) Purple else Danger, modifier = Modifier.size(38.dp))
            }
            Text(if (free) "LIVRE" else "OBSTRUÍDO", color = if (free) Safe else Danger, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun VoiceCard(enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp).background(Brush.linearGradient(listOf(PurpleDark, Purple)), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.VolumeUp, null, tint = Color.White) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Orientação por voz", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Orientações falando em tempo real", color = Secondary, fontSize = 13.sp)
            }
            Switch(checked = enabled, onCheckedChange = onEnabledChange, colors = SwitchDefaults.colors(checkedTrackColor = Purple, checkedThumbColor = Color.White))
        }
    }
}

@Composable
private fun EmergencyButton(onEmergency: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(78.dp).clip(RoundedCornerShape(22.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFD21D20), Color(0xFFFF3737))))
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    val startedAt = System.currentTimeMillis()
                    val released = tryAwaitRelease()
                    if (released && System.currentTimeMillis() - startedAt >= 3_000L) {
                        onEmergency()
                    }
                })
            }
            .padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(44.dp))
        Spacer(Modifier.width(18.dp))
        Column {
            Text("EMERGÊNCIA", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("Toque e segure por 3 segundos", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun BottomNavigation() {
    Row(Modifier.fillMaxWidth().height(82.dp).background(Color(0xFF0A111D)).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
        NavItem("Início", Icons.Outlined.Home, true, Modifier.weight(1f))
        NavItem("Óculos", Icons.Default.RemoveRedEye, false, Modifier.weight(1f))
        NavItem("Configurações", Icons.Outlined.Settings, false, Modifier.weight(1f))
    }
}

@Composable
private fun NavItem(text: String, icon: ImageVector, selected: Boolean, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (selected) Box(Modifier.width(44.dp).height(3.dp).background(Purple, CircleShape)) else Spacer(Modifier.height(3.dp))
        Spacer(Modifier.height(6.dp)); Icon(icon, text, tint = if (selected) Purple else Secondary, modifier = Modifier.size(29.dp))
        Text(text, color = if (selected) Purple else Secondary, fontSize = 12.sp)
    }
}

@Composable
private fun AppCard(modifier: Modifier, gradient: Boolean = false, content: @Composable BoxScope.() -> Unit) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).background(if (gradient) Brush.horizontalGradient(listOf(Color(0xFF17152F), Color(0xFF121629))) else Brush.linearGradient(listOf(Card, Color(0xFF0B111D)))).border(1.dp, Color(0xFF1C2635), RoundedCornerShape(20.dp)), content = content)
}

@Composable
private fun LogoMark(size: androidx.compose.ui.unit.Dp) {
    Canvas(Modifier.size(size)) {
        val path = androidx.compose.ui.graphics.Path().apply { moveTo(this@Canvas.size.width * .5f, 2f); lineTo(this@Canvas.size.width * .92f, this@Canvas.size.height * .88f); lineTo(this@Canvas.size.width * .57f, this@Canvas.size.height * .68f); lineTo(this@Canvas.size.width * .30f, this@Canvas.size.height * .91f); lineTo(this@Canvas.size.width * .08f, this@Canvas.size.height * .84f); close() }
        drawPath(path, Brush.linearGradient(listOf(Purple, PurpleDark)), style = Stroke(width = 9f, cap = StrokeCap.Round))
    }
}

@Composable
private fun GlassesIcon(color: Color) {
    Canvas(Modifier.size(36.dp, 20.dp)) {
        drawOval(color, topLeft = Offset(1f, 4f), size = Size(size.width * .4f, size.height * .65f), style = Stroke(4f))
        drawOval(color, topLeft = Offset(size.width * .59f, 4f), size = Size(size.width * .4f, size.height * .65f), style = Stroke(4f))
        drawLine(color, Offset(size.width * .4f, size.height * .35f), Offset(size.width * .6f, size.height * .35f), 4f)
    }
}

@Composable
private fun BatteryIcon(percent: Int) {
    Canvas(Modifier.size(44.dp, 28.dp)) {
        drawRoundRect(Color(0xFF6DDC88), style = Stroke(3f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f))
        drawRoundRect(Color(0xFF60D875), topLeft = Offset(5f, 5f), size = Size((size.width - 10f) * (percent / 100f), size.height - 10f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f))
        drawLine(Color(0xFF6DDC88), Offset(size.width + 1f, size.height * .35f), Offset(size.width + 1f, size.height * .65f), 5f)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun PreviewAutria() = AutriaScreen(
    state = NavigationState(
        connected = true,
        battery = 78,
        distanceCm = 20,
        sensorValid = true,
        action = "STOP",
        guideMessage = "Pare. Obstáculo muito próximo.",
        urgent = true,
        centerFree = false,
        backendOnline = true,
    ),
    onAudioChange = {},
    onEmergency = {},
)
