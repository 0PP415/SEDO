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

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth: ClothEntity)

    // Read
    @Query("SELECT * FROM clothes_table ORDER BY id DESC")
    fun getAllClothes(): Flow<List<ClothEntity>>

    // Update
    @Update
    suspend fun updateCloth(cloth: ClothEntity)

    // Delete
    @Delete
    suspend fun deleteCloth(cloth: ClothEntity)
}