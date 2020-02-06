package com.krs.community.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.LastUpdated

@Dao
interface LastUpdatedDao {

    /*@Query("SELECT * FROM LastUpdated")
    fun getLastUpdated() : LiveData<List<LastUpdated>>*/

    @Query("SELECT date FROM LastUpdated WHERE name==:name")
    fun getLastUpdatedDate(name:String) : String

    /*@Query("UPDATE udpate FROM LastUpdated WHERE name==:name")
    fun updatedDate(name:String) : String*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLastUpdated(lastUpdated : LastUpdated)
}