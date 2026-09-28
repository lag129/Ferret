package net.lag129.ferret.ui.compose

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import net.lag129.ferret.R
import net.lag129.ferret.model.Account
import net.lag129.ferret.utils.DateUtils
import org.koin.compose.koinInject
import kotlin.time.Instant

@Composable
fun SharedTransitionScope.DetailScreen(
    data: StatusCardData,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val dateUtils: DateUtils = koinInject()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickProfile(data.account) }
            ) {
                AsyncImage(
                    model = data.avatarUrl,
                    contentDescription = data.displayName,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(30))
                )

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    HtmlText(
                        body = data.displayName,
                        emojis = data.displayNameEmojis,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )

                    Text(
                        text = "@${data.userName}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SelectionContainer {
                HtmlText(
                    body = data.content,
                    emojis = data.emojis,
                )
            }

            if (!data.mediaAttachments.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                if (data.sensitive) {
                    var isBlurred by remember { mutableStateOf(true) }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MediaAttachmentCard(
                            mediaAttachments = data.mediaAttachments,
                            onClickMedia = onClickMedia,
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .blur(radius = if (isBlurred) 40.dp else 0.dp)
                        )

                        if (isBlurred) {
                            Text(
                                text = stringResource(R.string.sensitive),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                } else {
                    MediaAttachmentCard(
                        mediaAttachments = data.mediaAttachments,
                        onClickMedia = onClickMedia,
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }

            if (data.card != null) {
                Spacer(modifier = Modifier.height(8.dp))

                LinkPreviewCard(
                    url = data.card.url,
                    imageUrl = data.card.image,
                    title = data.card.title,
                    desc = data.card.description,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (data.reactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                ReactionBar(
                    reactions = data.reactions,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val time = Instant.parse(data.createdAt).toEpochMilliseconds()

            Text(
                text = dateUtils.formatDateTime(time),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        }
    }
}
