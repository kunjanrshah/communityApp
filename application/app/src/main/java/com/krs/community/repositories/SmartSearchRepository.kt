package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.SmartSearchQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.Member
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.type.SearchInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmartSearchRepository(
    private val apolloClient: ApolloClient,
    private val db: AppDatabase
) : SafeApiRequest() {

    suspend fun searchByKeyword(jsonObject: JsonObject): searchByKeywordsResponse {
        val start = safeInt(jsonObject, "start") ?: 0
        val length = safeInt(jsonObject, "length") ?: 10
        val filterBy = safeString(jsonObject, "filterBy")

        val input = SearchInput(
            start = Optional.Present(start),
            length = Optional.Present(length),
            filterBy = filterBy?.let { Optional.Present(it) } ?: Optional.Absent
        )

        try {
            val response = apolloClient.query(SmartSearchQuery(input)).execute()
            val errors = response.errors?.firstOrNull()?.message
            if (!errors.isNullOrEmpty()) throw Exception(errors)

            val result = response.data?.smartSearch

            return searchByKeywordsResponse().apply {
                success = true
                message = "success"
                totalRecords = result?.totalRecords?.toString() ?: "0"
                member = result?.members?.map { m ->
                    Member().apply {
                        id = m.id.toString()
                        firstName = m.first_name ?: ""
                        memberCode = m.member_code ?: ""
                        emailAddress = m.email ?: ""
                        mobile = m.mobile ?: ""
                        subCommunityId = m.sub_community_id?.toString() ?: ""
                        // map matchedFields → matches
                        setMatches(m.matchedFields ?: emptyList())
                        setMemberCount(m.member_count ?: 0)
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            // Re-throw so callers can handle via existing error handling
            throw e
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
        }
    }

    fun getLastName(id: Int): LiveData<String> {
        return db.getLastNameDao().getLastNameById(id)
    }

    fun getCityName(id: String): LiveData<String> {
        return db.getCityDao().getcityNameById(Integer.parseInt(id))
    }

    fun getRelationName(id: String): String {
        return db.getRelationsDao().getRelationNameById(Integer.parseInt(id))
    }

    fun getLocalCommName(id: String): String {
        return db.getLocalCommunityDao().getLocalCommName(id)
    }

    // Helpers copied from SmartFilterRepository patterns
    private fun safeString(jsonObject: JsonObject, key: String): String? {
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return null
        return try {
            val s = jsonObject.get(key).asString.trim()
            if (s.isEmpty() || s.equals("null", true)) null else s
        } catch (e: Exception) {
            null
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
}