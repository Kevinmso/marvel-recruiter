package com.marvel.recruiter.di

import androidx.room.Room
import com.marvel.recruiter.BuildConfig
import com.marvel.recruiter.data.local.AppDatabase
import com.marvel.recruiter.data.local.InitialStateCallback
import com.marvel.recruiter.data.remote.comicVineHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.NAME)
            .addCallback(InitialStateCallback)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    single { get<AppDatabase>().characterDao() }
    single { get<AppDatabase>().storyArcDao() }
    single { get<AppDatabase>().gameStateDao() }
    single { get<AppDatabase>().userRosterDao() }
    single { get<AppDatabase>().missionResultDao() }
    single { get<AppDatabase>().seedMetaDao() }

    single { comicVineHttpClient(apiKey = BuildConfig.COMIC_VINE_API_KEY) }
}
