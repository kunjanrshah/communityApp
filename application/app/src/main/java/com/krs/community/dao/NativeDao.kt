package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.NativeList

@Dao
interface NativeDao {

    @Query("SELECT name FROM NativeList ORDER BY name ASC")
    fun getNative(): LiveData<List<String>>

    @Query("SELECT COUNT(id) FROM NativeList")
    fun getNativeCount(): Int

    @Query("SELECT id FROM NativeList")
    fun getNativeIds(): LiveData<List<Int>>

    @Query("SELECT name FROM NativeList WHERE id == :id")
    fun getNativeById(id: Int): LiveData<String>

    @Query("SELECT name FROM NativeList WHERE id == :id")
    fun getNative(id: Int): String

    @Query("SELECT id FROM NativeList WHERE name == :name")
    fun getNativeIdByName(name: String): Int

    @Query("SELECT id FROM NativeList WHERE id NOT IN (:Ids)")
    fun getRemovedNativeIds(Ids: List<String>): List<Int>

    @Query("DELETE FROM NativeList WHERE id IN (:Ids)")
    fun deleteNativeByIds(Ids: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllNative(nativeList: List<NativeList>)
}