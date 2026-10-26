package net.lag129.ferret.ui.screen.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import net.lag129.ferret.model.Account
import net.lag129.ferret.ui.compose.PostCard
import net.lag129.ferret.ui.model.Post
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.SearchScreen(
    onClickDetail: (data: Post) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = koinViewModel(),
) {
    var keyword by remember { mutableStateOf("") }
    val uiState by searchViewModel.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Row {
                OutlinedTextField(
                    value = keyword,
                    onValueChange = { keyword = it },
                    label = { Text("検索") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { searchViewModel.searchByKeyword(keyword) }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            uiState?.let {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(
                        items = it.statuses,
                    ) { status ->
                        PostCard(
                            data = status.toPost(),
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
                }
            }
        }
    }
}
