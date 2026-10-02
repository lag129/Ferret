package net.lag129.ferret.ui.screen.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import net.lag129.ferret.R
import net.lag129.ferret.ui.compose.OnBackTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
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
            showAuthor = false,
            showDescription = false,
            showVersion = false,
            showLicenseBadges = false,
            textStyles = LibraryDefaults.libraryTextStyles(
                nameTextStyle = TextStyle.Default.copy(fontSize = 16.sp)
            ),
            modifier = modifier.padding(innerPadding)
        )
    }
}
