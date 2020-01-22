package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.app.lazyDeferred
import com.krs.community.interfaces.EditMemberListener
import com.krs.community.repositories.ProfileDetailRepository
import com.krs.community.responses.UpdateProfileResponse
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class ProfileDetailViewModel(
        private val mProfileDetailRepository: ProfileDetailRepository,
        var app: Application) : AndroidViewModel(app) {

    private lateinit var job_by_update: CompletableJob
    private lateinit var completableJob: CompletableJob

    var TAG: String = ProfileDetailViewModel::class.java.simpleName
    lateinit var mEditMemberListener: EditMemberListener

    var selectedRelationId=0
    lateinit var lstRelationId:List<Int>
    val relationName by lazyDeferred {
        mProfileDetailRepository.getRelationById(selectedRelationId)
    }
    val lstRelationName by lazyDeferred {
        mProfileDetailRepository.getRelations()
    }
    val relationIds by lazyDeferred {
        mProfileDetailRepository.getRelationIds()
    }

    var selectedLastNameId=0
    lateinit var lstLastNameId:List<Int>
    val lastName by lazyDeferred {
        mProfileDetailRepository.getLastNameById(selectedLastNameId)
    }
    val lstLastName by lazyDeferred {
        mProfileDetailRepository.getLastName()
    }
    val lastNameIds by lazyDeferred {
        mProfileDetailRepository.getLastNameIds()
    }

    val getLocalCommName by lazyDeferred {
        mProfileDetailRepository.getLocalCommName()
    }

    var selectedStateId=0
    lateinit var lstStateId:List<Int>
    val stateName by lazyDeferred {
        mProfileDetailRepository.getstateNameById(selectedStateId)
    }
    val lstStateName by lazyDeferred {
        mProfileDetailRepository.getStateName()
    }
    val stateIds by lazyDeferred {
        mProfileDetailRepository.getStateIds()
    }

    var selectedCityId:Int=0
    lateinit var selectedCityName:String
    val cityName by lazyDeferred {
        mProfileDetailRepository.getcityNameById(selectedCityId)
    }
    val cityId by lazyDeferred {
        mProfileDetailRepository.getCityId(selectedCityName)
    }


    val lstCityName by lazyDeferred {
        mProfileDetailRepository.getListCityName()
    }



    var selectedNativeId=0
    lateinit var lstNativeId:List<Int>
    val nativeName by lazyDeferred {
        mProfileDetailRepository.getNativeNameById(selectedNativeId)
    }
    val lstNativeName by lazyDeferred {
        mProfileDetailRepository.getNativeNames()
    }
    val nativeIds by lazyDeferred {
        mProfileDetailRepository.getNativeIds()
    }

    var selectedEducationId=0
    lateinit var lstEducationId:List<Int>
    val educationName by lazyDeferred {
        mProfileDetailRepository.getEducationById(selectedEducationId)
    }
    val lstEducationName by lazyDeferred {
        mProfileDetailRepository.getEducationNames()
    }
    val educationIds by lazyDeferred {
        mProfileDetailRepository.getEducationIds()
    }

    var selectedActivityId=0
    lateinit var lstActivityId:List<Int>
    val activityName by lazyDeferred {
        mProfileDetailRepository.getActivityById(selectedActivityId)
    }
    val lstActivityName by lazyDeferred {
        mProfileDetailRepository.getActivityNames()
    }
    val activityIds by lazyDeferred {
        mProfileDetailRepository.getActivityIds()
    }

    var selectedGotraId=0
    lateinit var lstGotraId:List<Int>
    val gotraName by lazyDeferred {
        mProfileDetailRepository.getGotraById(selectedGotraId)
    }
    val lstGotraName by lazyDeferred {
        mProfileDetailRepository.getGotraNames()
    }
    val gotraIds by lazyDeferred {
        mProfileDetailRepository.getGotraIds()
    }

    var selectedBusinessCategoryId=0
    lateinit var lstBusinessCategoryId:List<Int>
    val businessCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessCategoryById(selectedBusinessCategoryId)
    }
    val lstBusinessCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessCategoryNames()
    }
    val businessCategoryIds by lazyDeferred {
        mProfileDetailRepository.getBusinessCategoryIds()
    }

    var selectedBusinessSubCategoryId=0
    lateinit var lstBusinessSubCategoryId:List<Int>
    val businessSubCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessSubCategoryById(selectedBusinessCategoryId)
    }
    val lstBusinessSubCategoryName by lazyDeferred {
        mProfileDetailRepository.getBusinessSubCategoryNames()
    }
    val businessSubCategoryIds by lazyDeferred {
        mProfileDetailRepository.getBusinessSubCategoryIds()
    }

    var selectedOccupationId=0
    lateinit var lstOccupationId:List<Int>
    val occupationName by lazyDeferred {
        mProfileDetailRepository.getOccupationById(selectedOccupationId)
    }
    val lstOccupationName by lazyDeferred {
        mProfileDetailRepository.getOccupationNames()
    }
    val occupationIds by lazyDeferred {
        mProfileDetailRepository.getOccupationId()
    }

    suspend fun getCityNamebyState(id:Int):List<String>{
       return mProfileDetailRepository.getCityName(id)
    }

    fun getMemberByFilters(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mProfileDetailRepository.searchFilter(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mEditMemberListener.getMembers(response)
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

    fun updateProfile(profile: JsonObject,isEdit:Boolean) {
        job_by_update = Job()
        job_by_update.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response: UpdateProfileResponse
                    if(isEdit){
                        response = mProfileDetailRepository.updateProfile(profile)
                    }else{
                        response = mProfileDetailRepository.addProfile(profile)
                    }

                    response.let {
                        withContext(Dispatchers.Main) {
                            mEditMemberListener.getMessage(response)
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