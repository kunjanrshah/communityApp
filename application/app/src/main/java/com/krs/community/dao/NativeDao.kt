package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Native
import com.krs.community.entities.SubCommunity

@Dao
interface NativeDao {

    @Query("SELECT name FROM Native")
    fun getNative() : LiveData<List<String>>

    @Query("SELECT id FROM Native")
    fun getNativeIds() : LiveData<List<Int>>

    @Query("SELECT name FROM Native WHERE id == :id")
    fun getNativeById(id:Int) : LiveData<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllNative(native : List<Native>)
}