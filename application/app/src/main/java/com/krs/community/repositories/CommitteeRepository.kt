package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.SearchCommitteeUsersQuery
import com.krs.community.app.AppDatabase
import com.krs.community.auth.TokenManager
import com.krs.community.auth.TokenRefreshApi
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.type.SearchCommitteeUsersFilterInput
import com.krs.community.type.SearchCommitteeUsersInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommitteeRepository(
    private val db: AppDatabase,
    private val apolloClient: ApolloClient,
    private val tokenManager: TokenManager,
    private val tokenRefreshApi: TokenRefreshApi
) : SafeApiRequest() {

    suspend fun getUsersInCommittee(data: JsonObject): SmartFilterResponse {
        val start = safeInt(data, "start") ?: 0
        val lengthRaw = safeInt(data, "length")
        val length = lengthRaw ?: 25

        val filterByJson = try {
            if (data.has("filter_by") && data.get("filter_by").isJsonObject)
                data.getAsJsonObject("filter_by") else null
        } catch (e: Exception) {
            null
        }

        val input = SearchCommitteeUsersInput(
            start = Optional.Present(start),
            length = Optional.Present(length),
            filterBy = getOptionalString(data, "filterBy"),
            committeeId = getOptionalInt(data, "committeeId"),
            designationId = getOptionalInt(data, "designationId"),
            filter_by = filterByJson?.let { filter ->
                Optional.Present(
                    SearchCommitteeUsersFilterInput(
                        str_search = getOptionalString(filter, "str_search"),
                        local_community_id = getOptionalInt(filter, "local_community_id"),
                        sub_community_id = getOptionalInt(filter, "sub_community_id"),
                        committee_id = getOptionalInt(filter, "committee_id"),
                        designation_id = getOptionalInt(filter, "designation_id"),
                        start_date = getOptionalString(filter, "start_date"),
                        end_date = getOptionalString(filter, "end_date")
                    )
                )
            } ?: Optional.Absent
        )

        return try {
            val response = executeWithTokenRefresh(tokenManager, tokenRefreshApi) {
                apolloClient.query(SearchCommitteeUsersQuery(input)).execute()
            }

            response.exception?.let { throw it }
            val errors = response.errors?.firstOrNull()?.message
            if (!errors.isNullOrEmpty()) {
                throw Exception(errors)
            }

            val result = response.data?.searchCommitteeUsers

            SmartFilterResponse().apply {
                success = result?.success ?: false
                message = result?.message ?: ""
                totalRecords = result?.total_records ?: 0
                members = result?.members?.map { member ->
                    Member().apply {
                        id = member.id.toString()
                        firstName = member.first_name ?: ""
                        memberCode = member.member_code ?: ""
                        committee = member.committee ?: ""
                        committeeId = member.committee_id?.toString() ?: "0"
                        designation = member.designation ?: ""
                        designationId = member.designation_id?.toString() ?: "0"
                        subCommunity = member.sub_community ?: ""
                        subCommunityId = member.sub_community_id?.toString() ?: "0"
                        localCommunity = member.local_community ?: ""
                        localCommunityId = member.local_community_id?.toString() ?: "0"
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun safeInt(jsonObject: JsonObject, key: String): Int? {
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return null
        return try {
            val el = jsonObject.get(key)
            when {
                el.asJsonPrimitive.isNumber -> el.asInt
                el.asJsonPrimitive.isString -> {
                    val s = el.asString.trim()
                    if (s.isEmpty() || s.equals("null", true)) null else s.toInt()
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getOptionalInt(jsonObject: JsonObject?, key: String): Optional<Int?> {
        if (jsonObject == null) return Optional.Absent
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return Optional.Absent
        val el = jsonObject.get(key)
        return try {
            when {
                el.isJsonPrimitive && el.asJsonPrimitive.isNumber -> Optional.Present(el.asInt)
                el.isJsonPrimitive && el.asJsonPrimitive.isString -> {
                    val s = el.asString.trim()
                    if (s.isEmpty()) Optional.Absent else Optional.Present(s.toInt())
                }

                else -> Optional.Absent
            }
        } catch (e: Exception) {
            Optional.Absent
        }
    }

    private fun getOptionalString(jsonObject: JsonObject?, key: String): Optional<String?> {
        if (jsonObject == null) return Optional.Absent
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return Optional.Absent
        return try {
            val s = jsonObject.get(key).asString.trim()
            if (s.isEmpty()) Optional.Absent else Optional.Present(s)
        } catch (e: Exception) {
            Optional.Absent
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }

    suspend fun getLastnameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName(id)
        }
    }

    suspend fun getLocalCommunityById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommName(id.toString())
        }
    }

    suspend fun getCommitteeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getCommitteeDao().getCommitteeNameById(id)
        }
    }

    suspend fun getDesignationById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getDesignationDao().getDesignationNameById(id)
        }
    }


    suspend fun getLocalCommunity(id: Int): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommNameBySubId(id)
        }
    }

    suspend fun getCommitteeList(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCommitteeDao().getCommitteeNames()
        }
    }

    suspend fun getDesignationList(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getDesignationDao().getDesignationName()
        }
    }

    suspend fun getLocalCommunityName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommunityId(name)
        }
    }

    suspend fun getCommitteeName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCommitteeDao().getCommitteeName(name)
        }
    }

    suspend fun getDesignationName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getDesignationDao().getDesignationName(name)
        }
    }
}