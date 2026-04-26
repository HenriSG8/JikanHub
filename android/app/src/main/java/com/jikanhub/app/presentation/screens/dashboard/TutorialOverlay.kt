package com.jikanhub.app.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jikanhub.app.R
import com.jikanhub.app.presentation.theme.JikanAccent
import com.jikanhub.app.presentation.theme.JikanOnSurface
import com.jikanhub.app.presentation.theme.JikanSurface

@Composable
fun TutorialOverlay(
    step: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val tutorialStep = TutorialStep.entries[step]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        // --- Skip Button (Top Right) ---
        TextButton(
            onClick = onSkip,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.tutorial_skip),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- Content Area ---
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = tutorialStep,
                transitionSpec = {
                    fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut() + slideOutVertically { -it / 2 }
                },
                label = "TutorialContent"
            ) { targetStep ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (targetStep) {
                        TutorialStep.WELCOME -> {
                            WelcomeStep(targetStep)
                        }
                        TutorialStep.TASKS -> {
                            ArrowPointer(Alignment.Center, stringResource(R.string.tutorial_tasks_pointer), Icons.Default.ArrowDownward)
                            StepInfo(targetStep, Modifier.align(Alignment.Center).padding(top = 100.dp))
                        }
                        TutorialStep.ADD_TASK -> {
                            ArrowPointer(Alignment.BottomEnd, stringResource(R.string.tutorial_add_task_pointer), Icons.Default.ArrowForward, Modifier.padding(bottom = 90.dp, end = 20.dp).rotate(45f))
                            StepInfo(targetStep, Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp))
                        }
                        TutorialStep.MENU -> {
                            ArrowPointer(Alignment.TopStart, stringResource(R.string.tutorial_menu_pointer), Icons.Default.ArrowUpward, Modifier.padding(top = 60.dp, start = 20.dp).rotate(-45f))
                            StepInfo(targetStep, Modifier.align(Alignment.CenterStart).padding(start = 40.dp, top = 80.dp))
                        }
                        TutorialStep.THEME -> {
                            ArrowPointer(Alignment.TopEnd, stringResource(R.string.tutorial_theme_pointer), Icons.Default.ArrowUpward, Modifier.padding(top = 60.dp, end = 60.dp).rotate(45f))
                            StepInfo(targetStep, Modifier.align(Alignment.TopCenter).padding(top = 150.dp))
                        }
                    }
                }
            }
        }

        // --- Next Button (Bottom Right) ---
        Button(
            onClick = onNext,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JikanAccent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (step == TutorialStep.entries.size - 1) stringResource(R.string.tutorial_finish) else stringResource(R.string.tutorial_next),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (step < TutorialStep.entries.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(step: TutorialStep) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(32.dp),
            color = JikanAccent,
            tonalElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "J",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(step.titleRes),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(step.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun StepInfo(step: TutorialStep, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(step.titleRes),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(step.descriptionRes),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ArrowPointer(
    alignment: Alignment,
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(
            modifier = modifier.align(alignment),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = JikanAccent,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = text,
                color = JikanAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
