package net.lag129.ferret.ui.compose

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.lag129.ferret.model.Account

@Composable
fun SharedTransitionScope.DetailScreen(
    data: StatusCardData,
    onClickMedia: (mediaUrl: String, description: String?) -> Unit,
    onClickProfile: (account: Account) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {

    StatusCard(
        data = data,
        onClickDetail = {},
        onClickMedia = onClickMedia,
        onClickProfile = onClickProfile,
        animatedVisibilityScope = animatedVisibilityScope,
        modifier = modifier.padding(start = 12.dp, end = 12.dp)
    )
}
