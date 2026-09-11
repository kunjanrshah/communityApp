package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.GetSharedProfilesQuery
import com.krs.community.GetSharingProfilesQuery
import com.krs.community.SmartFilterQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.type.SearchRequestDTO
import com.krs.community.type.SmartFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SmartFilterRepository(
    private val api: ApiServices,
    private val db: AppDatabase,
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    suspend fun searchByName(jsonObject: JsonObject): SmartFilterResponse {
        val start = safeInt(jsonObject, "start") ?: 0
        val lengthRaw = safeInt(jsonObject, "length")
        // SmartFilterResult first page sends start=0,length="" →treat empty as default 10
        val length = lengthRaw ?: 10
        val orderBy = safeString(jsonObject, "orderBy")
        val orderByVal = safeString(jsonObject, "orderByVal")
        val alpha = safeString(jsonObject, "alpha")

        val filterByJson = try {
            if (jsonObject.has("filter_by") && jsonObject.get("filter_by").isJsonObject)
                jsonObject.getAsJsonObject("filter_by") else null
        } catch (e: Exception) {
            null
        }
        val smartFilterDto = buildSmartFilterDto(filterByJson)

        val input = SearchRequestDTO(
            start = Optional.Present(start),
            length = Optional.Present(length),
            orderBy = orderBy?.let { Optional.Present(it) } ?: Optional.Absent,
            orderByVal = orderByVal?.let { Optional.Present(it) } ?: Optional.Absent,
            alpha = alpha?.let { Optional.Present(it) } ?: Optional.Absent,
            filter_by = smartFilterDto?.let { Optional.Present(it) } ?: Optional.Absent
        )

        return try {
            val response = apolloClient.query(SmartFilterQuery(input)).execute()

            val errors = response.errors?.firstOrNull()?.message
            if (!errors.isNullOrEmpty()) {
                throw Exception(errors)
            }

            val result = response.data?.smartFilter

            SmartFilterResponse().apply {
                success = true
                message = "success"
                totalRecords = result?.totalRecords ?: 0
                members = result?.members?.map { member ->
                    Member().apply {
                        id = member.id.toString()
                        firstName = member.first_name ?: ""
                        fatherName = member.father_name ?: ""
                        motherName = member.mother_name ?: ""
                        memberCode = member.member_code ?: ""
                        subCommunityId = member.sub_community_id?.toString() ?: ""
                        localCommunityId = member.local_community_id?.toString() ?: ""
                        subCastId = member.last_name_id?.toString() ?: ""
                        emailAddress = member.email ?: ""
                        mobile = member.mobile ?: ""
                        gender = if (member.gender) "Male" else "Female"
                        status = member.status.toString()
                        headId = member.head_id.toString()
                        role = member.role?.toString() ?: ""
                        profilePic = member.profile_pic ?: ""
                        region = member.region ?: ""
                        isExpired = member.is_expired.toString()
                        educationId = member.education_id?.toString() ?: ""
                        occupationId = member.occupation_id?.toString() ?: ""
                        address = member.userAddress?.address ?: ""
                        localAddress = member.userAddress?.local_address ?: ""
                        cityId = member.userAddress?.city_id?.toString() ?: ""
                        stateId = member.userAddress?.states_id?.toString() ?: ""
                        area = member.userAddress?.area ?: ""
                        pincode = member.userAddress?.pincode ?: ""
                        maritalStatus = member.userPersonalDetail?.marital_status ?: ""
                        birthDate = member.userPersonalDetail?.birth_date?.toString() ?: ""
                        marriageDate = member.userPersonalDetail?.marriage_date?.toString() ?: ""
                        bloodGroup = member.userPersonalDetail?.blood_group ?: ""
                        nativePlaceId = member.userPersonalDetail?.native_place_id?.toString() ?: ""
                        gotraId = member.userPersonalDetail?.gotra_id?.toString() ?: ""
                        isDonor = member.userPersonalDetail?.is_donor.toString()
                        matrimony =
                            if (member.userPersonalDetail?.matrimony == true) "YES" else "NO"
                        businessCategoryId =
                            member.userWorkDetail?.business_category_id?.toString() ?: ""
                        businessAddress = member.userWorkDetail?.business_address ?: ""
                        birthPlace = member.userMatrimony?.birth_place_id?.toString() ?: ""
                        isSpect = member.userMatrimony?.is_spect.toString()
                        isMangal = member.userMatrimony?.is_mangal.toString()
                        isShani = member.userMatrimony?.is_shani.toString()
                    }
                } ?: emptyList()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun buildSmartFilterDto(filterByJson: JsonObject?): SmartFilterDto? {
        if (filterByJson == null) return null
        return SmartFilterDto(
            id = getInt(filterByJson, "id"),
            last_name_id = getInt(filterByJson, "last_name_id"),
            local_community_id = getInt(filterByJson, "local_community_id"),
            sub_community_id = getInt(filterByJson, "sub_community_id"),
            native_place_id = getInt(filterByJson, "native_place_id"),
            city_id = getInt(filterByJson, "city_id"),
            member_code = getString(filterByJson, "member_code"),
            head_name = getString(filterByJson, "head_name"),
            first_name = getString(filterByJson, "first_name"),
            father_name = getString(filterByJson, "father_name"),
            mother_name = getString(filterByJson, "mother_name"),
            gender = getBoolean(filterByJson, "gender"),
            marital_status = getString(filterByJson, "marital_status"),
            min_age = getInt(filterByJson, "min_age"),
            max_age = getInt(filterByJson, "max_age"),
            min_height = getInt(filterByJson, "min_height"),
            max_height = getInt(filterByJson, "max_height"),
            min_weight = getInt(filterByJson, "min_weight"),
            max_weight = getInt(filterByJson, "max_weight"),
            min_percentage = getInt(filterByJson, "min_percentage"),
            max_percentage = getInt(filterByJson, "max_percentage"),
            state_id = getInt(filterByJson, "state_id"),
            email_address = getString(filterByJson, "email_address"),
            mobile = getString(filterByJson, "mobile"),
            local_address = getString(filterByJson, "local_address"),
            address = getString(filterByJson, "address"),
            pincode = getString(filterByJson, "pincode"),
            area = getString(filterByJson, "area"),
            business_address = getString(filterByJson, "business_address"),
            education_id = getInt(filterByJson, "education_id"),
            gotra_id = getInt(filterByJson, "gotra_id"),
            business_category_id = getInt(filterByJson, "business_category_id"),
            occupation_id = getInt(filterByJson, "occupation_id"),
            birth_place_id = getInt(filterByJson, "birth_place_id"),
            birth_date = getString(filterByJson, "birth_date"),
            marriage_date = getString(filterByJson, "marriage_date"),
            expire_date = getString(filterByJson, "expire_date"),
            updated_dt = getString(filterByJson, "updated_dt"),
            blood_group = getString(filterByJson, "blood_group"),
            is_donor = getBoolean(filterByJson, "is_donor"),
            is_rented = getBoolean(filterByJson, "is_rented"),
            is_expired = getBoolean(filterByJson, "is_expired"),
            is_spect = getBoolean(filterByJson, "is_spect"),
            matrimony = getBoolean(filterByJson, "matrimony"),
            is_shani = getBoolean(filterByJson, "is_shani"),
            is_mangal = getBoolean(filterByJson, "is_mangal"),
            str_search = getString(filterByJson, "str_search"),
            committee_id = getInt(filterByJson, "committee_id"),
            designation_id = getInt(filterByJson, "designation_id"),
            start_date = getString(filterByJson, "start_date"),
            end_date = getString(filterByJson, "end_date")
        )
    }

    // Shared JsonObject-safe helpers (null + "" + type tolerant)
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

    private fun getInt(jsonObject: JsonObject, key: String): Optional<Int?> {
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

    private fun getString(jsonObject: JsonObject, key: String): Optional<String?> {
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return Optional.Absent
        return try {
            val s = jsonObject.get(key).asString.trim()
            if (s.isEmpty()) Optional.Absent else Optional.Present(s)
        } catch (e: Exception) {
            Optional.Absent
        }
    }

    private fun getBoolean(jsonObject: JsonObject, key: String): Optional<Boolean?> {
        if (!jsonObject.has(key) || jsonObject.get(key).isJsonNull) return Optional.Absent
        val el = jsonObject.get(key)
        return try {
            when {
                el.isJsonPrimitive && el.asJsonPrimitive.isBoolean -> Optional.Present(el.asBoolean)
                el.isJsonPrimitive && el.asJsonPrimitive.isNumber -> Optional.Present(el.asInt != 0)
                el.isJsonPrimitive && el.asJsonPrimitive.isString -> {
                    val s = el.asString.trim().lowercase()
                    when (s) {
                        "", "null" -> Optional.Absent
                        "true", "1", "yes", "male", "m" -> Optional.Present(true)
                        "false", "0", "no", "female", "f" -> Optional.Present(false)
                        else -> Optional.Present(
                            s.toBooleanStrictOrNull() ?: return Optional.Absent
                        )
                    }
                }

                else -> Optional.Absent
            }
        } catch (e: Exception) {
            Optional.Absent
        }
    }

    suspend fun searchByUser(jsonObject: JsonObject): LoginResponse {
        return apiRequest {
            api.getUserProfile(jsonObject)
        }
    }

    suspend fun getSharedProfile(jsonObject: JsonObject): SmartFilterResponse {
        val userId = jsonObject.get("id")?.asString?.toIntOrNull()
            ?: jsonObject.get("user_id")?.asString?.toIntOrNull()
            ?: throw IllegalArgumentException("id/user_id is required for getSharedProfile")

        val sharedResponse = apolloClient.query(GetSharedProfilesQuery(userId)).execute()
        val sharingResponse = apolloClient.query(GetSharingProfilesQuery(userId)).execute()

        val sharedErrors = sharedResponse.errors?.firstOrNull()?.message
        if (!sharedErrors.isNullOrEmpty()) {
            throw Exception(sharedErrors)
        }
        val sharingErrors = sharingResponse.errors?.firstOrNull()?.message
        if (!sharingErrors.isNullOrEmpty()) {
            throw Exception(sharingErrors)
        }

        val response = SmartFilterResponse()
        response.success = true
        response.message = "success"
        response.members = mapGraphUsersToMembers(sharedResponse.data?.getSharedProfiles)
        response.membersharing =
            mapGraphSharingUsersToMembers(sharingResponse.data?.getSharingProfiles)
        response.totalRecords = response.members?.size ?: 0
        return response
    }

    private fun mapGraphUsersToMembers(apolloUsers: List<GetSharedProfilesQuery.GetSharedProfile>?): List<Member> {
        return apolloUsers?.map { user ->
            Member().apply {
                id = user.id.toString()
                firstName = user.first_name ?: ""
                subCastId = user.last_name_id?.toString() ?: ""
                memberCode = user.member_code ?: ""
                mobile = user.mobile ?: ""
                emailAddress = user.email ?: ""
                status = user.status.toString()
            }
        } ?: emptyList()
    }

    private fun mapGraphSharingUsersToMembers(apolloUsers: List<GetSharingProfilesQuery.GetSharingProfile>?): List<Member> {
        return apolloUsers?.map { user ->
            Member().apply {
                id = user.id.toString()
                firstName = user.first_name ?: ""
                subCastId = user.last_name_id?.toString() ?: ""
                memberCode = user.member_code ?: ""
                mobile = user.mobile ?: ""
                emailAddress = user.email ?: ""
                status = user.status.toString()
            }
        } ?: emptyList()
    }

    suspend fun getInActiveRecords(jsonObject: JsonObject): SmartFilterResponse {
        return apiRequest {
            api.getInActiveUsers(jsonObject)
        }
    }

    suspend fun getNativeById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative(id)
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

    suspend fun getCityName(id: String): String {
        return db.getCityDao().getcityName(Integer.parseInt(id))
    }

    fun getSubCommunity(id: String): String {
        return db.getSubCommunityDao().getSubCommunityName(id)
    }

    fun getLocalCommunity(id: String): String {
        return db.getLocalCommunityDao().getLocalCommName(id)
    }

    suspend fun getLastNameById(id: Int): String {
        return db.getLastNameDao().getLastName(id)
    }

    fun getIdByLastName(name: String): Int {
        return db.getLastNameDao().getIdOfLastName(name)
    }
}