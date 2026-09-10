package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.krs.community.GetCitiesByStateQuery
import com.krs.community.SearchByCityQuery
import com.krs.community.app.AppDatabase
import com.krs.community.entities.City
import com.krs.community.entities.States
import com.krs.community.model.Member
import com.krs.community.model.SearchByCityData
import com.krs.community.model.SearchByCityModel
import com.krs.community.responses.CityResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.type.SearchByCityInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BrowseCityRepository(
    private val api: ApiServices,
    private val apolloClient: ApolloClient, private val db: AppDatabase
) : SafeApiRequest() {


    suspend fun getStates(): LiveData<List<States>> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getStates()
        }
    }

    suspend fun getCityByStateId(id: Int): LiveData<List<City>> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getCityById(id)
        }
    }

    suspend fun getLastnameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName(id)
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }

    suspend fun getLocalCommunityById(id: String): String {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommName(id)
        }
    }

    suspend fun getCitiesByState(stateId: Int, subCommunityId: Int): CityResponse {
        return try {
            val response = apolloClient.query(
                GetCitiesByStateQuery(stateId, subCommunityId)
            ).execute()

            val result = response.data?.getCitiesByState

            CityResponse().apply {
                success = true
                message = null
                last_updated = result?.last_updated
                deleted = result?.deleted?.map { it.toString() }

                data = result?.data?.map {
                    City(
                        id = it.id,
                        name = it.name,
                        parent_id = subCommunityId ?: 0,
                        count = it.count
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CityResponse().apply {
                success = false
                message = e.message
            }
        }
    }

    suspend fun userRecords(data: SearchByCityData): SearchByCityModel {
        val input = SearchByCityInput(
            cityId = data.filterBy?.cityId?.toIntOrNull() ?: 0,
            subCommunityId = data.sub_community_id?.toIntOrNull()?.let { Optional.Present(it) }
                ?: Optional.Absent,
            alpha = data.alpha?.let { Optional.Present(it) } ?: Optional.Absent,
            search = data.search?.let { Optional.Present(it) } ?: Optional.Absent,
            start = data.start?.toIntOrNull()?.let { Optional.Present(it) } ?: Optional.Absent,
            length = data.length?.toIntOrNull()?.let { Optional.Present(it) } ?: Optional.Absent
        )
        return try {
            val response = apolloClient.query(
                SearchByCityQuery(input)
            ).execute()

            val result = response.data?.searchByCity

            SearchByCityModel().apply {
                success = result?.success ?: false
                totalHead = result?.totalHead ?: 0
                totalMem = result?.totalMem ?: 0
                members = result?.members?.map { member ->
                    Member().apply {
                        id = member.id.toString()
                        role = member.role.name
                        headId = member.head_id.toString()
                        memberCode = member.member_code ?: ""
                        emailAddress = member.email ?: ""
                        mobile = member.mobile ?: ""
                        firstName = member.first_name
                        subCastId = member.last_name_id.toString()
                        fatherName = member.father_name ?: ""
                        motherName = member.mother_name ?: ""
                        status = member.status.toString()
                        gender = if (member.gender) "Male" else "Female"
                        profilePic = member.profile_pic
                        member.userAddress?.let { addr ->
                            address = addr.address
                            cityId = addr.city_id.toString()
                            stateId = addr.states_id.toString()
                            area = addr.area ?: ""
                            pincode = addr.pincode ?: ""
                        }
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SearchByCityModel().apply {
                success = false
                members = emptyList()
            }
        }
    }
}
