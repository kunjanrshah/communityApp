package com.krs.community.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.MasterCounts

@Dao
interface MasterUpdateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMasterCounts(masterCounts: MasterCounts)

    @Query("UPDATE MasterCounts SET business_categories=:index")
    fun updateBusinessCategoryIndex(index: Int)

    @Query("UPDATE MasterCounts SET business_sub_categories=:index")
    fun updateBusinessSubCategoryIndex(index: Int)

    @Query("UPDATE MasterCounts SET cities=:index")
    fun updateCitiesIndex(index: Int)

    @Query("UPDATE MasterCounts SET committees=:index")
    fun updateCommiteesIndex(index: Int)

    @Query("UPDATE MasterCounts SET current_activity=:index")
    fun updateCurrentActivityIndex(index: Int)

    @Query("UPDATE MasterCounts SET designations=:index")
    fun updateDesignationIndex(index: Int)

    @Query("UPDATE MasterCounts SET districts=:index")
    fun updateDistrictIndex(index: Int)

    @Query("UPDATE MasterCounts SET educations=:index")
    fun updateEducationIndex(index: Int)

    @Query("UPDATE MasterCounts SET local_community=:index")
    fun updateLocalCommIndex(index: Int)

    @Query("UPDATE MasterCounts SET native_place=:index")
    fun updateNativeIndex(index: Int)

    @Query("UPDATE MasterCounts SET occupation=:index")
    fun updateOccupationIndex(index: Int)

    @Query("UPDATE MasterCounts SET relations=:index")
    fun updateRelationIndex(index: Int)

    @Query("UPDATE MasterCounts SET states=:index")
    fun updateStateIndex(index: Int)

    @Query("UPDATE MasterCounts SET sub_casts=:index")
    fun updateSubCastIndex(index: Int)

    @Query("UPDATE MasterCounts SET sub_community=:index")
    fun updateSubCommIndex(index: Int)

    @Query("UPDATE MasterCounts SET gotra=:index")
    fun updateGotraIndex(index: Int)

    @Query("SELECT * FROM MasterCounts")
    fun getMasterCounts(): MasterCounts
}