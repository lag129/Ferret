package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lag129.ferret.TimelineViewModel

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onNavigate: (mediaUrl: String, description: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(
            items = statuses,
            key = { status -> status.id }
        ) { status ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusCard(
                    data = StatusCardData(
                        displayName = status.account.displayName,
                        userName = status.account.acct,
                        createdAt = status.createdAt,
                        avatarUrl = status.account.avatar,
                        content = status.content,
                        card = status.card,
                        displayNameEmojis = status.account.emojis,
                        emojis = status.emojis,
                        mediaAttachments = status.mediaAttachments,
                        spoilerText = status.spoilerText
                    ),
                    onMediaClick = onNavigate
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
                LoadingIndicator(viewModel, statuses.last().id)
            }
        }
    }
}

@Composable
fun LoadingIndicator(
    viewModel: TimelineViewModel,
    maxId: String,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    )

    LaunchedEffect(Unit) {
        viewModel.fetchNextHomeTimeline(maxId)
    }
}
