package com.krs.community.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.google.gson.JsonObject
import com.krs.community.SmartFilterQuery
import com.krs.community.app.AppDatabase
import com.krs.community.model.Member
import com.krs.community.responses.DeleteProfileResponse
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.type.SearchRequestDTO
import com.krs.community.type.SmartFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileDetailRepository(
    private val api: ApiServices,
    private val db: AppDatabase,
    private val apolloClient: ApolloClient
) : SafeApiRequest() {

    suspend fun changeStatus(jsonObject: JsonObject): searchByKeywordsResponse {
        return apiRequest {
            api.changeStatus(jsonObject)
        }
    }

    suspend fun updateProfile(profile: JsonObject): UpdateProfileResponse {
        return apiRequest {
            api.updateProfile(profile)
        }
    }

    suspend fun uploadProfileImage(profile: MultipartBody.Part, id: RequestBody, type: RequestBody): JsonObject {
        return apiRequest {
            api.uploadProfileImage(profile, id, type)
        }
    }

    suspend fun addProfile(profile: JsonObject): UpdateProfileResponse {
        return apiRequest {
            api.addMember(profile)
        }
    }

    suspend fun searchFilter(jsonObject: JsonObject): SmartFilterResponse {
        val start = jsonObject.get("start")?.asInt ?: 0
        val length = jsonObject.get("length")?.asInt ?: 10
        val orderBy = jsonObject.get("orderBy")?.asString
        val orderByVal = jsonObject.get("orderByVal")?.asString
        val alpha = jsonObject.get("alpha")?.asString

        val filterByJson = jsonObject.getAsJsonObject("filter_by")
        val smartFilterDto = buildSmartFilterDto(filterByJson)

        val input = SearchRequestDTO(
            start = Optional.Present(start),
            length = Optional.Present(length),
            orderBy = orderBy?.let { Optional.Present(it) } ?: Optional.Absent,
            orderByVal = orderByVal?.let { Optional.Present(it) } ?: Optional.Absent,
            alpha = alpha?.let { Optional.Present(it) } ?: Optional.Absent,
            filter_by = smartFilterDto?.let { Optional.Present(it) } ?: Optional.Absent
        )

        val response = apolloClient.query(SmartFilterQuery(input)).execute()

        val errors = response.errors?.firstOrNull()?.message
        if (!errors.isNullOrEmpty()) {
            throw Exception(errors)
        }

        val result = response.data?.smartFilter

        return SmartFilterResponse().apply {
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
                    gender = if (member.gender) "Male" else "Female"
                    birthDate = member.userPersonalDetail?.birth_date?.toString() ?: ""
                    marriageDate = member.userPersonalDetail?.marriage_date?.toString() ?: ""
                    bloodGroup = member.userPersonalDetail?.blood_group ?: ""
                    maritalStatus = member.userPersonalDetail?.marital_status ?: ""
                }
            } ?: emptyList()
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

    private fun getInt(jsonObject: JsonObject, key: String): Optional<Int> {
        return if (jsonObject.has(key)) {
            Optional.Present(jsonObject.get(key).asInt)
        } else {
            Optional.Absent
        }
    }

    private fun getString(jsonObject: JsonObject, key: String): Optional<String> {
        return if (jsonObject.has(key)) {
            Optional.Present(jsonObject.get(key).asString)
        } else {
            Optional.Absent
        }
    }

    private fun getBoolean(jsonObject: JsonObject, key: String): Optional<Boolean> {
        return if (jsonObject.has(key)) {
            Optional.Present(jsonObject.get(key).asBoolean)
        } else {
            Optional.Absent
        }
    }

    suspend fun getListCityName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityNames()
        }
    }

    suspend fun getOccupationNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupations()
        }
    }

    suspend fun getOccupationIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupationIdByName(name)
        }
    }

    suspend fun getOccupationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getOccupationDao().getOccupationById(id)
        }
    }

    suspend fun getBusinessSubCategoryNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getBusinessSubCategory()
        }
    }

    suspend fun getBusinessSubCategoryById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getBusinessSubCategoryById(id)
        }
    }

    suspend fun getSubCategoryIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getBusinessSubCategoryDao().getSubCategoryIdByName(name)
        }
    }

    suspend fun getBusinessCategoryNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategorys()
        }
    }

    suspend fun getCategoryIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getCategoryIdByName(name)
        }
    }

    suspend fun getBusinessCategoryById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getBusinessCategoryDao().getBusinessCategoryById(id)
        }
    }

    suspend fun getGotraNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotra()
        }
    }

    suspend fun getGotraIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotraIdByName(name)
        }
    }

    suspend fun getGotraById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getGotraDao().getGotraById(id)
        }
    }

    suspend fun getActivityNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivity()
        }
    }


    suspend fun getActivityIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getActivityIdByName(name)
        }
    }

    suspend fun getActivityById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getCurrentActivityDao().getCurrentActivityById(id)
        }
    }

    suspend fun getEducationNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducations()
        }
    }


    suspend fun getEducationIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducationIdByName(name)
        }
    }

    suspend fun getEducationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getEducationDao().getEducationById(id)
        }
    }

    suspend fun getNativeNames(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNative()
        }
    }

    suspend fun getNativeNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNativeById(id)
        }
    }

    suspend fun getNativeIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getNativeDao().getNativeIdByName(name)
        }
    }

    suspend fun getRelations(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelations()
        }
    }

    suspend fun getRelationById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelationById(id)
        }
    }

    suspend fun getRelationNameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getRelationNameById(id)
        }
    }

    suspend fun getIdByRelation(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getRelationsDao().getIdByRelation(name)
        }
    }

    suspend fun getIdByLastName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getIdByLastName(name)
        }
    }

    suspend fun getLastNameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName(id)
        }
    }

    suspend fun getLastName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLastNameDao().getLastName()
        }
    }

    suspend fun getLocalCommName(id: String): String {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommName(id)
        }
    }

    suspend fun getLocalCommName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommName()
        }
    }

    suspend fun getLocalCommunity(id: Int): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommNameBySubId(id)
        }
    }

    suspend fun getLocalCommunityId(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getLocalCommunityDao().getLocalCommunityId(name)
        }
    }

    suspend fun getcityNameById(id: Int): LiveData<String> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityNameById(id)
        }
    }

    suspend fun getcityById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityById(id)
        }
    }

    suspend fun getCityIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getcityIdByName(name)
        }
    }

    suspend fun getCityName(id: Int): List<String> {
        return withContext(Dispatchers.IO) {
            Log.d("SP_State", "id: " + id)
            db.getCityDao().getCityNameByState(id)
        }
    }


    suspend fun getCityId(name: String): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            db.getCityDao().getCityIdByName(name)
        }
    }

    suspend fun getstateIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getstateIdByName(name)
        }
    }

    suspend fun getstateNameById(id: Int): String {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getstateNameById(id)
        }
    }

    suspend fun getStateName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getStatesDao().getStateNames()
        }
    }

    suspend fun getSubCommIdByName(name: String): Int {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommIdByName(name)
        }
    }

    suspend fun getSubCommName(): LiveData<List<String>> {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommName()
        }
    }

    suspend fun getSubCommName(id: String): String {
        return withContext(Dispatchers.IO) {
            db.getSubCommunityDao().getSubCommunityName(id)
        }
    }

    suspend fun deleteMember(data: JsonObject): DeleteProfileResponse {
        return apiRequest {
            api.deleteMember(data)
        }
    }
}