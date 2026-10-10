package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.GetStatisticsQuery
import com.krs.community.app.AppDatabase
import com.krs.community.responses.StatisticResponse
import com.krs.community.responses.Statistics
import com.krs.community.type.StatisticsInputDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatisticsRepository(
    private val apolloClient: ApolloClient,
        private val db: AppDatabase
) : SafeApiRequest() {

    private val TAG: String = StatisticsRepository::class.java.simpleName
    suspend fun getStatistics(jsonObject: JsonObject): StatisticResponse {
        val filter = jsonObject.getAsJsonObject("input") ?: jsonObject
        val input = StatisticsInputDto(
            cityId = filter.intValue("cityId")?.let { Optional.Present(it) } ?: Optional.Absent,
            subCommunityId = filter.intValue("subCommunityId")?.let { Optional.Present(it) }
                ?: Optional.Absent,
            localCommunityId = filter.intValue("localCommunityId")?.let { Optional.Present(it) }
                ?: Optional.Absent
        )

        val response = apolloClient.query(GetStatisticsQuery(input)).execute()
        response.exception?.let { throw it }
        response.errors?.firstOrNull()?.message?.let { throw Exception(it) }
        val result = response.data?.getStatistics
            ?: throw Exception("Statistics response was empty")

        return StatisticResponse().apply {
            success = result.success
            data = result.data.let { statistics ->
                Statistics().apply {
                    totalVillages = statistics.TotalVillages
                    totalFamily = statistics.TotalFamily
                    totalMembers = statistics.TotalMembers
                    totalMale = statistics.TotalMale
                    totalFemale = statistics.TotalFemale
                    totalUnmarriedMale = statistics.TotalUnmarriedMale
                    totalUnmarriedFemale = statistics.TotalUnmarriedFemale
                    totalInterestedMale = statistics.TotalInterestedMale
                    totalInterestedFemale = statistics.TotalInterestedFemale
                }
            }
            cities = result.cities.map { city ->
                City().apply { cityId = city.city_id.toString() }
            }
        }
    }

    private fun JsonObject.intValue(name: String): Int? =
        get(name)?.takeUnless { it.isJsonNull }?.asInt

    suspend fun getcityIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityIdByName(name)
        }
    }

    suspend fun getCityDistinctName(citiesId: ArrayList<String>): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityDistinctName(citiesId)
        }
    }

    suspend fun getSubIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommIdByName(name)
        }
    }

    suspend fun getLocalIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommunityId(name)
        }
    }

    suspend fun getSubComm(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommName()
        }
    }

    suspend fun getLocalComm(SubId: Int): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommNameBySubId(SubId)
        }
    }

    suspend fun getCityNames(): List<String> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityListName()
        }
    }
}