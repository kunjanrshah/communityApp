package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Occupations

@Dao
interface OccupationDao {

    @Query("SELECT name FROM Occupations")
    fun getOccupations(): LiveData<List<String>>

    @Query("SELECT id FROM Occupations")
    fun getOccupationIds(): LiveData<List<Int>>

    @Query("SELECT name FROM Occupations WHERE id == :id")
    fun getOccupationById(id: Int): LiveData<String>

    @Query("SELECT id FROM Occupations WHERE name == :name")
    fun getOccupationIdByName(name: String): Int

    @Query("SELECT id FROM Occupations WHERE id NOT IN (:Ids)")
    fun getRemovedOccupationIds(Ids: List<String>): List<Int>

    @Query("DELETE FROM Occupations WHERE id IN (:Ids)")
    fun deleteOccupationByIds(Ids: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllOccupation(occupations: List<Occupations>)
}