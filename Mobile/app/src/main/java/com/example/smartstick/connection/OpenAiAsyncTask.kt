package com.example.smartstick.connection

import android.os.AsyncTask
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

class OpenAIManager(private val apiKey: String, private val listener: OnOpenAIResponseListener)
{
    fun sendQuestion(question: String) {
        OpenAIAsyncTask(apiKey, question, listener).execute()
    }
    @Suppress("DEPRECATION")
    private class OpenAIAsyncTask(
        private val apiKey: String,
        private val question: String,
        private val listener: OnOpenAIResponseListener
    ) : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String? {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, "{\"question\": \"$question\"}")
            val request = Request.Builder()
                .url("https://api.openai.com/v1/engines/davinci-codex/completions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            return try {
                val response = client.newCall(request).execute()
                response.body?.string()
            } catch (e: IOException) {
                null
            }
        }
        override fun onPostExecute(result: String?) {
            if (result != null) {
                val answer = parseAnswerFromResponse(result)
                listener.onResponse(answer)
            } else {
                listener.onError("API request failed.")
            }
        }
        private fun parseAnswerFromResponse(responseString: String): String {
            val json = JSONObject(responseString)
            return json.getJSONArray("choices").getJSONObject(0).getString("text")
        }
    }
    interface OnOpenAIResponseListener {
        fun onResponse(answer: String)
        fun onError(errorMessage: String)
    }
}