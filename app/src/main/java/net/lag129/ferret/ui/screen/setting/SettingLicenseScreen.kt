package net.lag129.ferret.ui.screen.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import net.lag129.ferret.R
import net.lag129.ferret.ui.compose.OnBackTopAppBar

@Composable
fun SettingLicenseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val libraries by produceLibraries()

    Scaffold(
        topBar = {
            OnBackTopAppBar(
                body = stringResource(id = R.string.ferret_setting_source_license),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        LibrariesContainer(
            libraries = libraries,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
