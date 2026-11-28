package com.example.proyectoquinche.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectoquinche.R

internal val BrandGreen = Color(0xFF176B45)
internal val BrandGreenDark = Color(0xFF0E5235)
internal val BrandGreenSoft = Color(0xFFE9F4EE)
internal val BrandBlue = Color(0xFF245F8F)
internal val BrandBlueSoft = Color(0xFFE9F2F8)
internal val BrandGold = Color(0xFFC59A2F)
internal val BrandGoldSoft = Color(0xFFFBF4DF)
internal val BrandNavy = Color(0xFF173A54)
internal val BrandMuted = Color(0xFF6D7B84)
internal val BrandLine = Color(0xFFE2E7E5)
internal val BrandSurface = Color(0xFFF5F7F6)

private val ShieldShape = GenericShape { size, _ ->
    moveTo(size.width * .12f, 0f)
    lineTo(size.width * .88f, 0f)
    lineTo(size.width, size.height * .12f)
    lineTo(size.width, size.height * .73f)
    lineTo(size.width * .5f, size.height)
    lineTo(0f, size.height * .73f)
    lineTo(0f, size.height * .12f)
    close()
}

@Composable
internal fun BrandLogo(modifier: Modifier = Modifier, compact: Boolean = false) {
    Box(
        modifier
            .clip(ShieldShape)
            .background(Brush.linearGradient(listOf(Color(0xFFEFD57F), Color(0xFFA97817))))
            .padding(if (compact) 2.dp else 4.dp)
            .clip(ShieldShape)
            .background(Brush.linearGradient(listOf(Color(0xFFFBFDFB), Color(0xFFE4EEE8)))),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.align(Alignment.TopEnd).padding(if (compact) 6.dp else 15.dp).size(if (compact) 8.dp else 23.dp).background(Color(0xFFE6BC45), CircleShape))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SJQ", color = BrandGreenDark, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = if (compact) 12.sp else 26.sp, letterSpacing = 1.sp)
            if (!compact) Text("LOGOTIPO OFICIAL", color = BrandGreenDark, fontWeight = FontWeight.Bold, fontSize = 5.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
internal fun BrandTopBar(title: String, online: Boolean, back: Boolean, navigation: () -> Unit) {
    Surface(color = BrandGreenDark) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().height(76.dp).padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(navigation, Modifier.size(42.dp).background(Color.White.copy(.10f), RoundedCornerShape(10.dp))) {
                Icon(if (back) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Menu, null, tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandLogo(Modifier.size(42.dp, 44.dp), compact = true)
                Spacer(Modifier.width(9.dp))
                Column {
                    Text("Comuna San José", color = Color.White.copy(.75f), fontSize = 9.sp)
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(14.dp).background(Color.White.copy(.12f), CircleShape).padding(3.dp).background(if (online) Color(0xFF67D991) else Color.Gray, CircleShape))
            }
        }
    }
}

@Composable
internal fun SyncStrip(online: Boolean, pending: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF8E7),
        shape = RoundedCornerShape(9.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAD9A9))
    ) {
        Row(Modifier.height(36.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).background(if (online) Color(0xFF39A96D) else Color.Gray, CircleShape))
            Spacer(Modifier.width(7.dp)); Text(if (online) "En línea" else "Sin conexión", color = Color(0xFF7D641F), fontSize = 11.sp)
            Box(Modifier.padding(horizontal = 12.dp).width(1.dp).height(18.dp).background(Color(0xFFEAD9A9)))
            Text("↻", color = Color(0xFF9A721C), fontSize = 16.sp); Spacer(Modifier.width(6.dp))
            Text(if (pending == 1) "1 cambio por sincronizar" else "$pending cambios por sincronizar", color = Color(0xFF7D641F), fontSize = 11.sp)
            Spacer(Modifier.width(8.dp)); Text("›", color = Color(0xFF9A721C), fontSize = 17.sp)
        }
    }
}

@Composable
internal fun PrototypeLoginHero() {
    Box(Modifier.fillMaxWidth().height(377.dp)) {
        Image(painterResource(R.drawable.quinche_landscape), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xE6073623), Color(0xA609482F), Color(0xE60A3326)))))
        Column(Modifier.fillMaxSize().padding(top = 28.dp, start = 32.dp, end = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            BrandLogo(Modifier.size(112.dp))
            Row(Modifier.padding(top = 15.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.width(18.dp).height(3.dp).background(BrandGreen)); Box(Modifier.width(18.dp).height(3.dp).background(BrandBlue)); Box(Modifier.width(18.dp).height(3.dp).background(BrandGold))
            }
            Text("ADMINISTRACIÓN COMUNITARIA", Modifier.padding(top = 17.dp), color = Color(0xFFF4DC91), fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 2.sp)
            Text("Comuna San José\ndel Quinche", Modifier.padding(top = 5.dp), color = Color.White, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 32.sp, textAlign = TextAlign.Center)
            Text("Organización, confianza y progreso para nuestra\ncomunidad", Modifier.padding(top = 10.dp), color = Color.White.copy(.86f), fontSize = 14.sp, lineHeight = 20.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
internal fun OfflineNote() {
    Row(Modifier.fillMaxWidth().background(Color(0xFFF1F7F3), RoundedCornerShape(10.dp)).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(28.dp).background(BrandGreen, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(17.dp)) }
        Spacer(Modifier.width(10.dp)); Column { Text("Funciona sin Internet", color = BrandGreenDark, fontWeight = FontWeight.Bold, fontSize = 12.sp); Text("Los registros se guardan de forma segura en este dispositivo.", color = BrandMuted, fontSize = 10.sp) }
    }
}
