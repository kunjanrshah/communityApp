package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.States

@Dao
interface StatesDao {

    @Query("SELECT * FROM States")
    fun getStates() : LiveData<List<States>>

    @Query("SELECT name FROM States")
    fun getStateNames() : LiveData<List<String>>

    @Query("SELECT name FROM States WHERE id == :id")
    fun getstateNameById(id: Int): String

    @Query("SELECT id FROM States WHERE name == :name")
    fun getstateIdByName(name:String) : Int

    @Query("SELECT id FROM States")
    fun getStateIds() : LiveData<List<Int>>

    @Query("SELECT id FROM States WHERE id NOT IN (:Ids)")
    fun getRemovedStateIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM States WHERE id IN (:Ids)")
    fun deleteStateByIds(Ids: List<Int>) : Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllStates(state : List<States>)

}