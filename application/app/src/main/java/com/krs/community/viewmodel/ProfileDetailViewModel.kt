package com.krs.community.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.app.lazyDeferred
import com.krs.community.listeners.EditMemberListener
import com.krs.community.listeners.ImageUploadListener
import com.krs.community.repositories.ProfileDetailRepository
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException

import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileDetailViewModel(
        private val mProfileDetailRepository: ProfileDetailRepository,
        var app: Application) : AndroidViewModel(app) {

    private lateinit var job_by_update: CompletableJob
    private lateinit var completableJob: CompletableJob

    var TAG: String = ProfileDetailViewModel::class.java.simpleName
    lateinit var mEditMemberListener: EditMemberListener
    lateinit var mImageUploadListener: ImageUploadListener

    var selectedRelationId = 0
    val relationName by lazyDeferred {
        mProfileDetailRepository.getRelationById(selectedRelationId)
    }
    val lstRelationName by lazyDeferred {
        mProfileDetailRepository.getRelations()
    }

    var selectedLastNameId = 0
    val lastName by lazyDeferred {
        mProfileDetailRepository.getLastNameById(selectedLastNameId)
    }

    suspend fun getLastNameById(id: Int): String {
        return mProfileDetailRepository.getLastNameById(id)
    }

    val lstLastName by lazyDeferred {
        mProfileDetailRepository.getLastName()
    }

    val getLocalCommName by lazyDeferred {
        mProfileDetailRepository.getLocalCommName()
    }

    suspend fun getLocalCommunity(id: Int): LiveData<List<String>> {
        return mProfileDetailRepository.getLocalCommunity(id)
    }

    suspend fun getSubCommIdByName(name:String):Int{
        return mProfileDetailRepository.getSubCommIdByName(name)
    }

    var selectedStateId = 0
    val stateName by lazyDeferred {
        mProfileDetailRepository.getstateNameById(selectedStateId)
    }

    suspend fun getstateNameById(id: Int): String {
        return mProfileDetailRepository.getstateNameById(id)
    }

    val lstStateName by lazyDeferred {
        mProfileDetailRepository.getStateName()
    }

    val lstSubCommName by lazyDeferred {
        mProfileDetailRepository.getSubCommName()
    }

    var selectedCityId: Int = 0
    lateinit var selectedCityName: String
    val cityName by lazyDeferred {
        mProfileDetailRepository.getcityNameById(selectedCityId)
    }

    suspend fun getcityName(id: Int): LiveData<String> {
        return mProfileDetailRepository.getcityNameById(id)
    }

    val lstCityName by lazyDeferred {
        mProfileDetailRepository.getListCityName()
    }

    var selectedNativeId = 0
    val nativeName by lazyDeferred {
        mProfileDetailRepository.getNativeNameById(selectedNativeId)
    }

    val lstNativeName by lazyDeferred {
        mProfileDetailRepository.getNativeNames()
    }

    var selectedEducationId = 0
    val educationName by lazyDeferred {
        mProfileDetailRepository.getEducationById(selectedEducationId)
    }
    val lstEducationName by lazyDeferred {
        mProfileDetailRepository.getEducationNames()
    }

    var selectedActivityId = 0
    val activityName by lazyDeferred {
        mProfileDetailRepository.getActivityById(selectedActivityId)
    }
    val lstActivityName by lazyDeferred {
        mProfileDetailRepository.getActivityNames()
    }

    var selectedGotraId = 0
    val gotraName by lazyDeferred {
        mProfileDetailRepository.getGotraById(selectedGotraId)
    }
    val lstGotraName by lazyDeferred {
        mProfileDetailRepository.getGotraNames()
    }

    var selectedBusinessCategoryId = 0
    val businessCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessCategoryById(selectedBusinessCategoryId)
    }
    val lstBusinessCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessCategoryNames()
    }

    var selectedBusinessSubCategoryId = 0
    val businessSubCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessSubCategoryById(selectedBusinessCategoryId)
    }
    val lstBusinessSubCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessSubCategoryNames()
    }

    var selectedOccupationId = 0
    val occupationName by lazyDeferred {
        mProfileDetailRepository.getOccupationById(selectedOccupationId)
    }
    val lstOccupationName by lazyDeferred {
        mProfileDetailRepository.getOccupationNames()
    }

    suspend fun getCityNamebyState(id: Int): List<String> {
        return mProfileDetailRepository.getCityName(id)}

    suspend fun getActivityIdByName(name:String):Int{
        return mProfileDetailRepository.getActivityIdByName(name)
    }

    suspend fun getOccupationIdByName(name:String):Int{
        return mProfileDetailRepository.getOccupationIdByName(name)
    }

    suspend fun getSubCategoryIdByName(name:String):Int{
        return mProfileDetailRepository.getSubCategoryIdByName(name)
    }

    suspend fun getCategoryIdByName(name:String):Int{
        return mProfileDetailRepository.getCategoryIdByName(name)
    }

    suspend fun getGotraIdByName(name:String):Int{
        return mProfileDetailRepository.getGotraIdByName(name)
    }

    suspend fun getEducationIdByName(name:String):Int{
        return mProfileDetailRepository.getEducationIdByName(name)
    }

    suspend fun getstateIdByName(name:String):Int{
        return mProfileDetailRepository.getstateIdByName(name)
    }

    suspend fun getCityIdByName(name:String):Int{
        return mProfileDetailRepository.getCityIdByName(name)
    }

    suspend fun getNativeIdByName(name:String):Int{
        return mProfileDetailRepository.getNativeIdByName(name)
    }

    suspend fun getLocalCommunityId(name:String):Int{
        return mProfileDetailRepository.getLocalCommunityId(name)
    }

    suspend fun getIdByLastName(name:String):Int{
        return mProfileDetailRepository.getIdByLastName(name)
    }

    suspend fun getIdByRelation(name: String): Int {
        return mProfileDetailRepository.getIdByRelation(name)
    }

    fun getMemberByFilters(jsonObject: JsonObject) {
        if (isNetworkConnected(app.applicationContext)) {
            completableJob = Job()
            completableJob.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response = mProfileDetailRepository.searchFilter(jsonObject)
                        response.let {
                            withContext(Dispatchers.Main) {
                                mEditMemberListener.getScanResult(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mEditMemberListener.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mEditMemberListener.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mEditMemberListener.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun uploadImage(file: File, id: String, type: String) {
        if (isNetworkConnected(app.applicationContext)) {
            job_by_update = Job()
            job_by_update.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val requestFile = RequestBody.create(
                                "image/*".toMediaTypeOrNull(),
                                file
                        )
                        val body = MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
                        val id = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                id)


                        val _type = RequestBody.create(
                                "text/plain".toMediaTypeOrNull(),
                                type)

                        val response: JsonObject = mProfileDetailRepository.uploadProfileImage(body, id, _type)

                        response.let {
                            withContext(Dispatchers.Main) {
                                Log.d("Response", response.toString())
                                if (response.get("success").asString.equals("success")) {
                                    mImageUploadListener.getResult(response.getAsJsonObject("data"))
                                } else {
                                    mImageUploadListener.onFailure(response.get("message").asString)
                                }
                                response.get("data")
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mImageUploadListener.onFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mImageUploadListener.onFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mImageUploadListener.onFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun updateProfile(profile: JsonObject, isEdit: Boolean) {
        if (isNetworkConnected(app.applicationContext)) {
            job_by_update = Job()
            job_by_update.let { thejob ->

                CoroutineScope(Dispatchers.IO + thejob).launch {
                    try {
                        val response: UpdateProfileResponse
                        if (isEdit) {
                            response = mProfileDetailRepository.updateProfile(profile)
                        } else {
                            response = mProfileDetailRepository.addProfile(profile)
                        }

                        response.let {
                            withContext(Dispatchers.Main) {
                                mEditMemberListener.getUpdateOrAddResult(response)
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let {
                            mEditMemberListener.getFailure(it)
                        }
                    } catch (e: NoInternetException) {
                        e.message?.let {
                            mEditMemberListener.getFailure(it)
                        }
                    } catch (e: Exception) {
                        e.message?.let {
                            mEditMemberListener.getFailure(it)
                        }
                    }
                    thejob.complete()
                }
            }
        }
    }
}