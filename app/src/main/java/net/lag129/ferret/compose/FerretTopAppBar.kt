package net.lag129.ferret.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.lag129.ferret.R
import net.lag129.ferret.Timeline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FerretTopAppBar(
    currentTimeline: Timeline,
    onSwitch: (Timeline) -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Box {
                Text(
                    when (currentTimeline) {
                        Timeline.HOME -> stringResource(R.string.home_timeline)
                        Timeline.LOCAL -> stringResource(R.string.local_timeline)
                        Timeline.FEDERATED -> stringResource(R.string.federated_timeline)
                    },
                    modifier = Modifier.clickable { isExpanded = true }
                )

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.home_timeline)) },
                        onClick = {
                            onSwitch(Timeline.HOME)
                            isExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.local_timeline)) },
                        onClick = {
                            onSwitch(Timeline.LOCAL)
                            isExpanded = false
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.federated_timeline)) },
                        onClick = {
                            onSwitch(Timeline.FEDERATED)
                            isExpanded = false
                        }
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "その他"
                )
            }
        },
        expandedHeight = 48.dp,
        modifier = modifier
    )
}
