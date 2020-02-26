package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Committee

@Dao
interface CommitteeDao {

    @Query("SELECT * FROM Committee")
    fun getCommittee() : LiveData<List<Committee>>

    @Query("SELECT id FROM Committee WHERE name=:name")
    fun getCommitteeName(name:String) : Int

    @Query("SELECT name FROM Committee WHERE id=:id")
    fun getCommitteeNameById(id: Int): String

    @Query("SELECT name FROM Committee ORDER BY name ASC")
    fun getCommitteeNames() : LiveData<List<String>>

    @Query("SELECT id FROM Committee WHERE id NOT IN (:Ids)")
    fun getRemovedCommitteeIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM Committee WHERE id IN (:Ids)")
    fun deleteCommitteeByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllCommittee(committee : List<Committee>)
}