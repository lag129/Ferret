package net.lag129.ferret.ui.screen.profile

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.ProfileTopBar
import net.lag129.ferret.ui.compose.StatusCard
import net.lag129.ferret.ui.compose.StatusCardData
import net.lag129.ferret.ui.compose.toStatusCardData
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.MyProfileScreen(
    onClickDetail: (data: StatusCardData) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: MyProfileViewModel = koinViewModel()
) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    val statuses by viewModel.statuses.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchMyStatuses()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            account?.let {
                item { ProfileTopBar(it) }
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
                    val maxId = statuses.last().id
                    LoadingIndicator(onFetchNext = {
                        viewModel.fetchNextStatusesById(maxId)
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
