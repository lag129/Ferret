package net.lag129.ferret

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.lag129.ferret.compose.StatusCard
import net.lag129.ferret.ui.theme.FerretTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Napier.base(DebugAntilog())

        enableEdgeToEdge()
        setContent {
            FerretTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Filled.Edit, "Edit")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val viewModel: ViewModel by viewModels()

                    TimelineScreen(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineScreen(
    viewModel: ViewModel,
    modifier: Modifier = Modifier
) {
    val statuses by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = statuses,
            key = { status -> status.id }
        ) { status ->
            StatusCard(status.content)
        }
    }
}
