package com.powerje.devdrawer.config

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.powerje.devdrawer.data.Pattern
import com.powerje.devdrawer.widget.DevDrawerWidget
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ConfigActivity : ComponentActivity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get widget ID if this is a configuration callback
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Handle back press to finish with result
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishWithResult()
                }
            },
        )

        setContent {
            MaterialTheme {
                ConfigScreen(
                    onFinish = { finishWithResult() },
                )
            }
        }
    }

    private fun finishWithResult() {
        // Update widget
        MainScope().launch {
            val manager = GlanceAppWidgetManager(this@ConfigActivity)
            val glanceIds = manager.getGlanceIds(DevDrawerWidget::class.java)
            glanceIds.forEach { glanceId ->
                DevDrawerWidget().update(this@ConfigActivity, glanceId)
            }
        }

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
        }
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    viewModel: ConfigViewModel = viewModel(),
    @Suppress("UnusedParameter") onFinish: () -> Unit,
) {
    val patterns by viewModel.patterns.collectAsState()
    val editingPattern by viewModel.editingPattern.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("DevDrawer Patterns") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addPattern() }) {
                Icon(Icons.Default.Add, contentDescription = "Add pattern")
            }
        },
    ) { padding ->
        if (patterns.isEmpty()) {
            EmptyState(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
            )
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(patterns) { index, pattern ->
                    PatternCard(
                        pattern = pattern,
                        onEdit = { viewModel.editPattern(index) },
                        onDelete = { viewModel.deletePattern(index) },
                    )
                }
            }
        }
    }

    editingPattern?.let { editing ->
        PatternDialog(
            initialLabel = editing.label,
            initialRegex = editing.regex,
            isNew = editing.index == null,
            onDismiss = { viewModel.dismissDialog() },
            onSave = { label, regex -> viewModel.savePattern(label, regex) },
        )
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No patterns yet",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add a pattern like com.mycompany.*\nto match your dev apps",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun PatternCard(
    pattern: Pattern,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pattern.label,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = pattern.regex,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun PatternDialog(
    initialLabel: String,
    initialRegex: String,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Boolean,
) {
    var label by remember { mutableStateOf(initialLabel) }
    var regex by remember { mutableStateOf(initialRegex) }
    var regexError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Add Pattern" else "Edit Pattern") },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = regex,
                    onValueChange = {
                        regex = it
                        regexError = null
                    },
                    label = { Text("Regex Pattern") },
                    singleLine = true,
                    isError = regexError != null,
                    supportingText = regexError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!onSave(label, regex)) {
                        regexError = "Invalid regex pattern"
                    }
                },
                enabled = label.isNotBlank() && regex.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
