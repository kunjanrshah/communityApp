package com.krs.community.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krs.community.BuildConfig;
import com.krs.community.R;
import com.krs.community.interfaces.RetrofitListener;
import com.krs.community.model.ErrorObject;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.Logger;
import com.krs.community.utils.Utility;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBase {
    public ApiServices apiServices;
    protected Context context;
    private Logger logger;



    public RetrofitBase(Context context, boolean addTimeout) {
        this.context = context;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder().addInterceptor(interceptor);
        if (addTimeout) {
            httpClientBuilder.readTimeout(AppConstants.TimeOut.SOCKET_TIME_OUT, TimeUnit.SECONDS);
            httpClientBuilder.connectTimeout(AppConstants.TimeOut.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        } else {
            httpClientBuilder.readTimeout(AppConstants.TimeOut.IMAGE_UPLOAD_SOCKET_TIMEOUT, TimeUnit.SECONDS);
            httpClientBuilder.connectTimeout(AppConstants.TimeOut.IMAGE_UPLOAD_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        }
        addingHeaders(httpClientBuilder);
        OkHttpClient httpClient = httpClientBuilder.build();

        logger = new Logger(RetrofitBase.class.getSimpleName());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.APPLICATION_BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiServices=retrofit.create(ApiServices.class);

    }

    private void addingHeaders(OkHttpClient.Builder builder) {

        builder.interceptors().add(chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader(context.getString(R.string.apikey), "q1fgdfggfw2e2rt3y5u6i8iug12fh123yhhddaf")
                    .addHeader(context.getString(R.string.devicetoken), "123468")
                    .addHeader(context.getString(R.string.intudid), "145dfdfs")
                    .build();
            return chain.proceed(request);
        });
    }

    void validateResponse(Response response, RetrofitListener retrofitListener, int apiFlag) {
        if (response.code() == 200) {
            ResponseBody responseBody = (ResponseBody) response.body();
            try {
                retrofitListener.onResponseSuccess(responseBody, apiFlag);
            } catch (Exception e) {
                error(response, retrofitListener, apiFlag);
            }
        } else {
            error(response, retrofitListener, apiFlag);
        }
    }

    private void error(Response response, RetrofitListener retrofitListener, int apiFlag) {
        Gson gson = new Gson();
        ErrorObject errorPojo;
        try {
            errorPojo = gson.fromJson((response.errorBody()).string(), ErrorObject.class);
            if (errorPojo == null) {
                errorPojo = Utility.getServerErrorPojo(context);
            }
            retrofitListener.onResponseError(errorPojo, null, apiFlag);
        } catch (Exception e) {
            retrofitListener.onResponseError(Utility.getServerErrorPojo(context), null, apiFlag);
        }
    }
}
