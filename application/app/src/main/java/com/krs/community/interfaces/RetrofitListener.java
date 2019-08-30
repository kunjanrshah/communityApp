package com.krs.community.interfaces;


import com.krs.community.model.ErrorObject;

import okhttp3.ResponseBody;

public interface RetrofitListener {
    void onResponseSuccess(ResponseBody responseBody, int apiFlag);

    void onResponseError(ErrorObject errorObject, Throwable throwable, int apiFlag);
}
