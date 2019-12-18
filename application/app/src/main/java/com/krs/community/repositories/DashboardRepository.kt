package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.entities.*
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DashboardRepository(
        private val api: ApiServices,
        private val db: AppDatabase
): SafeApiRequest()  {

    private val subCommunity = MutableLiveData<List<SubCommunity>>()
    private val localCommunity = MutableLiveData<List<LocalCommunity>>()
    private val lastName = MutableLiveData<List<LastName>>()
    private val education = MutableLiveData<List<Educations>>()
    private val gotra = MutableLiveData<List<Gotra>>()
    private val state = MutableLiveData<List<States>>()
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
    private val TAG:String=DashboardRepository::class.java.simpleName
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


    init {
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

   suspend fun fetchDesignation() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.designation))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getDesignation(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.designation),response.last_updated)
                lastUpdated.postValue(lastdate)
                designation.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchCommittee() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.committee))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getCommittee(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.committee),response.last_updated)
                lastUpdated.postValue(lastdate)
                committee.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchSubCommunities() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.sub_community))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getSubCommunity(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.sub_community),response.last_updated)
                lastUpdated.postValue(lastdate)
                subCommunity.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchLocalCommunities() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.local_community))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getListLocalCommunity(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.local_community),response.last_updated)
                lastUpdated.postValue(lastdate)
                localCommunity.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchLastName() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.last_name))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getUserLastName(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.last_name),response.last_updated)
                lastUpdated.postValue(lastdate)
                lastName.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchEducation() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.education))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getEducation(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.education),response.last_updated)
                lastUpdated.postValue(lastdate)
                education.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchGotra() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.gotra))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getGotra(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.gotra),response.last_updated)
                lastUpdated.postValue(lastdate)
                gotra.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchState(){
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.state))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getUserState(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.state),response.last_updated)
                lastUpdated.postValue(lastdate)
                state.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchCity() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.city))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getListCity(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.city),response.last_updated)
                lastUpdated.postValue(lastdate)
                city.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchBusinessCategory() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.business_category))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getBusinessCategory(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.business_category),response.last_updated)
                lastUpdated.postValue(lastdate)
                businessCategory.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchBusinessSubCategory() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.business_sub_category))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getListBusinessSubCategory(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.business_sub_category),response.last_updated)
                lastUpdated.postValue(lastdate)
                businessSubCategory.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchNative() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string._native))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getNative(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string._native),response.last_updated)
                lastUpdated.postValue(lastdate)
                native.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchOccupation() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.occupation))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getOccupation(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.occupation),response.last_updated)
                lastUpdated.postValue(lastdate)
                occupation.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchRelations() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.relation))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getRelations(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.relation),response.last_updated)
                lastUpdated.postValue(lastdate)
                relations.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchCurrentActivity() {
        return withContext(Dispatchers.IO) {
            try {
                val date= db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.current_activity))
                val mJSONObject=JSONObject()
                mJSONObject.put(AppController.mApplication.getString(R.string.date),date)
                val updated=  JsonParser().parse(mJSONObject.toString()) as JsonObject
                val response = apiRequest { api.getActivity(updated) }
                Log.d(TAG, "response: $response")
                val lastdate=LastUpdated(AppController.mApplication.getString(R.string.current_activity),response.last_updated)
                lastUpdated.postValue(lastdate)
                currentActivity.postValue(response.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}