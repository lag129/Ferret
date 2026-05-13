package net.lag129.ferret.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.lag129.ferret.R

enum class BottomAppBarItem {
    HOME, PROFILE
}

@Composable
fun FerretBottomAppBar(
    selected: BottomAppBarItem,
    onClick: (BottomAppBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    BottomAppBar(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(R.drawable.house_duotone),
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { },
            selected = selected == BottomAppBarItem.HOME,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick(BottomAppBarItem.HOME)
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(R.drawable.user_circle_duotone),
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { },
            selected = selected == BottomAppBarItem.PROFILE,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick(BottomAppBarItem.PROFILE)
            }
        )
    }
}
