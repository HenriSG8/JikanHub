package com.jikanhub.app.presentation.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll

object WidgetUpdater {
    suspend fun updateAllWidgets(context: Context) {
        TasksWidget().updateAll(context)
    }
}
