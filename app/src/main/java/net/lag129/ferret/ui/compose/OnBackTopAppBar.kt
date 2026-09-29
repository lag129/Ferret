package net.lag129.ferret.ui.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import net.lag129.ferret.R
import net.lag129.ferret.model.CustomEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBackTopAppBar(
    body: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    emojis: ImmutableList<CustomEmoji>? = null,
) {
    TopAppBar(
        title = {
            HtmlText(
                body = body,
                emojis = emojis,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painterResource(R.drawable.arrow_left),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    )
}
