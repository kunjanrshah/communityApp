package com.krs.community.repositories

import com.krs.community.model.CitiesModel
import com.krs.community.model.SearchByCityData
import com.krs.community.model.SearchByCityModel
import com.krs.community.model.StatesModel
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants

class BrowseCityRepository( private val api: ApiServices
): SafeApiRequest()  {

    suspend fun userState(): StatesModel {
        return apiRequest{
            api.getUserState()
        }
    }

    suspend fun userCity(id: Int): CitiesModel {
        return apiRequest{
            api.getUserCities(AppConstants.CitiesRequest(id.toString()))
        }
    }

    suspend fun userRecords(data: SearchByCityData): SearchByCityModel {
        return apiRequest{
            api.getSearchByCity(data)
        }
    }

}