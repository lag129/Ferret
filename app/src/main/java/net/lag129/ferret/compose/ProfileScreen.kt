package net.lag129.ferret.compose

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import net.lag129.ferret.ProfileViewModel
import net.lag129.ferret.api.entity.Account

@Composable
fun SharedTransitionScope.ProfileScreen(
    data: Account,
    viewModel: ProfileViewModel,
    navigateToProfileScreen: (account: Account) -> Unit,
    navigateToMediaScreen: (mediaUrl: String, description: String?) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val statuses by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(data.id) {
        viewModel.fetchAccountStatuses(data.id)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        item {
            ProfileTopBar(data)
        }

        stickyHeader {
            Text("sticky header")
        }

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
                            displayNameEmojis = targetStatus.account.emojis,
                            emojis = targetStatus.emojis,
                            mediaAttachments = targetStatus.mediaAttachments,
                            sensitive = targetStatus.sensitive,
                            spoilerText = targetStatus.spoilerText
                        )
                    }
                }

                StatusCard(
                    data = statusData,
                    onMediaClick = navigateToMediaScreen,
                    onProfileClick = navigateToProfileScreen,
                    animatedVisibilityScope = animatedVisibilityScope
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
                LoadingIndicator(
                    viewModel = viewModel,
                    accountId = statuses.last().account.id,
                    maxId = statuses.last().id
                )
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    data: Account,
    modifier: Modifier = Modifier
) {
    Column {
        AsyncImage(
            model = data.header,
            contentDescription = data.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        )

        AsyncImage(
            model = data.avatar,
            contentDescription = data.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .offset(y = (-40).dp)
                .clip(RoundedCornerShape(30))
        )

        HtmlText(
            body = data.displayName,
            emojis = data.emojis,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(
                fontSize = 24.sp
            ),
            modifier = Modifier.offset(y = (-8).dp)
        )

        Text(data.acct)

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${data.followersCount} フォロワー")

            Text("${data.followingCount} フォロー中")

            Text("${data.statusesCount} 投稿")
        }

        HtmlText(
            body = data.note,
            emojis = data.emojis,
            fontWeight = FontWeight.Light,
            style = TextStyle(
                fontSize = 16.sp,
                lineBreak = LineBreak.Paragraph
            )
        )
    }
}

@Composable
private fun LoadingIndicator(
    viewModel: ProfileViewModel,
    accountId: String,
    maxId: String,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    )

    LaunchedEffect(Unit) {
        viewModel.fetchNextAccountStatuses(
            accountId = accountId,
            maxId = maxId
        )
    }
}
