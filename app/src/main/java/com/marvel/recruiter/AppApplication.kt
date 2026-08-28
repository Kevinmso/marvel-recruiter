package com.marvel.recruiter

import android.app.Application
import com.marvel.recruiter.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application do app. Registrada no AndroidManifest (`android:name`).
 *
 * Inicia o Koin uma única vez, no boot do processo: a partir daqui qualquer
 * tela pode pedir suas dependências com `koinViewModel()` / `get()` sem
 * receber `Context` de mão em mão.
 */
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()                 // loga o grafo no Logcat (útil enquanto monta)
            androidContext(this@AppApplication)
            modules(appModule)
        }
    }
}
