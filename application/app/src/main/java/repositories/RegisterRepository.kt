package repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krs.community.model.RBStates
import com.krs.community.retrofit.ApiServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {

    fun userState(): LiveData<String> {
        val userstate = MutableLiveData<String>()
        ApiServices().getUserState()
                .enqueue(object : Callback<RBStates> {
                    override fun onFailure(call: Call<RBStates>, t: Throwable) {
                        userstate.value = t.message
                    }

                    override fun onResponse(call: Call<RBStates>, response: Response<RBStates>) {

                        if (response.isSuccessful) {
                            userstate.value = response.body().toString()
                        } else {
                            userstate.value = response.errorBody().toString()
                        }
                    }
                })
        return userstate
    }
}