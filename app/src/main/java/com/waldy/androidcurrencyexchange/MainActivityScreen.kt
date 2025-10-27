package com.waldy.androidcurrencyexchange

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * This is the main UI container for your application.
 * It is a stateless Composable, meaning it doesn't create or manage its own data.
 */
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun MainActivityScreen() {
    val tabTitles = listOf("Exchange", "Ratios")

    val pagerState = rememberPagerState { tabTitles.size }
    val scope = rememberCoroutineScope()

    // Using Material3's recommended surface color for background
    val backgroundColor = MaterialTheme.colorScheme.surface

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor), // Apply a consistent background color
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Add padding around the content
        ) {
            // A custom, modern TabRow implementation
            CustomTabRow(
                tabTitles = tabTitles,
                selectedTabIndex = pagerState.currentPage,
                onTabClick = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CurrencyExchangeScreen()
                    1 -> CurrencyRatioScreen()
                }
            }
        }
    }
}

@Composable
fun CustomTabRow(
    tabTitles: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)) // Pill-shaped container
            .background(MaterialTheme.colorScheme.surfaceVariant) // A subtle background for the tabs
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index

                // Animate color changes for a smoother transition
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                    label = "tab_background_color"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                    label = "tab_content_color"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(backgroundColor)
                        .clickable(onClick = { onTabClick(index) }) // Use a standard clickable modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


// --- Placeholder Screens ---
// It's a good practice to center the placeholder content for a better preview experience.
@Composable
fun CurrencyExchangeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Currency Exchange", fontSize = 18.sp)
    }
}

@Composable
fun CurrencyRatioScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Content for Currency Ratio", fontSize = 18.sp)
    }
}
