package com.krs.community.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.krs.community.dao.*
import com.krs.community.entities.*

@Database(entities = [RoomMember::class, Designation::class, Committee::class, States::class, Relations::class, Occupations::class,
    NativeList::class, LastName::class, Gotra::class, Educations::class, CurrentActivity::class, City::class,
    BusinessSubCategory::class, BusinessCategory::class, SubCommunity::class, LocalCommunity::class, LastUpdated::class, MasterCounts::class],
        version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getSubCommunityDao(): SubCommunityDao
    abstract fun getLocalCommunityDao(): LocalCommunityDao
    abstract fun getLastNameDao(): LastNameDao
    abstract fun getBusinessCategoryDao(): BusinessCategoryDao
    abstract fun getBusinessSubCategoryDao(): BusinessSubCategoryDao
    abstract fun getCityDao(): CityDao
    abstract fun getEducationDao(): EducationDao
    abstract fun getGotraDao(): GotraDao
    abstract fun getNativeDao(): NativeDao
    abstract fun getOccupationDao(): OccupationDao
    abstract fun getRelationsDao(): RelationDao
    abstract fun getStatesDao(): StatesDao
    abstract fun getCurrentActivityDao(): CurrentActivityDao
    abstract fun getCommitteeDao(): CommitteeDao
    abstract fun getDesignationDao(): DesignationDao
    abstract fun getLastUpdatedDao(): LastUpdatedDao
    abstract fun getMasterUpdateDao(): MasterUpdateDao
    abstract fun getRoomMemberDao(): RoomMemberDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "Community.db"
                ).build()
    }
}