package com.autojournal

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.autojournal.ui.navigation.NavigationGraph
import com.autojournal.ui.theme.AutoJournalTheme
import com.autojournal.ui.viewmodels.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val openRefuel = intent?.getBooleanExtra("open_refuel", false) ?: false

        setContent {
            AutoJournalApp(
                openRefuel = openRefuel
            )
        }
    }
}

@Composable
fun AutoJournalApp(
    openRefuel: Boolean = false
) {
    AutoJournalTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel: DashboardViewModel = hiltViewModel()
            NavigationGraph(
                viewModel = viewModel,
                openRefuel = openRefuel
            )
        }
    }
}