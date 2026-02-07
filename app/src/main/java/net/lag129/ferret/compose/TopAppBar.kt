package net.lag129.ferret.compose

import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import net.lag129.ferret.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(SelectedTab.HOME) }

    TopAppBar(
        title = {
            Text(
                when (selectedTab) {
                    SelectedTab.HOME -> stringResource(R.string.home_timeline)
                    SelectedTab.LOCAL -> stringResource(R.string.local_timeline)
                    SelectedTab.FEDERATED -> stringResource(R.string.federated_timeline)
                },
                modifier = Modifier.clickable { isExpanded = true }
            )
        },
        actions = {
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.home_timeline)) },
                    onClick = {
                        selectedTab = SelectedTab.HOME
                        isExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.local_timeline)) },
                    onClick = {
                        selectedTab = SelectedTab.LOCAL
                        isExpanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.federated_timeline)) },
                    onClick = {
                        selectedTab = SelectedTab.FEDERATED
                        isExpanded = false
                    }
                )
            }
        },
        modifier = modifier
    )
}

private enum class SelectedTab {
    HOME,
    LOCAL,
    FEDERATED
}
