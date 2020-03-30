package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Educations

@Dao
interface EducationDao {

    @Query("SELECT name FROM Educations")
    fun getEducations(): LiveData<List<String>>

    @Query("SELECT id FROM Educations")
    fun getEducationIds(): LiveData<List<Int>>

    @Query("SELECT name FROM Educations WHERE id == :id")
    fun getEducationById(id: Int): LiveData<String>

    @Query("SELECT id FROM Educations WHERE id NOT IN (:Ids)")
    fun getRemovedEducationIds(Ids: List<String>): List<Int>

    @Query("DELETE FROM Educations WHERE id IN (:Ids)")
    fun deleteEducationByIds(Ids: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllEducation(educations: List<Educations>)


    @Query("SELECT id FROM Educations WHERE name == :name")
    fun getEducationIdByName(name: String): Int
}