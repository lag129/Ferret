package net.lag129.ferret.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.lag129.ferret.R
import net.lag129.ferret.viewmodel.Timeline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FerretTopAppBar(
    currentTimeline: Timeline,
    onSwitch: (Timeline) -> Unit,
    onClickSetting: () -> Unit,
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
            TextButton(onClick = onClickSetting) {
                Text("︙")
            }
        },
        expandedHeight = 48.dp,
        modifier = modifier
    )
}
