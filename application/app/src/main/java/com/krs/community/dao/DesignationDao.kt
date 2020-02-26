package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Designation

@Dao
interface DesignationDao {

    @Query("SELECT * FROM Designation")
    fun getDesignation() : LiveData<List<Designation>>

    @Query("SELECT id FROM Designation WHERE name=:name")
    fun getDesignationName(name:String) : Int

    @Query("SELECT name FROM Designation WHERE id=:id")
    fun getDesignationNameById(id: Int): String

    @Query("SELECT name FROM Designation ORDER BY name ASC")
    fun getDesignationName() : LiveData<List<String>>

    @Query("SELECT id FROM Designation WHERE id NOT IN (:Ids)")
    fun getRemovedDesignationIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM Designation WHERE id IN (:Ids)")
    fun deleteDesignationByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllDesignation(designation : List<Designation>)
}