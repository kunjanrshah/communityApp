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


    @Query("SELECT name FROM City WHERE id == :id")
    fun getcityName(id:Int) : String

    @Query("SELECT name FROM City ORDER BY name ASC")
    fun getcityNames() : LiveData<List<String>>

    @Query("SELECT id FROM City WHERE name == :name")
    fun getCityIdByName(name:String) : LiveData<Int>

    @Query("SELECT id FROM City WHERE name == :name")
    fun getCityId(name:String) : Int

    @Query("SELECT * FROM City WHERE parent_id == :stateId")
    fun getCityById(stateId:Int) : LiveData<List<City>>

    @Query("SELECT id FROM City WHERE id NOT IN (:Ids)")
    fun getRemovedCityIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM City WHERE id IN (:Ids)")
    fun deleteCityByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllCity(city : List<City>)
}