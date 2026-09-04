package com.krs.community.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApiLogInterceptor(
    private val tag: String = "ApiLog"
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBody = original.body
        val requestBuffer = Buffer()
        requestBody?.writeTo(requestBuffer)
        val requestString = requestBuffer.readUtf8()

        val requestLog = buildString {
            appendLine("══════════════════════════════════════════════")
            appendLine("REQUEST | ${getCurrentTime()}")
            appendLine("URL      : ${original.url}")
            appendLine("METHOD   : ${original.method}")
            appendLine("HEADERS  :")
            original.headers.toMap().forEach { (name, value) ->
                appendLine("  $name: $value")
            }
            if (!requestString.isNullOrEmpty()) {
                appendLine("BODY     :")
                appendLine(requestString)
            }
            appendLine("══════════════════════════════════════════════")
        }

        Log.d(tag, requestLog)

        val newRequest = original.newBuilder().apply {
            if (requestBody != null && requestString.isNotEmpty()) {
                val contentType = requestBody.contentType()
                val newBody = okhttp3.RequestBody.create(contentType, requestString)
                method(original.method, newBody)
            }
        }.build()

        val response: Response
        try {
            response = chain.proceed(newRequest)
        } catch (e: Exception) {
            val errorLog = buildString {
                appendLine("══════════════════════════════════════════════")
                appendLine("ERROR   | ${getCurrentTime()}")
                appendLine("MESSAGE  : ${e.message}")
                appendLine("══════════════════════════════════════════════")
            }
            Log.e(tag, errorLog)
            throw e
        }

        val responseBody = response.body
        val responseString = responseBody?.string()

        val responseLog = buildString {
            appendLine("══════════════════════════════════════════════")
            appendLine("RESPONSE | ${getCurrentTime()}")
            appendLine("URL       : ${original.url}")
            appendLine("STATUS    : ${response.code} ${response.message}")
            appendLine("HEADERS   :")
            response.headers.toMap().forEach { (name, value) ->
                appendLine("  $name: $value")
            }
            if (!responseString.isNullOrEmpty()) {
                appendLine("BODY      :")
                appendLine(responseString)
            }
            appendLine("══════════════════════════════════════════════")
        }

        Log.d(tag, responseLog)

        val newResponseBody = responseBody?.let {
            ResponseBody.create(responseBody.contentType(), responseString ?: "")
        }

        return response.newBuilder()
            .body(newResponseBody)
            .build()
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            .format(Date())
    }
}
