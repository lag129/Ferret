package net.lag129.ferret.ui.screen.timeline

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.FerretTopAppBar
import net.lag129.ferret.ui.compose.PostCard
import net.lag129.ferret.ui.model.Post
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.TimelineScreen(
    onClickDetail: (data: Post) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    onClickSetting: () -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
    timelineViewModel: TimelineViewModel = koinViewModel()
) {
    val posts by timelineViewModel.uiState.collectAsStateWithLifecycle()
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
                    items = posts,
                ) { post ->
                    PostCard(
                        data = post,
                        onClickDetail = onClickDetail,
                        onClickMedia = onClickMedia,
                        onClickProfile = onClickProfile,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        thickness = 0.2.dp
                    )
                }

                val isLast = posts.isEmpty()

                if (isLast.not()) {
                    item {
                        val maxId = posts.last().id
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
