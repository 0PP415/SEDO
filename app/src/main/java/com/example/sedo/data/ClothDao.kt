// 기존 코드에 아래 2개의 어노테이션(@Update, @Delete) 함수를 추가해 주세요!
package com.example.sedo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothDao {

    // [C] Create (기존)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth: ClothEntity)

    // [R] Read (기존)
    @Query("SELECT * FROM clothes_table ORDER BY id DESC")
    fun getAllClothes(): Flow<List<ClothEntity>>

    // ⭐️ [U] Update (새로 추가!)
    // id가 일치하는 데이터를 찾아서, 나머지 내용들을 새것으로 덮어씌웁니다.
    @Update
    suspend fun updateCloth(cloth: ClothEntity)

    // ⭐️ [D] Delete (새로 추가!)
    // 전달받은 entity와 id가 일치하는 행을 통째로 날려버립니다.
    @Delete
    suspend fun deleteCloth(cloth: ClothEntity)
}