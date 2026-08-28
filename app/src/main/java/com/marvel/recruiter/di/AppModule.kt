package com.marvel.recruiter.di

import org.koin.dsl.module

/**
 * Grafo de dependências do app.
 *
 * Cada tarefa que introduz uma peça registra ela aqui:
 *  - T-04/T-05: `AppDatabase` (Room) + DAOs
 *  - Fase 2 (`game/`): fontes de aleatoriedade e serviços de lógica de jogo
 *  - Fase 3: `ComicVineApi` (Ktor) — só usado na rotina de seed (RF-01, C-02)
 *  - Fase 4: ViewModels das 5 telas (spec.md)
 *
 * `single { }` = uma instância reusada no app inteiro.
 * `factory { }` = nova instância a cada `get()`.
 * `viewModel { }` = integrado ao ciclo de vida do Android (precisa de koin-androidx-compose).
 */
val appModule = module {
    // vazio por enquanto — peças entram nas tarefas correspondentes
}
