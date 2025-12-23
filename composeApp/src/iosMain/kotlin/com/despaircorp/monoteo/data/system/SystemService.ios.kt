package com.despaircorp.monoteo.data.system

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.preferredLanguages

actual class SystemService {
    actual fun getLanguage(): String {
        val languages = NSLocale.preferredLanguages
        return (languages.firstOrNull() as? String)?.take(2) ?: "en"
    }
}