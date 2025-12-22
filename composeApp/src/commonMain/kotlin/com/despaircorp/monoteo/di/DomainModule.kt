package com.despaircorp.monoteo.di

import com.despaircorp.monoteo.domain.location.RequestUserPositionUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { RequestUserPositionUseCase(get()) }
}