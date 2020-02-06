package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Native

@Dao
interface NativeDao {

    @Query("SELECT name FROM Native ORDER BY name ASC")
    fun getNative() : LiveData<List<String>>

    @Query("SELECT id FROM Native")
    fun getNativeIds() : LiveData<List<Int>>

    @Query("SELECT name FROM Native WHERE id == :id")
    fun getNativeById(id:Int) : LiveData<String>

    @Query("SELECT id FROM Native WHERE name == :name")
    fun getNativeIdByName(name:String) : Int

    @Query("SELECT id FROM Native WHERE id NOT IN (:Ids)")
    fun getRemovedNativeIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM Native WHERE id IN (:Ids)")
    fun deleteNativeByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllNative(native : List<Native>)
}