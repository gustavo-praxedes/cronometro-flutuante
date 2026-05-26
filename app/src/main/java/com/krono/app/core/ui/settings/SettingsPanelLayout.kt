package com.krono.app.core.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.krono.app.core.ui.components.ScrollFadeContainer
import com.krono.app.core.ui.theme.KronoTokens

@Composable
fun SettingsPanelLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    ScrollFadeContainer(
        canScrollForward = scrollState.canScrollForward,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = KronoTokens.Settings.panelHorizontalInset),
            verticalArrangement = Arrangement.spacedBy(KronoTokens.Settings.panelSectionGap)
        ) {
            Spacer(Modifier.height(KronoTokens.Settings.panelTopSpacing))
            content()
            Spacer(Modifier.height(KronoTokens.Settings.panelBottomSpacing))
        }
    }
}
