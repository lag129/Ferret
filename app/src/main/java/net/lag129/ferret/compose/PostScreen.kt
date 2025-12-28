package net.lag129.ferret.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.lag129.ferret.ui.theme.FerretTheme

@Composable
fun PostScreen(
    modifier: Modifier = Modifier
) {
    val inputValue = rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        FilledTonalButton(
            onClick = { },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Post")
        }

        Spacer(modifier = Modifier.padding(4.dp))

        TextField(
            value = inputValue.value,
            onValueChange = { inputValue.value = it },
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
fun PostScreenPreview() {
    FerretTheme {
        PostScreen()
    }
}
