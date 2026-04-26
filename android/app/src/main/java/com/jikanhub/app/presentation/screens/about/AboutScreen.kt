package com.jikanhub.app.presentation.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jikanhub.app.presentation.theme.*

@Composable
fun AboutContent() {
    val context = LocalContext.current
    val githubUrl = "https://github.com/HenriSG8"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(28.dp),
            color = JikanAccent.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = JikanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "JikanHub",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = JikanOnSurface
        )

        Text(
            text = "Versão 1.0.0",
            style = MaterialTheme.typography.bodyMedium,
            color = JikanOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Author Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = JikanSurfaceVariant,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = JikanAccent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Criado por",
                    style = MaterialTheme.typography.labelLarge,
                    color = JikanOnSurfaceVariant
                )
                Text(
                    text = "Henrique Viana",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = JikanOnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GitHub Link Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                    context.startActivity(intent)
                },
            shape = RoundedCornerShape(24.dp),
            color = JikanAccent.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, JikanAccent.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = JikanAccent
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Ver no GitHub",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = JikanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Policy Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://jikanhub.duckdns.org/privacy"))
                    context.startActivity(intent)
                },
            shape = RoundedCornerShape(24.dp),
            color = JikanOnSurface.copy(alpha = 0.05f),
            border = androidx.compose.foundation.BorderStroke(1.dp, JikanOnSurfaceVariant.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = JikanOnSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Política de Privacidade",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = JikanOnSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Focado em produtividade e design minimalista.",
            style = MaterialTheme.typography.bodySmall,
            color = JikanOnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}
