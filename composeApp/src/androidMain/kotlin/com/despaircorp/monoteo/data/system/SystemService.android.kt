package com.despaircorp.monoteo.data.system

import java.util.Locale

actual class SystemService {
    actual fun getLanguage(): String = Locale.getDefault().language
}