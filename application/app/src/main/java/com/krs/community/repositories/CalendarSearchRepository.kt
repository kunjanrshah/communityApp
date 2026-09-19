package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.UsersByDateQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.Member
import com.krs.community.responses.ReminderResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.type.GetUsersByDateInput

class CalendarSearchRepository(
    private val api: ApiServices,
    private val apolloClient: ApolloClient, private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun getSearchByDate(jsonObject: JsonObject): SmartFilterResponse {
        val input = buildGetUsersByDateInput(jsonObject)
        return try {
            val response = apolloClient.query(UsersByDateQuery(input)).execute()

            val errors = response.errors?.firstOrNull()?.message
            if (!errors.isNullOrEmpty()) throw Exception(errors)

            val result = response.data?.usersByDate

            SmartFilterResponse().apply {
                success = result?.success ?: false
                message = result?.message ?: ""
                totalRecords = result?.total_records ?: 0
                members = result?.members?.map { m ->
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
                        setMatched((m.matchedFields ?: emptyList()).joinToString(","))
                        subCommunityId = m.sub_community_id?.toString() ?: ""
                        localCommunityId = m.local_community_id?.toString() ?: ""
                        subCastId = m.last_name_id?.toString() ?: ""
                        role = m.role?.name ?: ""
                        headId = m.head_id?.toString() ?: "0"
                        status = m.status?.toString() ?: ""
                        gender = if (m.gender == true) "Male" else "Female"
                        setMemberCount(m.member_count ?: 0)
                        m.userAddress?.let { addr ->
                            cityId = addr.city_id?.toString() ?: "0"
                            area = addr.area ?: ""
                        }
                        m.userPersonalDetail?.let { detail ->
                            birthDate = detail.birth_date?.toString() ?: ""
                            marriageDate = detail.marriage_date?.toString() ?: ""
                        }
                        expireDate = m.expire_date?.toString() ?: ""
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SmartFilterResponse().apply {
                success = false
                message = e.message
            }
        }
    }

    private fun buildGetUsersByDateInput(jsonObject: JsonObject): GetUsersByDateInput {
        val fromDate = safeString(jsonObject, "fromdate")
        val toDate = safeString(jsonObject, "todate")
        val date = safeString(jsonObject, "date")
        val filter = safeString(jsonObject, "filter")
        val id = safeInt(jsonObject, "id")
        val subCommunityId = safeInt(jsonObject, "sub_community_id")
        val start = safeInt(jsonObject, "start")
        val length = safeInt(jsonObject, "length")

        return GetUsersByDateInput(
            fromdate = fromDate,
            todate = toDate,
            date = date,
            filter = filter,
            id = id,
            sub_community_id = subCommunityId,
            start = start,
            length = length
        )
    }

    private fun safeString(jsonObject: JsonObject, key: String): Optional<String?> {
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return Optional.Absent
        return try {
            val s = jsonObject.get(key).asString.trim()
            if (s.isEmpty() || s.equals("null", ignoreCase = true)) Optional.Absent
            else Optional.Present(s)
        } catch (e: Exception) {
            Optional.Absent
        }
    }

    private fun safeInt(jsonObject: JsonObject, key: String): Optional<Int?> {
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return Optional.Absent
        return try {
            val el = jsonObject.get(key)
            when {
                el.asJsonPrimitive.isNumber -> Optional.Present(el.asInt)
                el.asJsonPrimitive.isString -> {
                    val s = el.asString.trim()
                    if (s.isEmpty() || s.equals("null", ignoreCase = true)) Optional.Absent
                    else Optional.Present(s.toInt())
                }

                else -> Optional.Absent
            }
        } catch (e: Exception) {
            Optional.Absent
        }
    }

    suspend fun setReminder(jsonObject: JsonObject): ReminderResponse {
        return apiRequest {
            api.setReminder(jsonObject)
        }
    }

    fun getListCityName(): LiveData<List<String>> {
        return db.getCityDao().getcityNames()
    }

    fun getCityIdByName(name: String): Int {
        return db.getCityDao().getCityId(name)
    }

    fun getLastName(): LiveData<List<String>> {
        return db.getLastNameDao().getLastName()
    }

    fun getCityName(id: String): String {
        return db.getCityDao().getcityName(Integer.parseInt(id))
    }

    fun getLastNameById(id: Int): String {
        return db.getLastNameDao().getLastName(id)
    }

    fun getIdByLastName(name: String): Int {
        return db.getLastNameDao().getIdOfLastName(name)
    }
}
