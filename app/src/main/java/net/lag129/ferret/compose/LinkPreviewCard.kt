package net.lag129.ferret.compose

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import net.lag129.ferret.ui.theme.FerretTheme

@Composable
fun LinkPreviewCard(
    url: String,
    title: String,
    desc: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null
) {
    var isClicked by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.clickable {
                isClicked = true
            }
        ) {
            AsyncImage(
                model = imageUrl ?: "",
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .height(88.dp)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = url.toUri().host ?: url,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                )

                Spacer(modifier = Modifier.padding(2.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.padding(1.dp))

                Text(
                    text = desc,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
        }
    }

    if (isClicked) {
        val context = LocalContext.current
        val intent = remember { Intent(Intent.ACTION_VIEW, url.toUri()) }
        context.startActivity(intent)
    }
}

@Preview
@Composable
private fun LinkPreviewCardPreview() {
    FerretTheme {
        LinkPreviewCard(
            url = "https://www.example.com",
            title = "銀河鉄道の夜",
            desc = "いきなりこっちも窓から顔を引っ込めて地図を見ているときなどは思わずそう思いました。この男は、どこかぐあいが悪いようにそろそろと出て来て立ちました。カムパネルラの頬は、まるで一度に叫んで、そっちの方を見ましたが、急いで行きすぎようとしました。さわやかな秋の時計の盤面には、ぴかぴか青びかりを出す小さな虫もいて、その谷の底には川が明るく下にのぞけたのです。むかしのバルドラの野原に来たジョバンニはみんなのいるそっちの方へ倒れるようになりました。"
        )
    }
}
