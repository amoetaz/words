package com.moetaz.words.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moetaz.words.data.local.database.DatabaseInitializer
import com.moetaz.words.data.local.database.MIGRATION_1_2
import com.moetaz.words.data.local.database.MIGRATION_2_3
import com.moetaz.words.data.local.database.MIGRATION_3_4
import com.moetaz.words.data.local.database.WordDatabase
import com.moetaz.words.data.repository.WordRepositoryImpl
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.domain.usecase.AddWordUseCase
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import com.moetaz.words.domain.usecase.GetWordsUseCase
import com.moetaz.words.presentation.add.AddWordViewModel
import com.moetaz.words.presentation.detail.WordDetailViewModel
import com.moetaz.words.presentation.flashcards.FlashcardsViewModel
import com.moetaz.words.presentation.list.WordListViewModel
import com.moetaz.words.presentation.quiz.QuizViewModel
import com.moetaz.words.presentation.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        val context = androidContext()
        Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            "word_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val dao = get<WordDatabase>().wordDao()
                    CoroutineScope(Dispatchers.IO).launch {
                        DatabaseInitializer.populateIfEmpty(context, dao)
                    }
                }
            })
            .build()
    }
    single { get<WordDatabase>().wordDao() }
    single<WordRepository> { WordRepositoryImpl(get(), androidContext()) }
    single { GetWordsUseCase(get()) }
    single { GetWordByIdUseCase(get()) }
    single { AddWordUseCase(get()) }

    viewModel { WordListViewModel(get()) }
    viewModel { AddWordViewModel(get(), get()) }
    viewModel { WordDetailViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { FlashcardsViewModel(get()) }
    viewModel { QuizViewModel(get()) }
}
