package com.powerje.devdrawer.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AppActionActivity : ComponentActivity() {
    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_APP_NAME = "app_name"

        fun createIntent(
            context: Context,
            packageName: String,
            appName: String,
        ): Intent {
            return Intent(context, AppActionActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packageName =
            intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: run {
                finish()
                return
            }
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: packageName

        setContent {
            MaterialTheme {
                ActionDialog(
                    appName = appName,
                    packageName = packageName,
                    onLaunch = { launchApp(packageName) },
                    onUninstall = { uninstallApp(packageName) },
                    onAppInfo = { openAppInfo(packageName) },
                    onStorePage = { openStorePage(packageName) },
                    onCopyPackage = { copyPackageName(packageName) },
                    onDismiss = { finish() },
                )
            }
        }
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            startActivity(intent)
        }
        finish()
    }

    private fun uninstallApp(packageName: String) {
        val intent =
            Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
            }
        startActivity(intent)
        finish()
    }

    private fun openAppInfo(packageName: String) {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
            }
        startActivity(intent)
        finish()
    }

    private fun copyPackageName(packageName: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Package Name", packageName))
        Toast.makeText(this, "Copied: $packageName", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun openStorePage(packageName: String) {
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
            }
        startActivity(intent)
        finish()
    }
}

@Composable
fun ActionDialog(
    appName: String,
    packageName: String,
    onLaunch: () -> Unit,
    onUninstall: () -> Unit,
    onAppInfo: () -> Unit,
    onStorePage: () -> Unit,
    onCopyPackage: () -> Unit,
    onDismiss: () -> Unit,
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = onLaunch,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Launch")
                    }
                    Button(
                        onClick = onUninstall,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Uninstall")
                    }
                    Button(
                        onClick = onAppInfo,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("App Info")
                    }
                    Button(
                        onClick = onStorePage,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Play Store")
                    }
                    Button(
                        onClick = onCopyPackage,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Copy Package Name")
                    }
                }
            }
        }
    }
}
