package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.SubCommunity

@Dao
interface SubCommunityDao {

    @Query("SELECT * FROM SubCommunity")
    fun getSubCommunity() : LiveData<List<SubCommunity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllSubCommunities(subCommunity : List<SubCommunity>)
}