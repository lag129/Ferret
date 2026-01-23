package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.lag129.ferret.api.entity.Account

@Composable
fun ProfileScreen(
    data: Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = data.header,
                contentDescription = data.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, top = 124.dp)
            ) {
                AsyncImage(
                    model = data.avatar,
                    contentDescription = data.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(72.dp)
                )
            }
        }

        Text(data.displayName)

        Text(data.acct)
    }
}
