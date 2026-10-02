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
        val payload = extractSearchInput(jsonObject)
        val start = safeInt(payload, "start") ?: 0
        val length = safeInt(payload, "length") ?: 10
        val filterBy = safeString(payload, "filterBy")
            ?: safeString(payload, "filter_by")
            ?: safeString(payload, "str_search")
        val requestedSubCommunityId = safeInt(payload, "sub_community_id")
            ?: safeInt(payload, "subCommunityId")
        val mergedFilterBy = mergeFilterBy(filterBy, requestedSubCommunityId)

        val input = SearchInput(
            start = Optional.Present(start),
            length = Optional.Present(length),
            filterBy = mergedFilterBy?.let { Optional.Present(it) } ?: Optional.Absent
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

    private fun extractSearchInput(jsonObject: JsonObject): JsonObject {
        if (jsonObject.has("variables") && jsonObject.get("variables").isJsonObject) {
            val variables = jsonObject.getAsJsonObject("variables")
            if (variables.has("input") && variables.get("input").isJsonObject) {
                return variables.getAsJsonObject("input")
            }
        }

        if (jsonObject.has("input") && jsonObject.get("input").isJsonObject) {
            return jsonObject.getAsJsonObject("input")
        }

        return jsonObject
    }

    private fun mergeFilterBy(filterBy: String?, subCommunityId: Int?): String? {
        val base = filterBy?.trim()?.takeIf { it.isNotEmpty() }
        // Treat null/0/negative as "no sub-community filter".
        val community = subCommunityId
            ?.takeIf { it > 0 }
            ?.toString()
            ?: return base

        if (base.isNullOrEmpty()) return "sub_community_id:$community"
        if (base.contains("sub_community_id", ignoreCase = true)) return base

        return "$base sub_community_id:$community"
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