package com.fiap.autria

import android.widget.Toast
import com.fiap.autria.settings.SettingScreen
import com.fiap.autria.about.AboutScreen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fiap.autria.R
import com.fiap.autria.ui.theme.AutriaTheme

// ---------------------------------------------------------------------------
// Cores "extras" que o Material3 ColorScheme não tem por padrão
// (sucesso/perigo). Se preferir, mova isto para o Color.kt do projeto autria
// e reaproveite em qualquer tela.
// ---------------------------------------------------------------------------
private val Safe = Color(0xFF5EE578)   // pode virar "Success" no Color.kt
// Danger não precisa de cor própria: usamos MaterialTheme.colorScheme.error

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
    var showSettings by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var isDarkTheme by remember { mutableStateOf(true) }

    LaunchedEffect(notice) {
        notice?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    AutriaTheme(darkTheme = isDarkTheme) {
        when {
            showAbout -> AboutScreen(
                onBackClick = { showAbout = false }
            )
            showSettings -> SettingScreen(
                onBackClick = { showSettings = false },
                onToggleTheme = { isDarkTheme = !isDarkTheme },
                onAboutClick = { showAbout = true }
            )
            else -> AutriaScreen(
                state = state,
                onSettingsClick = { showSettings = true }
            )
        }
    }
}

@Composable
private fun AutriaScreen(
    state: NavigationState,
    onSettingsClick: () -> Unit,
) {
    // O wrapper MaterialTheme(...) manual foi removido daqui: quem define
    // cores/tipografia agora é o AutriaTheme, chamado uma vez lá em cima.
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(30.dp))
            Header(onSettingsClick = onSettingsClick)
            ConnectionCard(state.connected, state.battery)
            StopAlert(state)
            DistanceCard(state.distanceCm, state.sensorValid)
            DirectionRow(state)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Header(onSettingsClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Default.Settings, "Configurações",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(34.dp)
                .clickable { onSettingsClick() }
        )
        Spacer(Modifier.weight(1f))

        // Logo real do autria (aumentada, sem fundo/container).
        Image(
            painter = painterResource(id = R.drawable.autrialogo),
            contentDescription = "Logo Autria",
            modifier = Modifier.size(72.dp)
        )

        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "AUTRIA",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp
            )
            Text(
                "NAVEGAÇÃO ASSISTIDA",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 11.sp, letterSpacing = .8.sp
            )
        }
        // O ícone de fone de ouvido que ficava aqui no canto direito foi removido.
    }
}

@Composable
private fun ConnectionCard(connected: Boolean, battery: Int) {
    AppCard(Modifier.fillMaxWidth(), gradient = true) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Antes: Box com fundo primaryContainer (laranja) atrás da imagem.
            // Agora: só a imagem dos óculos, maior e sem fundo colorido.
            Image(
                painter = painterResource(id = R.drawable.imgoculos),
                contentDescription = "Óculos Autria",
                modifier = Modifier.size(54.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (connected) "Óculos conectado" else "Óculos desconectado",
                    color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    if (connected) "Link estável" else "Verifique a conexão",
                    color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Bateria", color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
                Text("$battery%", color = MaterialTheme.colorScheme.onBackground, fontSize = 23.sp)
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
    // Antes: dois pares de hex fixos (vermelho / roxo).
    // Agora: error do tema para alerta e primary para o estado normal.
    val colors = if (isStop) {
        listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer)
    } else {
        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
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
            Row(
                Modifier.clip(CircleShape).background(Color(0x33000000)).padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.VolumeUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(7.dp))
                Text(if (state.speaking && state.audioEnabled) "FALANDO" else "MONITORANDO", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DistanceCard(distance: Int?, valid: Boolean) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("DISTÂNCIA FRONTAL", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(72.dp).border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sensors, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(46.dp))
                }
                Spacer(Modifier.width(18.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(distance?.toString() ?: "--", color = MaterialTheme.colorScheme.onBackground, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                        if (distance != null) Text(" cm", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Text(if (valid) "Leitura do sensor" else "Sensor sem leitura", color = MaterialTheme.colorScheme.secondary, fontSize = 16.sp)
                }
                val close = valid && distance != null && distance <= 30
                Text(
                    if (close) "MUITO PRÓXIMO" else if (valid) "SEGURO" else "OFFLINE",
                    color = if (close) MaterialTheme.colorScheme.error else if (valid) Safe else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(1.dp, if (close) MaterialTheme.colorScheme.error else Safe, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
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
            Text(label, color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Box(
                Modifier.size(62.dp).border(4.dp, if (free) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (free) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, modifier = Modifier.size(38.dp))
            }
            Text(if (free) "LIVRE" else "OBSTRUÍDO", color = if (free) Safe else MaterialTheme.colorScheme.error, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AppCard(modifier: Modifier, gradient: Boolean = false, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (gradient)
                    Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant))
                else
                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))
            )
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
        content = content
    )
}

// BatteryIcon continua como um desenho vetorial simples.
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
private fun PreviewAutria() {
    AutriaTheme(darkTheme = true) {
        AutriaScreen(
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
            onSettingsClick = {},
        )
    }
}