package com.powerje.devdrawer.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.powerje.devdrawer.config.ConfigActivity
import com.powerje.devdrawer.data.DataStorePatternStorage
import com.powerje.devdrawer.data.PatternRepository
import com.powerje.devdrawer.matching.AppMatcher
import com.powerje.devdrawer.matching.InstalledApp
import com.powerje.devdrawer.matching.MatchedApp
import kotlinx.coroutines.flow.first

class DevDrawerWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val storage = DataStorePatternStorage(context)
        val repository = PatternRepository(storage)
        repository.initialize()

        val patterns = repository.getPatterns().first()
        val installedApps = getInstalledApps(context)
        val matchedApps = AppMatcher.match(installedApps, patterns)

        provideContent {
            GlanceTheme {
                WidgetContent(context, patterns.isEmpty(), matchedApps)
            }
        }
    }

    private fun getInstalledApps(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        return pm.getInstalledApplications(0)
            .map { appInfo ->
                InstalledApp(
                    packageName = appInfo.packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                )
            }
    }
}

@Composable
private fun WidgetContent(
    context: Context,
    patternsEmpty: Boolean,
    matchedApps: List<MatchedApp>,
) {
    Box(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface),
        contentAlignment = Alignment.Center,
    ) {
        when {
            patternsEmpty -> EmptyHint(context)
            matchedApps.isEmpty() -> NoMatches(context)
            else -> AppList(context, matchedApps)
        }
    }
}

@Composable
private fun EmptyHint(context: Context) {
    val configIntent =
        Intent(context, ConfigActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    Text(
        text = "Tap to add patterns\nlike com.example.*",
        style =
            TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 14.sp,
            ),
        modifier =
            GlanceModifier
                .padding(16.dp)
                .clickable(actionStartActivity(configIntent)),
    )
}

@Composable
private fun NoMatches(context: Context) {
    val configIntent =
        Intent(context, ConfigActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    Text(
        text = "No apps match your patterns",
        style =
            TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 14.sp,
            ),
        modifier =
            GlanceModifier
                .padding(16.dp)
                .clickable(actionStartActivity(configIntent)),
    )
}

@Composable
private fun AppList(
    context: Context,
    apps: List<MatchedApp>,
) {
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        items(apps, itemId = { it.packageName.hashCode().toLong() }) { app ->
            AppRow(context, app)
        }
    }
}

@Composable
private fun AppRow(
    context: Context,
    app: MatchedApp,
) {
    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
    val actionIntent = AppActionActivity.createIntent(context, app.packageName, app.appName)
    val icon = getAppIcon(context, app.packageName)

    Row(
        modifier =
            GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // App icon
        icon?.let {
            Image(
                provider = ImageProvider(it),
                contentDescription = app.appName,
                modifier =
                    GlanceModifier
                        .size(40.dp)
                        .padding(end = 12.dp),
            )
        }

        // Main content - tap to launch
        Column(
            modifier =
                GlanceModifier
                    .defaultWeight()
                    .then(
                        if (launchIntent != null) {
                            GlanceModifier.clickable(actionStartActivity(launchIntent))
                        } else {
                            GlanceModifier
                        },
                    ),
        ) {
            Text(
                text = app.appName,
                style =
                    TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 14.sp,
                    ),
            )
            Text(
                text = app.packageName,
                style =
                    TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 10.sp,
                    ),
            )
        }

        // Action button - tap for menu
        Text(
            text = "⋮",
            style =
                TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 20.sp,
                ),
            modifier =
                GlanceModifier
                    .padding(8.dp)
                    .clickable(actionStartActivity(actionIntent)),
        )
    }
}

private fun getAppIcon(
    context: Context,
    packageName: String,
): Bitmap? {
    return try {
        val drawable = context.packageManager.getApplicationIcon(packageName)
        if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bitmap =
                Bitmap.createBitmap(
                    drawable.intrinsicWidth.coerceAtLeast(1),
                    drawable.intrinsicHeight.coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888,
                )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}
