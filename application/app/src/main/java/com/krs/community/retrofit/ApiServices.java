package com.krs.community.retrofit;



import com.krs.community.model.RBStates;
import com.krs.community.utils.AppConstants;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServices {

    @POST(AppConstants.UrlPath.GET_CITIES)
    Call<ResponseBody> getCities(@Body AppConstants.StateRequest request);

    @POST(AppConstants.UrlPath.GET_STATE)
    Call<RBStates> getStates();

    @POST(AppConstants.UrlPath.GET_SUBCASTE)
    Call<ResponseBody> getSubCaste();

}
