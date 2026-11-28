package com.example.proyectoquinche.data.remote.mapper

import org.json.JSONObject

internal fun JSONObject.stringOrNull(key: String): String? =
    if (isNull(key)) null else getString(key)

internal fun JSONObject.doubleOrNull(key: String): Double? =
    if (isNull(key)) null else optString(key).toDoubleOrNull()

internal fun JSONObject.booleanOrNull(key: String): Boolean? =
    if (isNull(key)) null else getBoolean(key)

internal fun Any?.orJsonNull(): Any = this ?: JSONObject.NULL
