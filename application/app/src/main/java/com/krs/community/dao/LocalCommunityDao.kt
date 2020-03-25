package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.LocalCommunity


@Dao
interface LocalCommunityDao {

    @Query("SELECT * FROM LocalCommunity WHERE parent_id=:id")
    fun getLocalCommunity(id: Int) : LiveData<List<LocalCommunity>>

    @Query("SELECT COUNT(id) FROM LocalCommunity")
    fun getLocalCommunityCount(): Int

    @Query("SELECT name FROM LocalCommunity ORDER BY name ASC")
    fun getLocalCommName() : LiveData<List<String>>

    @Query("SELECT name FROM LocalCommunity WHERE parent_id=:id ORDER BY name ASC")
    fun getLocalCommNameBySubId(id:Int) : LiveData<List<String>>

    @Query("SELECT name FROM LocalCommunity WHERE id=:id")
    fun getLocalCommName(id:String) : String

    @Query("SELECT id FROM LocalCommunity WHERE name=:name")
    fun getLocalCommunityId(name:String) : Int

    @Query("SELECT id FROM LocalCommunity WHERE id NOT IN (:Ids)")
    fun getRemovedLocalCommunityIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM LocalCommunity WHERE id IN (:Ids)")
    fun deleteLocalCommunityByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllLocalCommunities(subCommunity : List<LocalCommunity>)
}