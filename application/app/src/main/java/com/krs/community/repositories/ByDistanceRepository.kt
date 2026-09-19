package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.krs.community.NearByUsersQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.ByDistanceModel
import com.krs.community.model.Member
import com.krs.community.responses.ByDistanceResponse
import com.krs.community.type.GetNearbyUsersInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ByDistanceRepository(
    private val apolloClient: ApolloClient, private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun byDistance(distance: ByDistanceModel): ByDistanceResponse {
        val input = buildNearbyUsersInput(distance)
        return try {
            val response = apolloClient.query(NearByUsersQuery(input)).execute()

            val errors = response.errors?.firstOrNull()?.message
            if (!errors.isNullOrEmpty()) throw Exception(errors)

            val result = response.data?.nearByUsers

            ByDistanceResponse().apply {
                success = true
                message = "success"
                totalRecords = result?.totalRecords?.toString() ?: "0"
                member = result?.members?.map { m ->
                    Member().apply {
                        id = m.id.toString()
                        memberCode = m.member_code ?: ""
                        firstName = m.first_name
                        fatherName = m.father_name ?: ""
                        motherName = m.mother_name ?: ""
                        emailAddress = m.email ?: ""
                        mobile = m.mobile ?: ""
                        profilePic = m.profile_pic ?: ""
                        region = m.region ?: ""
                        nearBy = m.nearBy ?: ""
                        setDistance(m.distance ?: "")
                        setMatches(m.matchedFields ?: emptyList())
                        setMemberCount(m.member_count ?: 0)
                        subCommunityId = m.sub_community_id?.toString() ?: ""
                        localCommunityId = m.local_community_id?.toString() ?: ""
                        subCastId = m.last_name_id?.toString() ?: ""
                        role = m.role?.name ?: ""
                        headId = m.head_id?.toString() ?: "0"
                        status = m.status?.toString() ?: ""
                        gender = if (m.gender == true) "Male" else "Female"
                        m.userAddress?.let { addr ->
                            cityId = addr.city_id?.toString() ?: "0"
                            area = addr.area ?: ""
                        }
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ByDistanceResponse().apply {
                success = false
                message = e.message
            }
        }
    }

    private fun buildNearbyUsersInput(distance: ByDistanceModel): GetNearbyUsersInput {
        val lat = safeDouble(distance.lat)
        val lng = safeDouble(distance.lng)
        val km = safeInt(distance.km)
        val start = safeInt(distance.start)
        val length = safeInt(distance.length)
        val subCommunityId = safeInt(distance.sub_community_id)
        val userId = safeInt(distance.userId)

        return GetNearbyUsersInput(
            lat = lat,
            lng = lng,
            km = km,
            nearBy = distance.nearBy?.let { Optional.Present(it) } ?: Optional.Absent,
            start = start,
            length = length,
            subCommunityId = subCommunityId,
            user_id = userId
        )
    }

    private fun safeDouble(value: String?): Double {
        return try {
            value?.trim()?.toDouble()
        } catch (_: Exception) {
            0.0
        } ?: 0.0
    }

    private fun safeInt(value: String?): Optional<Int> {
        return try {
            val v = value?.trim()
            if (v.isNullOrEmpty() || v.equals("null", ignoreCase = true)) {
                Optional.Absent
            } else {
                Optional.Present(v.toInt())
            }
        } catch (_: Exception) {
            Optional.Absent
        }
    }

    fun getCityName(id: String): LiveData<String> {
        return db.getCityDao().getcityNameById(Integer.parseInt(id))
    }

    fun getLastName(id: String): LiveData<String> {
        return db.getLastNameDao().getLastNameById(Integer.parseInt(id))
    }

    fun getLocalCommName(id: String): String {
        return db.getLocalCommunityDao().getLocalCommName(id)
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }
}
