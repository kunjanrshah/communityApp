package com.krs.community.repositories

import com.apollographql.apollo.ApolloClient
import com.google.gson.JsonObject
import com.krs.community.StatusChangeMutation
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.type.StatusChangeInputDto

internal suspend fun ApolloClient.changeMemberStatus(payload: JsonObject): searchByKeywordsResponse {
    val input = StatusChangeInputDto(
        idList = payload.requireString("idList"),
        status = payload.requireInt("status"),
        id = payload.requireInt("id")
    )
    val response = mutation(StatusChangeMutation(input)).execute()
    response.errors?.firstOrNull()?.message?.let { throw Exception(it) }
    val statusChange = response.data?.statusChange
    if (statusChange == null) {
        throw Exception(
            response.exception?.message
                ?: "Status change returned no data"
        )
    }

    return searchByKeywordsResponse().apply {
        success = statusChange.success
        message = statusChange.message
    }
}

private fun JsonObject.requireString(name: String): String {
    val value = get(name)
    require(value != null && !value.isJsonNull && value.isJsonPrimitive) {
        "Missing or invalid '$name' for status change"
    }
    return value.asString
}

private fun JsonObject.requireInt(name: String): Int {
    val value = get(name)
    require(value != null && !value.isJsonNull && value.isJsonPrimitive) {
        "Missing or invalid '$name' for status change"
    }
    return value.asInt
}
