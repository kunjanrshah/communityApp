package com.krs.community.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.MasterCounts

@Dao
interface MasterUpdateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMasterCounts(masterCounts: MasterCounts)

    @Query("UPDATE MasterCounts SET business_categories=:index")
    fun updateBusinessCategoryIndex(index: Int)

    @Query("SELECT * FROM MasterCounts")
    fun getMasterCounts(): MasterCounts
}