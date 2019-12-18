package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Gotra
import com.krs.community.entities.SubCommunity

@Dao
interface GotraDao {

    @Query("SELECT name FROM Gotra")
    fun getGotra() : LiveData<List<String>>

    @Query("SELECT id FROM Gotra")
    fun getGotraIds() : LiveData<List<Int>>

    @Query("SELECT name FROM Gotra WHERE id == :id")
    fun getGotraById(id:Int) : LiveData<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllGotra(gotra : List<Gotra>)
}