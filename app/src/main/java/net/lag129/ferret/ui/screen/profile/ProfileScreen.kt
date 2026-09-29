package net.lag129.ferret.ui.screen.profile

import android.icu.text.CompactDecimalFormat
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.toImmutableList
import net.lag129.ferret.R
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.ProfileScreen(
    id: String,
    onClickDetail: (data: StatusCardData) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    onClickBottomAppBar: (bottomAppBarItem: BottomAppBarItem) -> Unit,
    selectedItem: BottomAppBarItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = koinViewModel(),
    account: Account? = null
) {
    val statuses by profileViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        profileViewModel.fetchAccountStatuses(id)
    }

    Scaffold(
        bottomBar = {
            FerretBottomAppBar(
                selected = selectedItem,
                onClick = onClickBottomAppBar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (account == null) {
                item {
                    ProfileTopBar(statuses.firstOrNull()?.account ?: return@item)
                }
            } else {
                item {
                    ProfileTopBar(account)
                }
            }

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
                    val accountId = statuses.last().account.id
                    val maxId = statuses.last().id
                    LoadingIndicator(onFetchNext = {
                        profileViewModel.fetchNextAccountStatuses(accountId, maxId)
                    })
                }
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    account: Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = account.header,
            contentDescription = account.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        )

        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            AsyncImage(
                model = account.avatar,
                contentDescription = account.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = (-40).dp)
                    .clip(RoundedCornerShape(30))
            )

            HtmlText(
                body = account.displayName,
                emojis = account.emojis.toImmutableList(),
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(
                    fontSize = 24.sp
                ),
                modifier = Modifier.offset(y = (-8).dp)
            )

            Text(account.acct)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val locale = LocalLocale.current.platformLocale
                val formatter = remember(locale) {
                    CompactDecimalFormat.getInstance(
                        locale,
                        CompactDecimalFormat.CompactStyle.SHORT
                    )
                }

                Text(stringResource(R.string.follower, formatter.format(account.followersCount)))

                Text(stringResource(R.string.following, formatter.format(account.followingCount)))

                Text(stringResource(R.string.posts, formatter.format(account.statusesCount)))
            }

            HtmlText(
                body = account.note,
                emojis = account.emojis.toImmutableList(),
                fontWeight = FontWeight.Light,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineBreak = LineBreak.Paragraph
                )
            )
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
