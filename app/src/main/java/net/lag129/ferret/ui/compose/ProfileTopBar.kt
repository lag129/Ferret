package net.lag129.ferret.ui.compose

import android.icu.text.CompactDecimalFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import net.lag129.ferret.R
import net.lag129.ferret.ui.model.Profile

@Composable
fun ProfileTopBar(
    profile: Profile,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = profile.header,
            contentDescription = profile.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        )

        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            AsyncImage(
                model = profile.avatar,
                contentDescription = profile.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = (-40).dp)
                    .clip(RoundedCornerShape(30))
            )

            HtmlText(
                body = profile.displayName,
                emojis = profile.emojis,
                fontWeight = FontWeight.SemiBold,
                style = TextStyle(
                    fontSize = 24.sp
                ),
                modifier = Modifier.offset(y = (-8).dp)
            )

            Text(profile.acct)

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

                Text(stringResource(R.string.follower, formatter.format(profile.followersCount)))

                Text(stringResource(R.string.following, formatter.format(profile.followingCount)))

                Text(stringResource(R.string.posts, formatter.format(profile.statusesCount)))
            }

            HtmlText(
                body = profile.note,
                emojis = profile.emojis,
                fontWeight = FontWeight.Light,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineBreak = LineBreak.Paragraph
                )
            )
        }
    }
}
