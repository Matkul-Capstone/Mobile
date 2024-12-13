package com.example.speaktoo.utils

import org.json.JSONObject
import retrofit2.Response

class ErrorHandlingUtil {

    companion object {
        /**
         * Extracts an error message from a Retrofit Response.
         * @param response The Retrofit response object.
         * @return The error message if available; otherwise, a default message.
         */
        fun extractErrorMessage(response: Response<*>): String {
            return try {
                val errorBody = response.errorBody()?.string()
                if (!errorBody.isNullOrEmpty()) {
                    // Parse the error body assuming it's in JSON format
                    val json = JSONObject(errorBody)
                    json.optString("message", "An unexpected error occurred.")
                } else {
                    response.message() ?: "An unexpected error occurred."
                }
            } catch (e: Exception) {
                "An unexpected error occurred."
            }
        }

        /**
         * Extracts error details from an exception, such as HttpException or others.
         * @param throwable The throwable error.
         * @return A human-readable error message.
         */
        fun extractExceptionMessage(throwable: Throwable): String {
            return when (throwable) {
                is retrofit2.HttpException -> {
                    val response = throwable.response()
                    if (response != null) {
                        extractErrorMessage(response)
                    } else {
                        "Network error occurred. Please try again later."
                    }
                }
                else -> throwable.message ?: "An unexpected error occurred."
            }
        }
    }
}