package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.krs.community.GetBusinessCategoriesQuery
import com.krs.community.GetCitiesQuery
import com.krs.community.GetCommitteesQuery
import com.krs.community.GetDesignationsQuery
import com.krs.community.GetEducationsQuery
import com.krs.community.GetGotrasQuery
import com.krs.community.GetLocalCommunitiesQuery
import com.krs.community.GetMastersCountsQuery
import com.krs.community.GetRelationsQuery
import com.krs.community.GetStatesQuery
import com.krs.community.GetSubCastsQuery
import com.krs.community.GetSubCommunitiesQuery
import com.krs.community.IsAppVersionExistsQuery
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.entities.BusinessCategory
import com.krs.community.entities.BusinessSubCategory
import com.krs.community.entities.City
import com.krs.community.entities.Committee
import com.krs.community.entities.CurrentActivity
import com.krs.community.entities.Designation
import com.krs.community.entities.Educations
import com.krs.community.entities.Gotra
import com.krs.community.entities.LastName
import com.krs.community.entities.LastUpdated
import com.krs.community.entities.LocalCommunity
import com.krs.community.entities.MasterCounts
import com.krs.community.entities.NativeList
import com.krs.community.entities.Occupations
import com.krs.community.entities.Relations
import com.krs.community.entities.States
import com.krs.community.entities.SubCommunity
import com.krs.community.responses.CountList
import com.krs.community.responses.MasterUpdateResponse
import com.krs.community.responses.UserCounts
import com.krs.community.type.DateInputDto
import com.krs.community.utils.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardRepository(
    private val apolloClient: ApolloClient,
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
    private val native = MutableLiveData<List<NativeList>>()
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

    private fun saveNative(native: List<NativeList>) {
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

    suspend fun getUpdatedVersion(version: Double): Boolean {
        return try {
            val response = apolloClient.query(IsAppVersionExistsQuery(version)).execute()
            response.data?.isAppVersionExists ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getMastersCounts(): MasterUpdateResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apolloClient.query(GetMastersCountsQuery()).execute()

                val data = response.data?.getMastersCounts ?: return@withContext null

                val countList = CountList().apply {
                    businessCategories = data.countList.business_categories
                    cities = data.countList.cities
                    committees = data.countList.committees
                    currentActivity = data.countList.current_activity
                    designations = data.countList.designations
                    districts = data.countList.districts
                    educations = data.countList.educations
                    localCommunity = data.countList.local_community
                    occupation = data.countList.occupation
                    relations = data.countList.relations
                    states = data.countList.states
                    subCasts = data.countList.sub_casts
                    subCommunity = data.countList.sub_community
                    gotra = data.countList.gotra
                }

                val userCounts = UserCounts().apply {
                    matrimonyCounts = data.userCounts.matrimony_counts
                    statusCounts = data.userCounts.status_counts
                }

                return@withContext MasterUpdateResponse().apply {
                    success = data.success
                    message = data.message
                    setCountList(countList)
                    setUserCounts(userCounts)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }


    suspend fun fetchDesignation(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao()
                    .getLastUpdatedDate(AppController.mApplication.getString(R.string.designation))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetDesignationsQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getDesignation = response.data?.getDesignations

                if (getDesignation != null && getDesignation.success) {
                    db.getMasterUpdateDao().updateDesignationIndex(index)

                    val list: List<Designation> = mapToDesignationList(getDesignation.data)
                    designation.postValue(list)
                }

                if (getDesignation != null && getDesignation.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.designation),
                        getDesignation?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getDesignation?.deleted.isNullOrEmpty()) {
                    removedDesignation.postValue(getDesignation?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToDesignationList(apolloList: List<GetDesignationsQuery.Data1?>?): List<Designation> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                Designation(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }


    suspend fun fetchCommittee(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao()
                    .getLastUpdatedDate(AppController.mApplication.getString(R.string.committee))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetCommitteesQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getCommittee = response.data?.getCommittees

                if (getCommittee != null && getCommittee.success) {
                    db.getMasterUpdateDao().updateCommiteesIndex(index)

                    val list: List<Committee> = mapToCommitteeList(getCommittee.data)
                    committee.postValue(list)
                }

                if (getCommittee != null && getCommittee.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.committee),
                        getCommittee?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getCommittee?.deleted.isNullOrEmpty()) {
                    removedCommittee.postValue(getCommittee?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToCommitteeList(apolloList: List<GetCommitteesQuery.Data1?>?): List<Committee> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                Committee(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }



    suspend fun fetchSubCommunities(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao()
                    .getLastUpdatedDate(AppController.mApplication.getString(R.string.sub_community))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetSubCommunitiesQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getSubCommunities = response.data?.getSubCommunities

                if (getSubCommunities != null && getSubCommunities.success) {
                    db.getMasterUpdateDao().updateSubCommIndex(index)

                    val list: List<SubCommunity> = mapToSubCommunityList(getSubCommunities.data)
                    subCommunity.postValue(list)
                }

                if (getSubCommunities != null && getSubCommunities.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.sub_community),
                        getSubCommunities?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getSubCommunities?.deleted.isNullOrEmpty()) {
                    removedSubCommunity.postValue(getSubCommunities?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToSubCommunityList(apolloList: List<GetSubCommunitiesQuery.Data1?>?): List<SubCommunity> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                SubCommunity(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }

    suspend fun fetchLocalCommunities(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao()
                    .getLastUpdatedDate(AppController.mApplication.getString(R.string.local_community))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetLocalCommunitiesQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getLocalCommunities = response.data?.getLocalCommunities

                if (getLocalCommunities != null && getLocalCommunities.success) {
                    db.getMasterUpdateDao().updateLocalCommIndex(index)

                    val list: List<LocalCommunity> =
                        mapToLocalCommunityList(getLocalCommunities.data)
                    localCommunity.postValue(list)
                }

                if (getLocalCommunities != null && getLocalCommunities.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.local_community),
                        getLocalCommunities?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getLocalCommunities?.deleted.isNullOrEmpty()) {
                    removedLocalCommunity.postValue(getLocalCommunities?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToLocalCommunityList(apolloList: List<GetLocalCommunitiesQuery.Data1?>?): List<LocalCommunity> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                LocalCommunity(
                    id = it.id.toInt(),
                    name = it.name,
                    parent_id = it.sub_community_id!!
                )
            }
        } ?: emptyList()
    }



    suspend fun fetchLastName(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.last_name))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetSubCastsQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getSubCasts = response.data?.getSubCasts

                if (getSubCasts != null && getSubCasts.success) {
                    db.getMasterUpdateDao().updateSubCastIndex(index)
                    val list: List<LastName> = mapToLastNameList(getSubCasts.data)
                    lastName.postValue(list)
                }

                if (getSubCasts != null && getSubCasts.last_updated != 0) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.last_name),
                        getSubCasts.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }
                if (!getSubCasts?.deleted.isNullOrEmpty()) {
                    removedLastName.postValue(getSubCasts?.deleted)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToLastNameList(apolloList: List<GetSubCastsQuery.Data1?>?): List<LastName> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                LastName(it.id.toInt(), it.name)
            }
        } ?: emptyList()
    }


    suspend fun fetchEducation(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.education))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                // Execute GraphQL query
                val response = apolloClient.query(
                    GetEducationsQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getEducation = response.data?.getEducations

                // If success, update DB and post data
                if (getEducation != null && getEducation.success) {
                    db.getMasterUpdateDao().updateEducationIndex(index)

                    val list: List<Educations> = mapToEducationList(getEducation.data)
                    education.postValue(list)
                }

                // Update last updated
                if (getEducation != null && getEducation.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.education),
                        getEducation?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                // Post deleted items if any
                if (!getEducation?.deleted.isNullOrEmpty()) {
                    removedEducation.postValue(getEducation?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToEducationList(apolloList: List<GetEducationsQuery.Data1?>?): List<Educations> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                Educations(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }


    suspend fun fetchGotra(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.gotra))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetGotrasQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getGotra = response.data?.getGotras

                if (getGotra != null && getGotra.success) {
                    db.getMasterUpdateDao().updateGotraIndex(index)

                    val list: List<Gotra> = mapToGotraList(getGotra.data)
                    gotra.postValue(list)
                }

                if (getGotra != null && getGotra.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.gotra),
                        getGotra?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getGotra?.deleted.isNullOrEmpty()) {
                    removedGotra.postValue(getGotra?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToGotraList(apolloList: List<GetGotrasQuery.Data1?>?): List<Gotra> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                Gotra(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }


    suspend fun fetchState(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.state))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }
                Log.v("getMastersResponse3:", "" + date)
                val response = apolloClient.query(
                    GetStatesQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getUserState = response.data?.getStates
                Log.v(TAG, "getMastersResponse4: $getUserState")
                if (getUserState != null && getUserState.success) {
                    db.getMasterUpdateDao().updateStateIndex(index)

                    val list: List<States> = mapToStateList(getUserState.data)
                    state.postValue(list)
                }

                if (getUserState != null && getUserState.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.state),
                        getUserState?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getUserState?.deleted.isNullOrEmpty()) {
                    removedStates.postValue(getUserState?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToStateList(apolloList: List<GetStatesQuery.Data1?>?): List<States> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                States(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }


    suspend fun fetchCity(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.city))
                try {
                    if (!date.isNullOrEmpty() && date != "null") {
                        date = (Integer.parseInt(date) + 1).toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Execute GraphQL query
                val response = apolloClient.query(
                    GetCitiesQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getCities = response.data?.getCities

                if (getCities != null && getCities.success) {
                    db.getMasterUpdateDao().updateCitiesIndex(index)

                    val list: List<City> = mapToCityList(getCities.data)
                    city.postValue(list)
                }

                if (getCities != null && getCities.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.city),
                        getCities?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getCities?.deleted.isNullOrEmpty()) {
                    removedCity.postValue(getCities?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToCityList(apolloList: List<GetCitiesQuery.Data1?>?): List<City> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                it.states_id?.let { it1 ->
                    City(
                        id = it.id.toInt(),
                        name = it.name,
                        parent_id = it1,
                    )
                }
            }
        } ?: emptyList()
    }



    suspend fun fetchBusinessCategory(count: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.business_category))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                val response = apolloClient.query(
                    GetBusinessCategoriesQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getBusinessCategory = response.data?.getBusinessCategories

                if (getBusinessCategory != null && getBusinessCategory.success) {
                    db.getMasterUpdateDao().updateBusinessCategoryIndex(count)

                    val list: List<BusinessCategory> =
                        mapToBusinessCategoryList(getBusinessCategory.data)
                    businessCategory.postValue(list)
                }

                if (getBusinessCategory != null && getBusinessCategory.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.business_category),
                        getBusinessCategory?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getBusinessCategory?.deleted.isNullOrEmpty()) {
                    removedBusinessCategory.postValue(getBusinessCategory?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToBusinessCategoryList(apolloList: List<GetBusinessCategoriesQuery.Data1?>?): List<BusinessCategory> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                BusinessCategory(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }

    suspend fun fetchRelations(index: Int) {
        return withContext(Dispatchers.IO) {
            try {
                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.relation))
                if (!date.isNullOrEmpty() && date != "null") {
                    date = (Integer.parseInt(date) + 1).toString()
                }

                // Execute GraphQL query
                val response = apolloClient.query(
                    GetRelationsQuery(
                        if (date != null) Optional.Present(DateInputDto(date = Optional.Present(date))) else Optional.Absent
                    )
                ).execute()
                val getRelations = response.data?.getRelations

                if (getRelations != null && getRelations.success) {
                    db.getMasterUpdateDao().updateRelationIndex(index)

                    val list: List<Relations> = mapToRelationList(getRelations.data)
                    relations.postValue(list)
                }

                if (getRelations != null && getRelations.last_updated.toLong() != 0L) {
                    val lastdate = LastUpdated(
                        AppController.mApplication.getString(R.string.relation),
                        getRelations?.last_updated.toString()
                    )
                    lastUpdated.postValue(lastdate)
                }

                if (!getRelations?.deleted.isNullOrEmpty()) {
                    removedRelations.postValue(getRelations?.deleted)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun mapToRelationList(apolloList: List<GetRelationsQuery.Data1?>?): List<Relations> {
        return apolloList?.mapNotNull { item ->
            item?.let {
                Relations(
                    id = it.id.toInt(),
                    name = it.name
                )
            }
        } ?: emptyList()
    }

//    suspend fun fetchBusinessSubCategory(index: Int) {
//        return withContext(Dispatchers.IO) {
//            try {
//                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.business_sub_category))
//                if (!date.isNullOrEmpty()) {
//                    date = (Integer.parseInt(date) + 1).toString()
//                }
//                val mJSONObject = JSONObject()
//                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
//                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
//                val response = apiRequest { api.getListBusinessSubCategory(updated) }
//                //   Log.d(TAG, "response: $response")
//                if (response.success) {
//                    db.getMasterUpdateDao().updateBusinessSubCategoryIndex(index)
//                }
//                if (!response.last_updated.isNullOrEmpty()) {
//                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.business_sub_category), response.last_updated)
//                    lastUpdated.postValue(lastdate)
//                    businessSubCategory.postValue(response.data)
//                }
//                if (!response.deleted.isNullOrEmpty()) {
//                    removedBusinessSubCategory.postValue(response.deleted)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    suspend fun fetchNative(index: Int) {
//        return withContext(Dispatchers.IO) {
//            try {
//                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string._native))
//                if (!date.isNullOrEmpty()) {
//                    date = (Integer.parseInt(date) + 1).toString()
//                }
//                val mJSONObject = JSONObject()
//                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
//                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
//                val response = apiRequest { api.getNative(updated) }
//                Log.d(TAG, "response: $response")
//                if (response.success) {
//                    db.getMasterUpdateDao().updateNativeIndex(index)
//                }
//                if (!response.last_updated.isNullOrEmpty()) {
//                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string._native), response.last_updated)
//                    lastUpdated.postValue(lastdate)
//                    native.postValue(response.data)
//                }
//                if (!response.deleted.isNullOrEmpty()) {
//                    removedNative.postValue(response.deleted)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    suspend fun fetchOccupation(index: Int) {
//        return withContext(Dispatchers.IO) {
//            try {
//                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.occupation))
//                if (!date.isNullOrEmpty()) {
//                    date = (Integer.parseInt(date) + 1).toString()
//                }
//                val mJSONObject = JSONObject()
//                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
//                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
//                val response = apiRequest { api.getOccupation(updated) }
//                Log.d(TAG, "response: $response")
//                if (response.success) {
//                    db.getMasterUpdateDao().updateOccupationIndex(index)
//                }
//                if (!response.last_updated.isNullOrEmpty()) {
//                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.occupation), response.last_updated)
//                    lastUpdated.postValue(lastdate)
//                    occupation.postValue(response.data)
//                }
//                if (!response.deleted.isNullOrEmpty()) {
//                    removedOccupation.postValue(response.deleted)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    suspend fun fetchCurrentActivity(index: Int) {
//        return withContext(Dispatchers.IO) {
//            try {
//                var date = db.getLastUpdatedDao().getLastUpdatedDate(AppController.mApplication.getString(R.string.current_activity))
//                if (!date.isNullOrEmpty()) {
//                    date = (Integer.parseInt(date) + 1).toString()
//                }
//                val mJSONObject = JSONObject()
//                mJSONObject.put(AppController.mApplication.getString(R.string.date), date)
//                val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
//                val response = apiRequest { api.getActivity(updated) }
//                Log.d(TAG, "response: $response")
//                if (response.success) {
//                    db.getMasterUpdateDao().updateCurrentActivityIndex(index)
//                }
//                if (!response.last_updated.isNullOrEmpty()) {
//                    val lastdate = LastUpdated(AppController.mApplication.getString(R.string.current_activity), response.last_updated)
//                    lastUpdated.postValue(lastdate)
//                    currentActivity.postValue(response.data)
//                }
//                if (!response.deleted.isNullOrEmpty()) {
//                    removedActivity.postValue(response.deleted)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}