package net.lag129.ferret.ui.screen.search

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import net.lag129.ferret.model.Post
import net.lag129.ferret.model.Profile
import net.lag129.ferret.model.converter.MastodonConverter.toPost
import net.lag129.ferret.ui.compose.PostCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.SearchScreen(
    onClickDetail: (data: Post) -> Unit,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (profile: Profile) -> Unit,
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
