package com.fiap.autria.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiap.autria.R

/**
 * Dados dos três pilares, pra não repetir o mesmo bloco de UI três vezes.
 */
private data class Pilar(
    val titulo: String,
    val descricao: String,
    val icon: ImageVector
)

private val pilares = listOf(
    Pilar(
        titulo = "Autonomia",
        descricao = "Proporcionar mais liberdade para que cada pessoa possa seguir seu próprio caminho com independência e confiança.",
        icon = Icons.Default.Explore
    ),
    Pilar(
        titulo = "Acessibilidade",
        descricao = "Tornar a tecnologia assistiva mais democrática, intuitiva e financeiramente acessível.",
        icon = Icons.Default.Favorite
    ),
    Pilar(
        titulo = "Amparo",
        descricao = "Oferecer segurança e suporte de maneira discreta, especialmente diante de situações de risco ou necessidade.",
        icon = Icons.Default.Shield
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sobre a AUTRIA") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            // Logo centralizada no topo
            Image(
                painter = painterResource(id = R.drawable.autrialogo),
                contentDescription = "Logo Autria",
                modifier = Modifier
                    .size(88.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "A AUTRIA nasceu com um propósito claro: usar a tecnologia para ampliar a liberdade e a autonomia das pessoas, e não criar novas barreiras.",
                fontSize = 17.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(16.dp))

            Paragrafo(
                "Desenvolvemos soluções de tecnologia assistiva voltadas para pessoas com deficiência visual, unindo Inteligência Artificial, acessibilidade e inovação para tornar a autonomia mais acessível, segura e intuitiva."
            )

            Paragrafo(
                "Nossa principal solução são os óculos inteligentes AUTRIA, desenvolvidos para oferecer assistência de forma rápida, discreta e confiável, mantendo o usuário no controle de sua própria jornada. Em um mercado marcado por tecnologias assistivas de alto custo e grande complexidade, buscamos construir uma alternativa mais acessível, sem abrir mão de aspectos essenciais como segurança, baixa latência, comunicação eficiente e facilidade de uso."
            )

            Paragrafo(
                "A essência da AUTRIA está representada em seu símbolo: o A, formado por um traço contínuo que representa um caminho em movimento. Ele simboliza a jornada de cada usuário e a presença da tecnologia como uma companhia que oferece suporte quando necessário, sem interferir em sua independência."
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Essa visão se sustenta em três pilares:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            pilares.forEach { pilar ->
                PilarItem(pilar)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(8.dp))

            Paragrafo(
                "O próprio nome AUTRIA traduz essa proposta. AUTO representa autonomia e independência, enquanto IA representa a Inteligência Artificial que torna essa assistência possível. Juntos, esses conceitos traduzem nossa missão: colocar a Inteligência Artificial a serviço da autonomia humana."
            )

            Paragrafo(
                "Na AUTRIA, acreditamos que a melhor tecnologia assistiva é aquela que não limita, não impõe e não toma o controle. Ela simplesmente está presente quando necessária, permitindo que a pessoa tenha mais confiança para seguir em frente."
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Porque a tecnologia deve estar a serviço da liberdade — e não o contrário.",
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        }
    }
}

@Composable
private fun Paragrafo(texto: String) {
    Text(
        text = texto,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun PilarItem(pilar: Pilar) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = pilar.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pilar.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = pilar.descricao,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}