package com.fiap.autria

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class BackendClient(
    private val baseUrl: String = BuildConfig.API_BASE_URL.trimEnd('/'),
    private val apiKey: String = BuildConfig.API_KEY,
) {
    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private val http = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .build()

    private fun request(path: String): Request.Builder = Request.Builder()
        .url("$baseUrl$path")
        .apply { if (apiKey.isNotBlank()) header("X-API-Key", apiKey) }

    suspend fun getAppState(): NavigationState = withContext(Dispatchers.IO) {
        http.newCall(request("/app/state").get().build()).execute().use { response ->
            if (!response.isSuccessful) throw IOException("API respondeu HTTP ${response.code}")
            val root = JSONObject(response.body?.string().orEmpty())
            val device = root.getJSONObject("device")
            val sensor = root.getJSONObject("sensor")
            val navigation = root.getJSONObject("navigation")
            val directions = navigation.getJSONObject("directions")

            NavigationState(
                connected = device.getBoolean("connected"),
                battery = device.getInt("battery_percent"),
                distanceCm = if (sensor.isNull("distance_cm")) null else sensor.getDouble("distance_cm").toInt(),
                sensorValid = sensor.getBoolean("valid"),
                action = navigation.getString("action"),
                guideMessage = navigation.getString("message"),
                urgent = navigation.getBoolean("urgent"),
                speaking = navigation.getBoolean("speak"),
                leftFree = directions.getBoolean("left"),
                centerFree = directions.getBoolean("center"),
                rightFree = directions.getBoolean("right"),
                audioEnabled = device.getBoolean("audio_enabled"),
                volumePercent = device.getInt("volume_percent"),
                vibrationEnabled = device.getBoolean("vibration_enabled"),
                vibrationPattern = device.getString("vibration_pattern"),
                backendOnline = true,
            )
        }
    }

    suspend fun setAudioEnabled(enabled: Boolean, current: NavigationState) =
        sendJson(
            path = "/device/settings",
            method = "PATCH",
            body = JSONObject()
                .put("audio_enabled", enabled)
                .put("volume_percent", current.volumePercent)
                .put("vibration_enabled", current.vibrationEnabled)
                .put("vibration_pattern", current.vibrationPattern),
        )

    suspend fun triggerEmergency() = sendJson(
        path = "/emergencies",
        method = "POST",
        body = JSONObject()
            .put("confirmed", true)
            .put("latitude", 0.0)
            .put("longitude", 0.0)
            .put("message", "Preciso de ajuda. Emergência acionada pelo aplicativo Autria."),
    )

    private suspend fun sendJson(path: String, method: String, body: JSONObject) =
        withContext(Dispatchers.IO) {
            val payload = body.toString().toRequestBody(jsonType)
            val builder = request(path)
            when (method) {
                "PATCH" -> builder.patch(payload)
                else -> builder.post(payload)
            }
            http.newCall(builder.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    val detail = response.body?.string()?.let {
                        runCatching { JSONObject(it).optString("detail") }.getOrNull()
                    }
                    throw IOException(detail?.takeIf { it.isNotBlank() } ?: "HTTP ${response.code}")
                }
            }
        }
}
