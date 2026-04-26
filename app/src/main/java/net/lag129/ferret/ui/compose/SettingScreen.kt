package net.lag129.ferret.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import net.lag129.ferret.ui.theme.FerretTheme

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier
) {
    val libraries by produceLibraries()

    LibrariesContainer(
        libraries = libraries,
        showAuthor = false,
        showDescription = false,
        showVersion = false,
        showLicenseBadges = false,
        textStyles = LibraryDefaults.libraryTextStyles(
            nameTextStyle = TextStyle.Default.copy(fontSize = 16.sp)
        ),
        modifier = modifier.fillMaxSize()
    )
}

@Preview
@Composable
private fun SettingScreenPreview() {
    FerretTheme {
        SettingScreen()
    }
}
