package net.lag129.ferret.ui.compose

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import net.lag129.ferret.model.Reaction

@Composable
fun ReactionBar(
    reactions: ImmutableList<Reaction>,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .height(30.dp)
            .horizontalScroll(rememberScrollState()),
    ) {
        reactions.forEach { reaction ->
            ReactionButton(
                reaction = reaction,
                onClick = {}
            )

            Spacer(Modifier.width(10.dp))
        }
    }
}

@Composable
fun ReactionButton(
    reaction: Reaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(6.dp),
        enabled = true,
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = when (reaction.me) {
                true -> MaterialTheme.colorScheme.primaryContainer
                false -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = modifier
    ) {
        Row {
            if (reaction.url.isNullOrEmpty()) {
                Text(
                    text = reaction.name,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            } else {
                AsyncImage(
                    model = reaction.url,
                    contentDescription = reaction.name,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                reaction.count.toString(),
                fontSize = 14.sp,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}
