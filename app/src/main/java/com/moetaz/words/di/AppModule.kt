package com.moetaz.words.di

import androidx.room.Room
import com.moetaz.words.data.local.database.WordDatabase
import com.moetaz.words.data.repository.WordRepositoryImpl
import com.moetaz.words.domain.repository.WordRepository
import com.moetaz.words.domain.usecase.AddWordUseCase
import com.moetaz.words.domain.usecase.GetWordByIdUseCase
import com.moetaz.words.domain.usecase.GetWordsUseCase
import com.moetaz.words.presentation.add.AddWordViewModel
import com.moetaz.words.presentation.detail.WordDetailViewModel
import com.moetaz.words.presentation.list.WordListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            WordDatabase::class.java,
            "word_db"
        ).build()
    }
    single { get<WordDatabase>().wordDao() }
    single<WordRepository> { WordRepositoryImpl(get()) }
    single { GetWordsUseCase(get()) }
    single { GetWordByIdUseCase(get()) }
    single { AddWordUseCase(get()) }

    viewModel { WordListViewModel(get()) }
    viewModel { AddWordViewModel(get()) }
    viewModel { WordDetailViewModel(get()) }
}
