package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Occupations
import com.krs.community.entities.SubCommunity

@Dao
interface OccupationDao {

    @Query("SELECT name FROM Occupations")
    fun getOccupations() : LiveData<List<String>>

    @Query("SELECT id FROM Occupations")
    fun getOccupationIds() : LiveData<List<Int>>

    @Query("SELECT name FROM Occupations WHERE id == :id")
    fun getOccupationById(id:Int) : LiveData<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllOccupation(occupations : List<Occupations>)
}