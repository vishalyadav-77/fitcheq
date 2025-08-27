package com.vayo.fitcheq.data.model.utils

inline fun <reified T : Enum<T>> T?.orDefault(default: T): T {
    return this ?: default
}

