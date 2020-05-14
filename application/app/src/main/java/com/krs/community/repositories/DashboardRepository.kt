package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.entities.*
import com.krs.community.responses.MasterUpdateResponse
import com.krs.community.responses.UserStatusResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DashboardRepository(
        private val api: ApiServices,
        private val db: AppDatabase
) : SafeApiRequest() {

    private val subCommunity = MutableLiveData<List<SubCommunity>>()
    private val localCommunity = MutableLiveData<List<LocalCommunity>>()
    private val lastName = MutableLiveData<List<LastName>>()
    private val education = MutableLiveData<List<Educations>>()
    private val gotra = MutableLiveData<List<Gotra>>()
    private val state = MutableLiveData<List<States>>()

    private val removedStates = MutableLiveData<List<String>>()
    private val removedCity = MutableLiveData<List<String>>()
    private val removedRelations = MutableLiveData<List<String>>()
    private val removedBusinessCategory = MutableLiveData<List<String>>()
    private val removedBusinessSubCategory = MutableLiveData<List<String>>()
    private val removedActivity = MutableLiveData<List<String>>()
    private val removedOccupation = MutableLiveData<List<String>>()
    private val removedNative = MutableLiveData<List<String>>()
    private val removedSubCommunity = MutableLiveData<List<String>>()
    private val removedLocalCommunity = MutableLiveData<List<String>>()
    private val removedLastName = MutableLiveData<List<String>>()
    private val removedEducation = MutableLiveData<List<String>>()
    private val removedGotra = MutableLiveData<List<String>>()
    private val removedCommittee = MutableLiveData<List<String>>()
    private val removedDesignation = MutableLiveData<List<String>>()

    private val city = MutableLiveData<List<City>>()
    private val businessCategory = MutableLiveData<List<BusinessCategory>>()
    private val businessSubCategory = MutableLiveData<List<BusinessSubCategory>>()
    private val native = MutableLiveData<List<Native>>()
    private val occupation = MutableLiveData<List<Occupations>>()
    private val relations = MutableLiveData<List<Relations>>()
    private val currentActivity = MutableLiveData<List<CurrentActivity>>()
    private val committee = MutableLiveData<List<Committee>>()
    private val designation = MutableLiveData<List<Designation>>()
    private val lastUpdated = MutableLiveData<LastUpdated>()
    private val TAG: String = DashboardRepository::class.java.simpleName
    private fun saveCurrentActivity(currentActivity: List<CurrentActivity>) {
        Coroutines.io {
            db.getCurrentActivityDao().saveAllCurrentActivity(currentActivity)
        }
    }

    private fun saveSubCommunities(subCommunity: List<SubCommunity>) {
        Coroutines.io {
            db.getSubCommunityDao().saveAllSubCommunities(subCommunity)
        }
    }

    private fun saveLocalCommunities(localCommunity: List<LocalCommunity>) {
        Coroutines.io {
            db.getLocalCommunityDao().saveAllLocalCommunities(localCommunity)
        }
    }

    private fun saveLastName(lastName: List<LastName>) {
        Coroutines.io {
            db.getLastNameDao().saveAllLastName(lastName)
        }
    }

    private fun saveBusinessCategory(businessCategory: List<BusinessCategory>) {
        Coroutines.io {
            db.getBusinessCategoryDao().saveAllBusinessCategory(businessCategory)
        }
    }

    private fun saveBusinessSubCategory(businessSubCategory: List<BusinessSubCategory>) {
        Coroutines.io {
            db.getBusinessSubCategoryDao().saveAllBusinessSubCategory(businessSubCategory)
        }
    }

    private fun saveCity(city: List<City>) {
        Coroutines.io {
            db.getCityDao().saveAllCity(city)
        }
    }

    private fun saveEducations(educations: List<Educations>) {
        Coroutines.io {
            db.getEducationDao().saveAllEducation(educations)
        }
    }

    private fun saveGotra(gotra: List<Gotra>) {
        Coroutines.io {
            db.getGotraDao().saveAllGotra(gotra)
        }
    }

    private fun saveNative(native: List<Native>) {
        Coroutines.io {
            db.getNativeDao().saveAllNative(native)
        }
    }

    private fun saveOccupation(occupations: List<Occupations>) {
        Coroutines.io {
            db.getOccupationDao().saveAllOccupation(occupations)
        }
    }

    private fun saveRelations(relations: List<Relations>) {
        Coroutines.io {
            db.getRelationsDao().saveAllRelation(relations)
        }
    }

    private fun saveState(states: List<States>) {
        Coroutines.io {
            /*val lstState=ArrayList<States>()
            lstState.add("")
            lstState.addAll(states)*/
            db.getStatesDao().saveAllStates(states)
        }
    }

    private fun saveCommittee(committee: List<Committee>) {
        Coroutines.io {
            db.getCommitteeDao().saveAllCommittee(committee)
        }
    }

    private fun saveDesignation(designation: List<Designation>) {
        Coroutines.io {
            db.getDesignationDao().saveAllDesignation(designation)
        }
    }

    private fun saveUpdated(updated: LastUpdated) {
        Coroutines.io {
            db.getLastUpdatedDao().saveLastUpdated(updated)
        }
    }

    private fun removedState(states: List<String>) {
        Coroutines.io {
            val deletedIds = db.getStatesDao().getRemovedStateIds(states)
            db.getStatesDao().deleteStateByIds(deletedIds)
        }
    }

    private fun removedActivity(activity: List<String>) {
        Coroutines.io {
            val deletedIds = db.getCurrentActivityDao().getRemovedActivityIds(activity)
            db.getCurrentActivityDao().deleteActivityByIds(deletedIds)
        }
    }

    private fun removedRelations(relation: List<String>) {
        Coroutines.io {
            val deletedIds = db.getRelationsDao().getRemovedRelationIds(relation)
            db.getRelationsDao().deleteRelationByIds(deletedIds)
        }
    }

    private fun removedSubCommunity(SubCommunity: List<String>) {
        Coroutines.io {
            val deletedIds = db.getSubCommunityDao().getRemovedSubCommunityIds(SubCommunity)
            db.getSubCommunityDao().deleteSubCommunityByIds(deletedIds)
        }
    }

    private fun removedLocalCommunity(localcommunity: List<String>) {
        Coroutines.io {
            val deletedIds = db.getLocalCommunityDao().getRemovedLocalCommunityIds(localcommunity)
            db.getLocalCommunityDao().deleteLocalCommunityByIds(deletedIds)
        }
    }

    private fun removedLastName(lastname: List<String>) {
        Coroutines.io {
            val deletedIds = db.getLastNameDao().getRemovedLastNameIds(lastname)
            db.getLastNameDao().deleteLastNameByIds(deletedIds)
        }
    }

    private fun removedEducation(education: List<String>) {
        Coroutines.io {
            val deletedIds = db.getEducationDao().getRemovedEducationIds(education)
            db.getEducationDao().deleteEducationByIds(deletedIds)
        }
    }

    private fun removedGotra(gotra: List<String>) {
        Coroutines.io {
            val deletedIds = db.getGotraDao().getRemovedGotraIds(gotra)
            db.getGotraDao().deleteGotraByIds(deletedIds)
        }
    }

    private fun removedCommittee(committee: List<String>) {
        Coroutines.io {
            val deletedIds = db.getCommitteeDao().getRemovedCommitteeIds(committee)
            db.getCommitteeDao().deleteCommitteeByIds(deletedIds)
        }
    }

    private fun removedDesignation(designation: List<String>) {
        Coroutines.io {
            val deletedIds = db.getDesignationDao().getRemovedDesignationIds(designation)
            db.getDesignationDao().deleteDesignationByIds(deletedIds)
        }
    }

    private fun removedCity(city: List<String>) {
        Coroutines.io {
            val deletedIds = db.getCityDao().getRemovedCityIds(city)
            db.getCityDao().deleteCityByIds(deletedIds)
        }
    }

    private fun removedOccupation(occupation: List<String>) {
        Coroutines.io {
            val deletedIds = db.getOccupationDao().getRemovedOccupationIds(occupation)
            db.getOccupationDao().deleteOccupationByIds(deletedIds)
        }
    }

    private fun removedBusinessCategory(category: List<String>) {
        Coroutines.io {
            val deletedIds = db.getBusinessCategoryDao().getRemovedCategoryIds(category)
            db.getBusinessCategoryDao().deleteCategoryByIds(deletedIds)
        }
    }

    private fun removedBusinessSubCategory(subCategory: List<String>) {
        Coroutines.io {
            val deletedIds = db.getBusinessSubCategoryDao().getRemovedSubCategoryIds(subCategory)
            db.getBusinessSubCategoryDao().deleteSubCategoryByIds(deletedIds)
        }
    }

    private fun removedNative(native: List<String>) {
        Coroutines.io {
            val deletedIds = db.getNativeDao().getRemovedNativeIds(native)
            db.getNativeDao().deleteNativeByIds(deletedIds)
        }
    }

    init {
        removedSubCommunity.observeForever {
            removedSubCommunity(it)
        }
        removedLocalCommunity.observeForever {
            removedLocalCommunity(it)
        }
        removedLastName.observeForever {
            removedLastName(it)
        }
        removedEducation.observeForever {
            removedEducation(it)
        }
        removedGotra.observeForever {
            removedGotra(it)
        }
        removedCommittee.observeForever {
            removedCommittee(it)
        }
        removedDesignation.observeForever {
            removedDesignation(it)
        }
        removedCity.observeForever {
            removedCity(it)
        }
        removedOccupation.observeForever {
            removedOccupation(it)
        }
        removedBusinessCategory.observeForever {
            removedBusinessCategory(it)
        }
        removedBusinessSubCategory.observeForever {
            removedBusinessSubCategory(it)
        }
        removedNative.observeForever {
            removedNative(it)
        }
        removedStates.observeForever {
            removedState(it)
        }
        removedActivity.observeForever {
            removedActivity(it)
        }
        removedRelations.observeForever {
            removedRelations(it)
        }

        subCommunity.observeForever {
            saveSubCommunities(it)
        }
        localCommunity.observeForever {
            saveLocalCommunities(it)
        }
        lastName.observeForever {
            saveLastName(it)
        }
        education.observeForever {
            saveEducations(it)
        }
        gotra.observeForever {
            saveGotra(it)
        }
        state.observeForever {
            saveState(it)
        }
        city.observeForever {
            saveCity(it)
        }
        businessCategory.observeForever {
            saveBusinessCategory(it)
        }
        businessSubCategory.observeForever {
            saveBusinessSubCategory(it)
        }
        occupation.observeForever {
            saveOccupation(it)
        }
        native.observeForever {
            saveNative(it)
        }
        currentActivity.observeForever {
            saveCurrentActivity(it)
        }
        relations.observeForever {
            saveRelations(it)
        }

        designation.observeForever {
            saveDesignation(it)
        }
        lastUpdated.observeForever {
            saveUpdated(it)
        }

        committee.observeForever {
            saveCommittee(it)
        }
    }

    suspend fun insertMasterCounts(masterCounts: MasterCounts) {
        db.getMasterUpdateDao().saveMasterCounts(masterCounts)
    }

    suspend fun getMasterCounts(): MasterCounts {
        return db.getMasterUpdateDao().getMasterCounts()
    }

    suspend fun getLastNameCount(): Int {
        return db.getLastNameDao().getLastNameCount()
    }

    suspend fun getNativeCount(): Int {
        return db.getNativeDao().getNativeCount()
    }

    suspend fun getSubCommCount(): Int {
        return db.getSubCommunityDao().getSubCommunityCount()
    }

    suspend fun getLocalCommCount(): Int {
        return db.getLocalCommunityDao().getLocalCommunityCount()
    }

    suspend fun getStatesCount(): Int {
        return db.getStatesDao().getStatesCount()
    }

    suspend fun getCitiesCount(): Int {
        return db.getCityDao().getCityCount()
    }

    suspend fun getUpdatedVersion(jsonObject: JsonObject): UserStatusResponse {
        return apiRequest {
            api.getUpdatedVersion(jsonObject)
        }
    }

    suspend fun getMasterUpdate(): MasterUpdateResponse {
        return apiRequest {
            api.getMasterUpdate()
        }
    }

    suspend fun fetchDesignation(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.designation))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getDesignation(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateDesignationIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.designation), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    designation.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedDesignation.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchCommittee(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.committee))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getCommittee(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateCommiteesIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.committee), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    committee.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedCommittee.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchSubCommunities(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.sub_community))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getSubCommunity(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateSubCommIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.sub_community), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    subCommunity.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedSubCommunity.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchLocalCommunities(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.local_community))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getListLocalCommunity(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateLocalCommIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.local_community), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    localCommunity.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedLocalCommunity.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchLastName(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.last_name))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getUserLastName(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateSubCastIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.last_name), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    lastName.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedLastName.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchEducation(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.education))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getEducation(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateEducationIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.education), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    education.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedEducation.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchGotra(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.gotra))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getGotra(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateGotraIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.gotra), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    gotra.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedGotra.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchState(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.state))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getUserState(updated) }
                if (response.success) {
                    db.getMasterUpdateDao().updateStateIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.state), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    state.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedStates.postValue(response.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchCity(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.city))
                try {
                    if (!date.isNullOrEmpty()) {
                        date = (Integer.parseInt(date) + 1).toString()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getListCity(updated) }
                Log.d(TAG, "city response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateCitiesIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.city), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    city.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedCity.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchBusinessCategory(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.business_category))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getBusinessCategory(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateBusinessCategoryIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.business_category), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    businessCategory.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedBusinessCategory.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchBusinessSubCategory(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.business_sub_category))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getListBusinessSubCategory(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateBusinessSubCategoryIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.business_sub_category), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    businessSubCategory.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedBusinessSubCategory.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchNative(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string._native))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getNative(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateNativeIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string._native), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    native.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedNative.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchOccupation(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.occupation))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getOccupation(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateOccupationIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.occupation), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    occupation.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedOccupation.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchRelations(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.relation))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getRelations(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateRelationIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.relation), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    relations.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedRelations.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchCurrentActivity(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.current_activity))
                if (!date.isNullOrEmpty()) {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                val mJSONObject = JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getActivity(updated) }
                Log.d(TAG, "response: $response")
                if (response.success) {
                    db.getMasterUpdateDao().updateCurrentActivityIndex(index)
                }
                if (!response.last_updated.isNullOrEmpty()) {
                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.current_activity), response.last_updated)
                    lastUpdated.postValue(lastdate)
                    currentActivity.postValue(response.data)
                }
                if (!response.deleted.isNullOrEmpty()) {
                    removedActivity.postValue(response.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}