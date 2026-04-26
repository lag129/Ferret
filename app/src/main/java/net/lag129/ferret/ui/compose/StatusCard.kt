package net.lag129.ferret.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import net.lag129.ferret.R
import net.lag129.ferret.model.Account
import net.lag129.ferret.model.Attachment
import net.lag129.ferret.model.CustomEmoji
import net.lag129.ferret.model.PreviewCard
import net.lag129.ferret.model.Status
import net.lag129.ferret.utils.DateUtils
import org.koin.compose.koinInject
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
@Serializable
data class StatusCardData(
    val displayName: String,
    val userName: String,
    val avatarUrl: String,
    val createdAt: String,
    val content: String,
    val account: Account,
    val card: PreviewCard? = null,
    val displayNameEmojis: ImmutableList<CustomEmoji>? = null,
    val emojis: ImmutableList<CustomEmoji>? = null,
    val mediaAttachments: ImmutableList<Attachment>? = null,
    val sensitive: Boolean,
    val spoilerText: String
)

fun Status.toStatusCardData(): StatusCardData {
    val target = this.reblog ?: this
    return StatusCardData(
        displayName = target.account.displayName,
        userName = target.account.acct,
        createdAt = target.createdAt,
        avatarUrl = target.account.avatar,
        content = target.content,
        account = target.account,
        card = target.card,
        displayNameEmojis = target.account.emojis.toImmutableList(),
        emojis = target.emojis.toImmutableList(),
        mediaAttachments = target.mediaAttachments.toImmutableList(),
        sensitive = target.sensitive,
        spoilerText = target.spoilerText
    )
}

@Composable
fun SharedTransitionScope.StatusCard(
    data: StatusCardData,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onClickMedia: ((mediaUrl: String, description: String?) -> Unit)? = null,
    onClickProfile: ((account: Account) -> Unit)? = null
) {
    val currentTime = Clock.System.now().toEpochMilliseconds()
    val dateUtils: DateUtils = koinInject()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        AsyncImage(
            model = data.avatarUrl,
            contentDescription = data.displayName,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(30))
                .clickable {
                    onClickProfile?.invoke(data.account)
                }
        )

        Spacer(modifier = Modifier.padding(6.dp))

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    HtmlText(
                        body = data.displayName,
                        emojis = data.displayNameEmojis,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .alignByBaseline()
                            .clickable {
                                onClickProfile?.invoke(data.account)
                            }
                    )

                    Spacer(modifier = Modifier.padding(4.dp))

                    Text(
                        text = "@${data.userName}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.alignByBaseline()
                    )
                }

                val postedTime = Instant.parse(data.createdAt).toEpochMilliseconds()

                Text(
                    text = dateUtils.getRelativeTimeSpanString(currentTime, postedTime),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline()
                )
            }

            var isSpoilerTextClicked by remember { mutableStateOf(false) }

            if (data.spoilerText.isNotBlank()) {
                Text(
                    text = data.spoilerText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(2.dp)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .clickable { isSpoilerTextClicked = !isSpoilerTextClicked }
                )
            }

            AnimatedVisibility(
                visible = data.spoilerText.isBlank() || isSpoilerTextClicked,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                HtmlText(
                    body = data.content,
                    emojis = data.emojis,
                    fontWeight = FontWeight.Light,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineBreak = LineBreak.Paragraph
                    ),
                    modifier = Modifier
                )
            }

            if (!data.mediaAttachments.isNullOrEmpty()) {
                Spacer(modifier = Modifier.padding(8.dp))

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
                Spacer(modifier = Modifier.padding(8.dp))

                LinkPreviewCard(
                    url = data.card.url,
                    imageUrl = data.card.image,
                    title = data.card.title,
                    desc = data.card.description,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
