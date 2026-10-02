package net.lag129.ferret.ui.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import net.lag129.ferret.R
import net.lag129.ferret.ui.compose.OnBackTopAppBar
import net.lag129.ferret.ui.theme.FerretTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBack: () -> Unit,
    onClickSettingAbout: () -> Unit,
    onClickSettingLicense: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            OnBackTopAppBar(
                body = stringResource(id = R.string.ferret_setting),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.ferret_setting_about)) },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.info),
                        contentDescription = ""
                    )
                },
                modifier = Modifier.clickable { onClickSettingAbout() }
            )

            ListItem(
                headlineContent = { Text(stringResource(id = R.string.ferret_setting_source_license)) },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.signature),
                        contentDescription = ""
                    )
                },
                modifier = Modifier.clickable { onClickSettingLicense() }
            )
        }
    }
}

@Preview
@Composable
private fun SettingScreenPreview() {
    FerretTheme {
        SettingScreen(
            onBack = {},
            onClickSettingAbout = {},
            onClickSettingLicense = {}
        )
    }
}
