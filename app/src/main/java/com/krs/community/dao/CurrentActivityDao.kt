package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.CurrentActivity

@Dao
interface CurrentActivityDao {

    @Query("SELECT name FROM CurrentActivity")
    fun getCurrentActivity(): LiveData<List<String>>

    @Query("SELECT id FROM CurrentActivity")
    fun getCurrentActivityIds(): LiveData<List<Int>>

    @Query("SELECT name FROM CurrentActivity WHERE id == :id")
    fun getCurrentActivityById(id: Int): LiveData<String>

    @Query("SELECT id FROM CurrentActivity WHERE name == :name")
    fun getActivityIdByName(name: String): Int

    @Query("SELECT id FROM CurrentActivity WHERE id NOT IN (:Ids)")
    fun getRemovedActivityIds(Ids: List<String>): List<Int>

    @Query("DELETE FROM CurrentActivity WHERE id IN (:Ids)")
    fun deleteActivityByIds(Ids: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllCurrentActivity(currentActivity: List<CurrentActivity>)
}