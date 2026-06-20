package com.example.sedo.data

import androidx.room.TypeConverter

class Converters {
    // 1. DB에 저장할 때: List<String> ➡️ "ER_wash_40,ER_no_bleach" (단일 String)
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    // 2. DB에서 꺼낼 때: "ER_wash_40,ER_no_bleach" ➡️ List<String>
    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split(",")
    }
}