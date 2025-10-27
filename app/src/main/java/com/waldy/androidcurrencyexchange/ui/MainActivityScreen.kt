package com.waldy.androidcurrencyexchange.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.waldy.androidcurrencyexchange.presentation.converter.CurrencyExchangeRoute
import com.waldy.androidcurrencyexchange.presentation.settings.SettingsRoute
import com.waldy.androidcurrencyexchange.ui.util.StringKeys
import com.waldy.androidcurrencyexchange.ui.util.t
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainActivityScreen() {
    val tabTitles = listOf(
        t(StringKeys.CURRENCY_CONVERTER),
        t(StringKeys.SETTINGS)
    )
    val pagerState = rememberPagerState { tabTitles.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            CustomTabRow(
                tabTitles = tabTitles,
                selectedTabIndex = pagerState.currentPage,
                onTabClick = { index ->
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> CurrencyExchangeRoute()
                    1 -> SettingsRoute(onNavigateUp = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    })
                }
            }
        }
    }
}

@Composable
private fun CustomTabRow(
    tabTitles: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabTitles.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onTabClick(index) }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
