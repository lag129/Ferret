package net.lag129.ferret.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.lag129.ferret.BuildConfig
import net.lag129.ferret.R
import net.lag129.ferret.ui.compose.OnBackTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingAboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            OnBackTopAppBar(
                body = stringResource(id = R.string.ferret_setting_about),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
        ) {
            ListItem(
                headlineContent = { Text("Ferret  Ver. ${BuildConfig.VERSION_NAME}") },
            )
        }
    }
}
