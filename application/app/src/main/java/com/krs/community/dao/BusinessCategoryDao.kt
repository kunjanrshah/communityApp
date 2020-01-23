package com.krs.community.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krs.community.entities.BusinessCategory
import com.krs.community.entities.SubCommunity

@Dao
interface BusinessCategoryDao {

    @Query("SELECT name FROM BusinessCategory")
    fun getBusinessCategorys() : LiveData<List<String>>

    @Query("SELECT id FROM BusinessCategory")
    fun getBusinessCategoryIds() : LiveData<List<Int>>

    @Query("SELECT name FROM BusinessCategory WHERE id == :id")
    fun getBusinessCategoryById(id:Int) : LiveData<String>

    @Query("SELECT id FROM BusinessCategory WHERE id NOT IN (:Ids)")
    fun getRemovedCategoryIds(Ids: List<String>) : List<Int>

    @Query("DELETE FROM BusinessCategory WHERE id IN (:Ids)")
    fun deleteCategoryByIds(Ids: List<Int>) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllBusinessCategory(businessCategory : List<BusinessCategory>)


    @Query("SELECT id FROM BusinessCategory WHERE name == :name")
    fun getCategoryIdByName(name:String) : Int

}