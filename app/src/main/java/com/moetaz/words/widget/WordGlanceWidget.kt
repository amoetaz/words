package com.moetaz.words.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.moetaz.words.MainActivity
import com.moetaz.words.data.local.dao.WordDao
import com.moetaz.words.data.local.entity.WordEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class WordGlanceWidget : GlanceAppWidget(), KoinComponent {

    private val wordDao: WordDao by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val allWords = wordDao.getAllWords()
        val dayIndex = (System.currentTimeMillis() / (1000 * 60 * 60 * 24)).toInt()
        val startIndex = if (allWords.isNotEmpty()) (dayIndex * 3) % allWords.size else 0
        
        val wordsToShow = mutableListOf<WordEntity>()
        if (allWords.isNotEmpty()) {
            for (i in 0 until 3) {
                val index = (startIndex + i) % allWords.size
                wordsToShow.add(allWords[index])
            }
        }

        provideContent {
            GlanceTheme {
                WidgetContent(wordsToShow)
            }
        }
    }

    @Composable
    private fun WidgetContent(words: List<WordEntity>) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Daily Words",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            if (words.isEmpty()) {
                Text(text = "No words added yet", style = TextStyle(fontSize = 16.sp))
            } else {
                words.forEach { wordEntity ->
                    WordItem(wordEntity)
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }
            }
        }
    }

    @Composable
    private fun WordItem(word: WordEntity) {
        Column(modifier = GlanceModifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text(
                text = word.word,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = GlanceTheme.colors.onSurface
                )
            )
            Text(
                text = word.translations.joinToString(", "),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.secondary
                )
            )
        }
    }
}

class WordGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WordGlanceWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        scheduleDailyUpdate(context)
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        scheduleDailyUpdate(context)
    }

    private fun scheduleDailyUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<WordWidgetWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_word_widget_update",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        
        // Trigger immediate update
        val immediateWork = OneTimeWorkRequestBuilder<WordWidgetWorker>().build()
        WorkManager.getInstance(context).enqueue(immediateWork)
    }
}
