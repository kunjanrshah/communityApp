package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloHttpException
import com.krs.community.GetCitiesByStateQuery
import com.krs.community.SearchByCityQuery
import com.krs.community.app.AppDatabase
import com.krs.community.auth.TokenManager
import com.krs.community.auth.TokenRefreshApi
import com.krs.community.auth.TokenRefreshCallbackRegistry
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
    private val apolloClient: ApolloClient,
    private val db: AppDatabase,
    private val tokenManager: TokenManager,
    private val tokenRefreshApi: TokenRefreshApi
) : SafeApiRequest() {

    private val tag = BrowseCityRepository::class.java.simpleName

    private enum class AuthResult {
        SessionExpired,
        TokenRefreshed,
        NotAuthError
    }

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
            val query = GetCitiesByStateQuery(stateId, subCommunityId)
            var response = apolloClient.query(query).execute()

            response.errors?.let { errors ->
                when (handleGraphQLErrors(errors)) {
                    AuthResult.TokenRefreshed -> response = apolloClient.query(query).execute()
                    AuthResult.SessionExpired -> {
                        return CityResponse().apply {
                            success = false
                            message = "Session expired"
                        }
                    }

                    AuthResult.NotAuthError -> Unit
                }
            }

            response.exception?.let { exception ->
                when (handleApolloException(exception)) {
                    AuthResult.TokenRefreshed -> response = apolloClient.query(query).execute()
                    AuthResult.SessionExpired -> {
                        return CityResponse().apply {
                            success = false
                            message = "Session expired"
                        }
                    }

                    AuthResult.NotAuthError -> Unit
                }
            }

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
            val query = SearchByCityQuery(input)
            var response = apolloClient.query(query).execute()

            response.errors?.let { errors ->
                when (handleGraphQLErrors(errors)) {
                    AuthResult.TokenRefreshed -> response = apolloClient.query(query).execute()
                    AuthResult.SessionExpired -> {
                        return SearchByCityModel().apply {
                            success = false
                            members = emptyList()
                        }
                    }

                    AuthResult.NotAuthError -> Unit
                }
            }

            response.exception?.let { exception ->
                when (handleApolloException(exception)) {
                    AuthResult.TokenRefreshed -> response = apolloClient.query(query).execute()
                    AuthResult.SessionExpired -> {
                        return SearchByCityModel().apply {
                            success = false
                            members = emptyList()
                        }
                    }

                    AuthResult.NotAuthError -> Unit
                }
            }

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
                        memberCount = member.member_count ?: 0
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

    private suspend fun handleGraphQLErrors(errors: List<Error>): AuthResult {
        val hasUnauthorizedError = errors.any { error ->
            val statusCode = (error.extensions as? Map<*, *>)?.get("statusCode")?.toString()
            val message = error.message.lowercase()
            statusCode == "401" || message.contains("unauthorized") || message.contains("401")
        }
        if (!hasUnauthorizedError) {
            return AuthResult.NotAuthError
        }

        Log.w(tag, "GraphQL unauthorized error detected, attempting token refresh")
        return handleAuthFailure()
    }

    private suspend fun handleApolloException(exception: Throwable): AuthResult {
        if (exception is ApolloHttpException && exception.statusCode == 401) {
            Log.w(tag, "HTTP 401 detected, attempting token refresh")
            return handleAuthFailure()
        }
        return AuthResult.NotAuthError
    }

    private suspend fun handleAuthFailure(): AuthResult = withContext(Dispatchers.IO) {
        val refreshToken = tokenManager.refreshToken
        if (refreshToken.isNullOrBlank()) {
            tokenManager.clearTokens()
            TokenRefreshCallbackRegistry.notifySessionExpired()
            return@withContext AuthResult.SessionExpired
        }

        when (val result = tokenRefreshApi.refreshToken(refreshToken)) {
            is TokenRefreshApi.RefreshResult.Success -> {
                tokenManager.saveTokens(result.accessToken, result.refreshToken)
                AuthResult.TokenRefreshed
            }

            is TokenRefreshApi.RefreshResult.Failure -> {
                if (result.error is TokenRefreshApi.SessionExpired) {
                    tokenManager.clearTokens()
                    TokenRefreshCallbackRegistry.notifySessionExpired()
                    AuthResult.SessionExpired
                } else {
                    AuthResult.NotAuthError
                }
            }
        }
    }
}
