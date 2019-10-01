package repositories

import androidx.lifecycle.MutableLiveData
import com.krs.community.model.RBCities
import com.krs.community.model.RBStates
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {

    private var registerRepository: RegisterRepository? = null

    fun getInstance(): RegisterRepository {
        if (registerRepository == null) {
            registerRepository = RegisterRepository()
        }
        return registerRepository as RegisterRepository
    }

    fun userState(): MutableLiveData<RBStates> {
        val userstate = MutableLiveData<RBStates>()
        ApiServices().getUserState()
                .enqueue(object : Callback<RBStates> {
                    override fun onFailure(call: Call<RBStates>, t: Throwable) {
                        userstate.value = null
                    }
                    override fun onResponse(call: Call<RBStates>, response: Response<RBStates>) {
                        if (response.isSuccessful) {
                            userstate.value = response.body()
                        }
                    }
                })
        return userstate
    }

    fun userCity(id: Int): MutableLiveData<RBCities> {
        val usercities = MutableLiveData<RBCities>()
        ApiServices().getUserCities(AppConstants.CitiesRequest(id.toString()))
                .enqueue(object : Callback<RBCities> {
                    override fun onFailure(call: Call<RBCities>, t: Throwable) {
                        usercities.value = null
                    }

                    override fun onResponse(call: Call<RBCities>, response: Response<RBCities>) {
                        if (response.isSuccessful) {
                            usercities.value = response.body()
                        }
                    }
                })
        return usercities
    }
}