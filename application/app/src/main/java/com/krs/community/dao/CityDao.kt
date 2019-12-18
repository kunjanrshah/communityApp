package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.City
import com.krs.community.entities.SubCommunity

@Dao
interface CityDao {

    @Query("SELECT * FROM City")
    fun getCity() : LiveData<List<City>>

    @Query("SELECT name FROM City WHERE parent_id == :stateId")
    fun getCityNameByState(stateId:Int) : List<String>

    @Query("SELECT name FROM City WHERE id == :id")
    fun getcityNameById(id:Int) : LiveData<String>

    @Query("SELECT id FROM City WHERE name == :name")
    fun getCityIdByName(name:String) : LiveData<Int>

    @Query("SELECT * FROM City WHERE parent_id == :stateId")
    fun getCityById(stateId:Int) : LiveData<List<City>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllCity(city : List<City>)
}