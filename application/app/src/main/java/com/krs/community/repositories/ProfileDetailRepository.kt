package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.AppDatabase
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileDetailRepository(private val api: ApiServices, private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun changeStatus(jsonObject: JsonObject): searchByKeywordsResponse {
        return apiRequest {
            api.changeStatus(jsonObject)
        }
    }

    suspend fun updateProfile(profile: JsonObject): UpdateProfileResponse {
        return apiRequest {
            api.updateProfile(profile)
        }
    }

    suspend fun uploadProfileImage(profile: MultipartBody.Part, id: RequestBody, type: RequestBody): JsonObject {
        return apiRequest {
            api.uploadProfileImage(profile, id, type)
        }
    }

    suspend fun addProfile(profile: JsonObject): UpdateProfileResponse {
        return apiRequest {
            api.addMember(profile)
        }
    }

    suspend fun searchFilter(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getSearchByFilter(jsonObject)
        }
    }

    suspend fun getListCityName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityNames()
        }
    }

    suspend fun getOccupationNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupations()
        }
    }

    suspend fun getOccupationIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupationIdByName(name)
        }
    }

    suspend fun getOccupationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupationById(id)
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

    suspend fun getSubCategoryIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getSubCategoryIdByName(name)
        }
    }

    suspend fun getBusinessCategoryNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategorys()
        }
    }

    suspend fun getCategoryIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getCategoryIdByName(name)
        }
    }

    suspend fun getBusinessCategoryById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategoryById(id)
        }
    }

    suspend fun getGotraNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotra()
        }
    }

    suspend fun getGotraIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotraIdByName(name)
        }
    }

    suspend fun getGotraById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotraById(id)
        }
    }

    suspend fun getActivityNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivity()
        }
    }


    suspend fun getActivityIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getActivityIdByName(name)
        }
    }

    suspend fun getActivityById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivityById(id)
        }
    }

    suspend fun getEducationNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducations()
        }
    }


    suspend fun getEducationIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducationIdByName(name)
        }
    }

    suspend fun getEducationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducationById(id)
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

    suspend fun getNativeIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNativeIdByName(name)
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

    suspend fun getRelationNameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelationNameById(id)
        }
    }

    suspend fun getIdByRelation(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getIdByRelation(name)
        }
    }

    suspend fun getIdByLastName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getIdByLastName(name)
        }
    }

    suspend fun getLastNameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName(id)
        }
    }

    suspend fun getLastName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName()
        }
    }

    suspend fun getLocalCommName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommName()
        }
    }

    suspend fun getLocalCommunity(id: Int): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommNameBySubId(id)
        }
    }

    suspend fun getLocalCommunityId(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommunityId(name)
        }
    }

    suspend fun getcityNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityNameById(id)
        }
    }

    suspend fun getCityIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityIdByName(name)
        }
    }

    suspend fun getCityName(id: Int): List<String> {
        return withContext(Dispatchers.IO) {
            Log.d("SP_State", "id: " + id)
            db.getCityDao().getCityNameByState(id)
        }
    }

    suspend fun getCityId(name: String): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getCityIdByName(name)
        }
    }

    suspend fun getstateIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getstateIdByName(name)
        }
    }

    suspend fun getstateNameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getstateNameById(id)
        }
    }

    suspend fun getStateName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getStateNames()
        }
    }

    suspend fun getSubCommIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommIdByName(name)
        }
    }

    suspend fun getSubCommName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommName()
        }
    }

    suspend fun deleteMember(data: JsonObject): DeleteProfileResponse {
        return apiRequest {
            api.deleteMember(data)
        }
    }
}