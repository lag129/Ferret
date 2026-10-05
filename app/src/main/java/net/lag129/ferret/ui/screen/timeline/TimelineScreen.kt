package net.lag129.ferret.ui.screen.timeline

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.FerretTopAppBar
import net.lag129.ferret.ui.compose.StatusCard
import net.lag129.ferret.ui.compose.StatusCardData
import net.lag129.ferret.ui.compose.toStatusCardData
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.TimelineScreen(
    onClickDetail: (data: StatusCardData) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    onClickSetting: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    timelineViewModel: TimelineViewModel = koinViewModel()
) {
    val statuses by timelineViewModel.uiState.collectAsStateWithLifecycle()
    val currentTimeline by timelineViewModel.currentTimeline.collectAsStateWithLifecycle()
    val isRefreshing by timelineViewModel.isRefreshing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FerretTopAppBar(
                currentTimeline = currentTimeline,
                onSwitch = { timelineViewModel.switchTimeline(it) },
                onClickSetting = onClickSetting
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { timelineViewModel.refreshTimeline() },
            modifier = modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = statuses,
                    key = { status -> status.id }
                ) { status ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val statusCardData = remember(status) { status.toStatusCardData() }

                        StatusCard(
                            data = statusCardData,
                            onClickDetail = onClickDetail,
                            onClickMedia = onClickMedia,
                            onClickProfile = onClickProfile,
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            thickness = 0.2.dp
                        )
                    }
                }

                val isLast = statuses.isEmpty()

                if (isLast.not()) {
                    item {
                        val maxId = statuses.last().id
                        LoadingIndicator(onFetchNext = {
                            timelineViewModel.fetchNextTimeline(maxId)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    onFetchNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    )

    LaunchedEffect(Unit) {
        onFetchNext()
    }
}
