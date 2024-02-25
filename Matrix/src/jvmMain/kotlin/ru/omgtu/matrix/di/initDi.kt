package ru.omgtu.matrix.di

import org.koin.core.context.startKoin

fun initDi() {
    startKoin {
        modules(diModule)
    }
}