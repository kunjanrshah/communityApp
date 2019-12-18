package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.krs.community.entities.Designation
import com.krs.community.entities.Gotra
import com.krs.community.entities.LastUpdated
import com.krs.community.entities.SubCommunity

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