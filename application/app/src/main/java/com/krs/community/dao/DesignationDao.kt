package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.Designation
import com.krs.community.entities.Gotra
import com.krs.community.entities.SubCommunity

@Dao
interface DesignationDao {

    @Query("SELECT * FROM Designation")
    fun getDesignation() : LiveData<List<Designation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllDesignation(designation : List<Designation>)
}