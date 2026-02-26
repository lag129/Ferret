package net.lag129.ferret.compose

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import net.lag129.ferret.TimelineViewModel
import net.lag129.ferret.api.entity.Account

@Composable
fun SharedTransitionScope.TimelineScreen(
    viewModel: TimelineViewModel,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val statuses by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshTimeline() },
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(
                items = statuses,
                key = { status -> status.id }
            ) { status ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val statusData = remember(status.id) {
                        (status.reblog ?: status).let { targetStatus ->
                            StatusCardData(
                                displayName = targetStatus.account.displayName,
                                userName = targetStatus.account.acct,
                                createdAt = targetStatus.createdAt,
                                avatarUrl = targetStatus.account.avatar,
                                content = targetStatus.content,
                                account = targetStatus.account,
                                card = targetStatus.card,
                                displayNameEmojis = targetStatus.account.emojis.toImmutableList(),
                                emojis = targetStatus.emojis.toImmutableList(),
                                mediaAttachments = targetStatus.mediaAttachments.toImmutableList(),
                                sensitive = targetStatus.sensitive,
                                spoilerText = targetStatus.spoilerText
                            )
                        }
                    }

                    StatusCard(
                        data = statusData,
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
                        viewModel.fetchNextTimeline(maxId)
                    })
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
