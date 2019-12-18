package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Committee
import com.krs.community.entities.Designation
import com.krs.community.entities.Gotra
import com.krs.community.entities.SubCommunity

@Dao
interface CommitteeDao {

    @Query("SELECT * FROM Committee")
    fun getCommittee() : LiveData<List<Committee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllCommittee(committee : List<Committee>)
}