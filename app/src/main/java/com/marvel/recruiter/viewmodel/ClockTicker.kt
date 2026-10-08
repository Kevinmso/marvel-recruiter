package com.marvel.recruiter.viewmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Emite o instante atual a cada [periodMs]; usado para atualizar cooldowns na tela. */
fun clockTicks(periodMs: Long = 30_000L, now: () -> Long = System::currentTimeMillis): Flow<Long> =
    flow {
        while (true) {
            emit(now())
            delay(periodMs)
        }
    }
