package com.marvel.recruiter.di

import androidx.room.Room
import com.marvel.recruiter.BuildConfig
import com.marvel.recruiter.data.local.AppDatabase
import com.marvel.recruiter.data.local.InitialStateCallback
import com.marvel.recruiter.data.remote.ComicVineApi
import com.marvel.recruiter.data.remote.comicVineHttpClient
import com.marvel.recruiter.data.repository.GameRepository
import com.marvel.recruiter.data.sync.RosterFetcher
import com.marvel.recruiter.data.sync.RosterSeeder
import com.marvel.recruiter.viewmodel.HeroProfileViewModel
import com.marvel.recruiter.viewmodel.ArcStoryViewModel
import com.marvel.recruiter.viewmodel.MissionResultViewModel
import com.marvel.recruiter.viewmodel.MissionsViewModel
import com.marvel.recruiter.viewmodel.PokedexViewModel
import com.marvel.recruiter.viewmodel.RosterViewModel
import com.marvel.recruiter.viewmodel.SeedViewModel
import com.marvel.recruiter.viewmodel.ShopViewModel
import com.marvel.recruiter.viewmodel.SquadViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.random.Random

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
    single { get<AppDatabase>().shopDao() }

    single { comicVineHttpClient(apiKey = BuildConfig.COMIC_VINE_API_KEY) }
    single { ComicVineApi(get()) }
    single {
        RosterFetcher(readSnapshot = {
            androidContext().assets.open("roster_snapshot.json").bufferedReader().use { it.readText() }
        })
    }
    single { RosterSeeder(fetcher = get(), db = get()) }
    single { GameRepository(get()) }

    // Fonte de aleatoriedade injetável (constitution.md C-15).
    single<Random> { Random.Default }

    viewModel { SeedViewModel(seeder = get()) }
    viewModel { RosterViewModel(repo = get(), random = get()) }
    viewModel { params -> SquadViewModel(repo = get(), arcCvId = params.get()) }
    viewModel { MissionsViewModel(repo = get()) }
    viewModel { params ->
        // Parâmetros posicionais: arcId (Long), heróis em CSV (String) e posição do minijogo (Double), nessa ordem.
        val arcId: Long = params.get()
        val heroIds = params.get<String>().split(",").filter { it.isNotBlank() }.map { it.toLong() }
        val position: Double = params.get()
        MissionResultViewModel(
            repo = get(),
            arcCvId = arcId,
            heroIds = heroIds,
            coordinationPosition = position,
            random = get(),
        )
    }
    viewModel { params -> HeroProfileViewModel(repo = get(), cvId = params.get()) }
    viewModel { PokedexViewModel(repo = get()) }
    viewModel { ShopViewModel(repo = get(), random = get()) }
    viewModel { params -> ArcStoryViewModel(repo = get(), arcCvId = params.get()) }
}
