package net.lag129.ferret.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import net.lag129.ferret.R
import net.lag129.ferret.utils.RelativeTime

@Composable
fun RelativeTime.toText(): String {
    return when (this) {
        RelativeTime.Now -> stringResource(R.string.now)
        is RelativeTime.Minutes -> stringResource(R.string.minute_abbreviation, value.toString())
        is RelativeTime.Hours -> stringResource(R.string.hour_abbreviation, value.toString())
        is RelativeTime.Date -> text
    }
}
