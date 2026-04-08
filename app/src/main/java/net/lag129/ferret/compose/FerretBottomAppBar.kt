package net.lag129.ferret.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FerretBottomAppBar(
    modifier: Modifier = Modifier
) {

    BottomAppBar(
        modifier = modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            icon = { },
            label = { },
            selected = true,
            onClick = {}
        )

        NavigationBarItem(
            icon = { },
            label = { },
            selected = true,
            onClick = {}
        )

        NavigationBarItem(
            icon = { },
            label = { },
            selected = true,
            onClick = {}
        )

        NavigationBarItem(
            icon = { },
            label = { },
            selected = true,
            onClick = {}
        )
    }
}
