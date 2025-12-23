package com.despaircorp.monoteo.data.system

import com.despaircorp.monoteo.domain.system.SystemRepository

class SystemRepositoryImpl(
    private val systemService: SystemService
): SystemRepository {

    override fun getSystemLanguage(): String = systemService.getLanguage()

}