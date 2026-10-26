package net.lag129.ferret.ui.screen.profile

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.model.Account
import net.lag129.ferret.model.toPostEmoji
import net.lag129.ferret.ui.compose.OnBackTopAppBar
import net.lag129.ferret.ui.compose.PostCard
import net.lag129.ferret.ui.compose.ProfileTopBar
import net.lag129.ferret.ui.model.Post
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.ProfileScreen(
    id: String,
    account: Account,
    onBack: () -> Unit,
    onClickDetail: (data: Post) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    val statuses by profileViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(id) {
        profileViewModel.fetchAccountStatuses(id)
    }

    Scaffold(
        topBar = {
            OnBackTopAppBar(
                body = account.displayName,
                emojis = account.emojis.map { it.toPostEmoji() },
                onBack = onBack,
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            item {
                ProfileTopBar(account)
            }

            items(
                items = statuses,
                key = { status -> status.id }
            ) { status ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PostCard(
                        data = status,
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
                        profileViewModel.fetchNextAccountStatuses(id, maxId)
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
