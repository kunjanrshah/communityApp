package com.krs.community.repositories

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.SearchCommitteeUsersQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.Member
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.type.SearchCommitteeUsersInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommitteeRepository(
    private val api: ApiServices,
    private val db: AppDatabase,
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    suspend fun getUsersInCommittee(data: JsonObject): SmartFilterResponse {
        val start = safeInt(data, "start") ?: 0
        val lengthRaw = safeInt(data, "length")
        val length = lengthRaw ?: 10

        val filterByJson = try {
            if (data.has("filter_by") && data.get("filter_by").isJsonObject)
                data.getAsJsonObject("filter_by") else null
        } catch (e: Exception) {
            null
        }

        val input = SearchCommitteeUsersInput(
            start = Optional.Present(start),
            length = Optional.Present(length),
            committeeId = getOptionalInt(filterByJson, "committee_id"),
            relationId = getOptionalInt(filterByJson, "relation_id"),
            designationId = getOptionalInt(filterByJson, "designation_id"),
            localCommunityId = getOptionalInt(filterByJson, "local_community_id"),
            startDate = getOptionalString(filterByJson, "start_date"),
            endDate = getOptionalString(filterByJson, "end_date"),
            name = getOptionalString(filterByJson, "str_search")
        )

        return try {
            val response = apolloClient.query(SearchCommitteeUsersQuery(input)).execute()

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
                        lastName = member.last_name ?: ""
                        memberCode = member.member_code ?: ""
                        mobile = member.mobile ?: ""
                        committee = member.committee ?: ""
                        designation = member.designation ?: ""
                        city = member.city ?: ""
                        state = member.state ?: ""
                        relation = member.relation ?: ""
                        emailAddress = member.email ?: ""
                        fatherName = member.father_name ?: ""
                        motherName = member.mother_name ?: ""
                        status = member.status.toString()
                        gender = if (member.gender) "Male" else "Female"
                        headId = member.head_id.toString()
                        head_name = member.head_name ?: ""
                        profilePic = member.profile_pic ?: ""
                        region = member.region ?: ""
                        isExpired = member.is_expired.toString()
                        subCommunityId = member.sub_community_id?.toString() ?: "0"
                        localCommunityId = member.local_community_id?.toString() ?: "0"
                        subCastId = member.last_name_id?.toString() ?: "0"
                        educationId = member.education_id?.toString() ?: "0"
                        occupationId = member.occupation_id?.toString() ?: "0"
                        subCommunity = member.sub_community ?: ""
                        localCommunity = member.local_community ?: ""
                        education = member.education ?: ""
                        occupation = member.occupation ?: ""
                        currentActivity = member.current_activity ?: ""
                        gotra = member.gotra ?: ""
                        nativePlace = member.native ?: ""
                        businessCategory = member.business_category ?: ""
                        businessSubCategory = member.business_sub_category ?: ""
                        mossad = member.mossad ?: ""
                        profileCompleted = member.profile_completed ?: ""
                        onlineStatus = member.online_status ?: 0
                        loginStatus = if (member.login_status == true) 1 else 0
                        lastLogin = member.last_login?.toString() ?: ""
                        profilePercentage = member.profile_percent?.toString() ?: "0"
                        matched = member.matchedFields?.joinToString(",") ?: ""
                        memberCount = member.member_count ?: 0
                        distance = member.distance ?: ""
                        nearBy = member.nearBy ?: ""
                        expireDate = member.expire_date?.toString() ?: ""
                        member.userAddress?.let { addr ->
                            address = addr.address ?: ""
                            localAddress = addr.local_address ?: ""
                            cityId = addr.city_id?.toString() ?: "0"
                            stateId = addr.states_id?.toString() ?: "0"
                            area = addr.area ?: ""
                            pincode = addr.pincode ?: ""
                        }
                        member.userPersonalDetail?.let { pd ->
                            nativePlaceId = pd.native_place_id?.toString() ?: "0"
                            bloodGroup = pd.blood_group ?: ""
                            maritalStatus = pd.marital_status ?: ""
                            birthDate = pd.birth_date?.toString() ?: ""
                            gotraId = pd.gotra_id?.toString() ?: "0"
                            isDonor = pd.is_donor.toString()
                            matrimony = if (pd.matrimony == true) "YES" else "NO"
                            marriageDate = pd.marriage_date?.toString() ?: ""
                        }
                        member.userWorkDetail?.let { wd ->
                            businessCategoryId = wd.business_category_id?.toString() ?: "0"
                            businessAddress = wd.business_address ?: ""
                            companyName = wd.company_name ?: ""
                            website = wd.website ?: ""
                            workDetails = wd.work_details ?: ""
                        }
                        member.userMatrimony?.let { um ->
                            birthPlace = um.birth_place_id?.toString() ?: ""
                            isSpect = um.is_spect.toString()
                            isMangal = um.is_mangal.toString()
                            isShani = um.is_shani.toString()
                            birthTime = um.birth_time ?: ""
                            hobby = um.hobby ?: ""
                            aboutMe = um.about_me ?: ""
                        }
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