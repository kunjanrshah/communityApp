package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.krs.community.GetCitiesByStateQuery
import com.krs.community.app.AppDatabase
import com.krs.community.entities.City
import com.krs.community.entities.States
import com.krs.community.model.SearchByCityData
import com.krs.community.model.SearchByCityModel
import com.krs.community.responses.CityResponse
import com.krs.community.retrofit.ApiServices
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
        return apiRequest {
            api.getSearchByCity(data)
        }
    }
}
