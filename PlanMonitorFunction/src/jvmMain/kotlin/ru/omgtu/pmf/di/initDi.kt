package ru.omgtu.pmf.di

import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initDi() {
    startKoin {
        modules(diModule)
    }
}