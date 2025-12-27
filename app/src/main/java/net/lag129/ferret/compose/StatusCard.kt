package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.charlex.compose.htmltext.material3.HtmlText
import net.lag129.ferret.ui.theme.FerretTheme

@Composable
fun StatusCard(
    displayName: String,
    userName: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = displayName,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = userName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }

            SelectionContainer {
                HtmlText(text = content)
            }
        }
    }
}

@Preview
@Composable
fun StatusCardPreview() {
    FerretTheme() {
        StatusCard(
            displayName = "ユーザー",
            userName = "@user@example.com",
            content = "<p>ダミーテキスト<p>"
        )
    }
}
