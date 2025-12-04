package com.powerje.devdrawer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.powerje.devdrawer.widget.DevDrawerWidget
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED,
            -> {
                refreshWidgets(context)
            }
        }
    }

    private fun refreshWidgets(context: Context) {
        MainScope().launch {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(DevDrawerWidget::class.java)
            glanceIds.forEach { glanceId ->
                DevDrawerWidget().update(context, glanceId)
            }
        }
    }
}
