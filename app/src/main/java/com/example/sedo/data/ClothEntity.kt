package com.example.sedo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes_table")
data class ClothEntity(
    @PrimaryKey(autoGenerate = true) // 데이터가 추가될 때마다 id를 자동으로 부여
    val id: Long = 0,
    val imageUri: String,            // 갤러리에서 가져온 사진 주소
    val name: String,                // 옷 이름
    val category: String,            // 카테고리
    val season: String,              // 계절
    val material: String,            // 소재
    val washGuide: String,           // AI 세탁 가이드 요약
    val tagSymbols: List<String>,     // 세탁 기호 리스트

    val video1Id: String, // youtube1 아이디
    val video2Id: String, // youtube2 아이디
    val video1Title: String, // 영상제목 1
    val video2Title: String // 영상제목 2
)
