package com.moetaz.words.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class WordWidgetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    override suspend fun doWork(): Result {
        WordGlanceWidget().updateAll(applicationContext)
        return Result.success()
    }
}
