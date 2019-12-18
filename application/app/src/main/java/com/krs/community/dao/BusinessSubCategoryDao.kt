package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.BusinessSubCategory
import com.krs.community.entities.SubCommunity

@Dao
interface BusinessSubCategoryDao {

    @Query("SELECT name FROM BusinessSubCategory")
    fun getBusinessSubCategory() : LiveData<List<String>>

    @Query("SELECT id FROM BusinessSubCategory")
    fun getBusinessSubCategoryIds() : LiveData<List<Int>>

    @Query("SELECT name FROM BusinessSubCategory WHERE id == :id")
    fun getBusinessSubCategoryById(id:Int) : LiveData<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllBusinessSubCategory(businessSubCategory : List<BusinessSubCategory>)
}