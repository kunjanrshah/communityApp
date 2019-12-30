package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.LastName
import com.krs.community.entities.SubCommunity

@Dao
interface LastNameDao {

    @Query("SELECT name FROM LastName ORDER BY name ASC")
    fun getLastName() : LiveData<List<String>>

    @Query("SELECT id FROM LastName ORDER BY name ASC")
    fun getLastNameIds() : LiveData<List<Int>>

    @Query("SELECT id FROM LastName WHERE name == :name")
    fun getIdByLastName(name:String) : LiveData<Int>

    @Query("SELECT id FROM LastName WHERE name == :name")
    fun getIdOfLastName(name:String) : Int

    @Query("SELECT name FROM LastName WHERE id == :id")
    fun getLastNameById(id:Int) : LiveData<String>

    @Query("SELECT name FROM LastName WHERE id == :id")
    fun getLastName(id:Int) : String

    @Query("SELECT id FROM LastName WHERE id NOT IN (:Ids)")
    fun getRemovedLastNameIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM LastName WHERE id IN (:Ids)")
    fun deleteLastNameByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllLastName(subCommunity : List<LastName>)
}