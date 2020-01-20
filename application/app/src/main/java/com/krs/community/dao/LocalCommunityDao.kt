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

    @Query("SELECT name FROM LocalCommunity WHERE id=:id")
    fun getLocalCommunity(id:String) : String

    @Query("SELECT id FROM LocalCommunity WHERE id NOT IN (:Ids)")
    fun getRemovedLocalCommunityIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM LocalCommunity WHERE id IN (:Ids)")
    fun deleteLocalCommunityByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllLocalCommunities(subCommunity : List<LocalCommunity>)
}