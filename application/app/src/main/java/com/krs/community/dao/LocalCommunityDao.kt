package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.LocalCommunity
import com.krs.community.entities.SubCommunity

@Dao
interface LocalCommunityDao {

    @Query("SELECT * FROM LocalCommunity")
    fun getLocalCommunity() : LiveData<List<LocalCommunity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllLocalCommunities(subCommunity : List<LocalCommunity>)
}