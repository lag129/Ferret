package net.lag129.ferret.ui.screen.profile

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile
import net.lag129.ferret.ui.compose.OnBackTopAppBar
import net.lag129.ferret.ui.compose.PostCard
import net.lag129.ferret.ui.compose.ProfileTopBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.ProfileScreen(
    id: String,
    profile: Profile,
    onBack: () -> Unit,
    onClickDetail: (data: Post) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (profile: Profile) -> Unit,
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
                body = profile.displayName,
                emojis = profile.emojis,
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
                ProfileTopBar(profile)
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
