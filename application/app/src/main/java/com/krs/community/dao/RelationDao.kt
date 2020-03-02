package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Relations

@Dao
interface RelationDao {

    @Query("SELECT name FROM Relations")
    fun getRelations() : LiveData<List<String>>

    @Query("SELECT id FROM Relations")
    fun getRelationIds() : LiveData<List<Int>>

    @Query("SELECT name FROM Relations WHERE id == :id")
    fun getRelationById(id:Int) : LiveData<String>

    @Query("SELECT id FROM Relations WHERE name == :name")
    fun getIdByRelation(name: String): Int

    @Query("SELECT name FROM Relations WHERE id == :id")
    fun getRelationNameById(id:Int) : String

    @Query("SELECT id FROM Relations WHERE id NOT IN (:Ids)")
    fun getRemovedRelationIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM Relations WHERE id IN (:Ids)")
    fun deleteRelationByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllRelation(subCommunity : List<Relations>)
}