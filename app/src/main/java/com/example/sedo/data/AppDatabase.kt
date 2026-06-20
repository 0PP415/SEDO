package com.example.sedo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// 버전은 1부터 시작합니다. (나중에 표 규격이 바뀌면 버전을 올려야 합니다)
@Database(entities = [ClothEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // 아까 만든 변환기 장착
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothDao(): ClothDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 앱 전체에서 DB 창고는 딱 하나만 존재하도록(Singleton) 만드는 로직입니다
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sedo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}