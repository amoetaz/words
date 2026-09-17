package com.moetaz.words

import android.app.Application
import com.moetaz.words.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WordsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WordsApplication)
            modules(appModule)
        }
    }
}
