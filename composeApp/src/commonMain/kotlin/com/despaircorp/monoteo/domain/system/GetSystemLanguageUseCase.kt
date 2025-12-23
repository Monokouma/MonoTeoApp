package com.despaircorp.monoteo.domain.system

class GetSystemLanguageUseCase(
    private val systemRepository: SystemRepository
) {
    operator fun invoke(): String = systemRepository.getSystemLanguage()
}
