package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.charlex.compose.htmltext.material3.HtmlText

@Composable
fun StatusCard(
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(12.dp)
    ) {
        SelectionContainer {
            HtmlText(text = content)
        }
    }
}

@Preview
@Composable
fun StatusCardPreview() {
    StatusCard(
        content = "ダミーテキスト"
    )
}
