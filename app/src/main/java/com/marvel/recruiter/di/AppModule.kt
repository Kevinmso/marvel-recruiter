package com.marvel.recruiter.di

import androidx.room.Room
import com.marvel.recruiter.data.local.AppDatabase
import com.marvel.recruiter.data.local.InitialStateCallback
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Grafo de dependências do app.
 *
 * Cada tarefa que introduz uma peça registra ela aqui:
 *  - T-05: `AppDatabase` (Room) + DAOs           ← feito
 *  - Fase 2 (`game/`): fontes de aleatoriedade e serviços de lógica de jogo
 *  - Fase 3: `ComicVineApi` (Ktor) — só usado na rotina de seed (RF-01, C-02)
 *  - Fase 4: ViewModels das 5 telas (spec.md)
 *
 * `single { }` = uma instância reusada no app inteiro.
 * `factory { }` = nova instância a cada `get()`.
 * `viewModel { }` = integrado ao ciclo de vida do Android (precisa de koin-androidx-compose).
 */
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
}
