package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.model.SearchData
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileDetailRepository (private val api: ApiServices,private val db:AppDatabase
): SafeApiRequest()  {



    suspend fun updateProfile(profile: JsonObject): UpdateProfileResponse {
        return apiRequest{
            api.updateProfile(profile)
        }
    }

    suspend fun addProfile(profile: JsonObject): UpdateProfileResponse {
        return apiRequest{
            api.addMember(profile)
        }
    }


    suspend fun getOccupationNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupations()
        }
    }

    suspend fun getOccupationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupationById(id)
        }
    }

    suspend fun getOccupationId(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupationIds()
        }
    }

    suspend fun getBusinessSubCategoryNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getBusinessSubCategory()
        }
    }

    suspend fun getBusinessSubCategoryById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getBusinessSubCategoryById(id)
        }
    }

    suspend fun getBusinessSubCategoryIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getBusinessSubCategoryIds()
        }
    }

    suspend fun getBusinessCategoryNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategorys()
        }
    }

    suspend fun getBusinessCategoryById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategoryById(id)
        }
    }

    suspend fun getBusinessCategoryIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategoryIds()
        }
    }

    suspend fun getGotraNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotra()
        }
    }

    suspend fun getGotraById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotraById(id)
        }
    }

    suspend fun getGotraIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotraIds()
        }
    }

    suspend fun getActivityNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivity()
        }
    }

    suspend fun getActivityById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivityById(id)
        }
    }

    suspend fun getActivityIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivityIds()
        }
    }

    suspend fun getEducationNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducations()
        }
    }

    suspend fun getEducationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducationById(id)
        }
    }

    suspend fun getEducationIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducationIds()
        }
    }

    suspend fun getNativeNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative()
        }
    }

    suspend fun getNativeNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNativeById(id)
        }
    }

    suspend fun getNativeIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNativeIds()
        }
    }

    suspend fun getRelations(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelations()
        }
    }

    suspend fun getRelationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelationById(id)
        }
    }

    suspend fun getRelationIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelationIds()
        }
    }
    suspend fun getLastNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastNameById(id)
        }
    }
    suspend fun getLastName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName()
        }
    }

    suspend fun getLastNameIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastNameIds()
        }
    }

    suspend fun getcityNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityNameById(id)
        }
    }



    suspend fun getCityName(id:Int): List<String> {
        return withContext(Dispatchers.IO) {
            Log.d("SP_State","id: "+id)
            db.getCityDao().getCityNameByState(id)
        }
    }

    suspend fun getCityId(name:String): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getCityIdByName(name)
        }
    }

    suspend fun getstateNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getstateNameById(id)
        }
    }
    suspend fun getStateName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getStateNames()
        }
    }

    suspend fun getStateIds(): LiveData<List<Int>> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getStateIds()
        }
    }
}