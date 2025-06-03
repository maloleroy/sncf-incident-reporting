package com.sncf.reports.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Custom type adapters for Gson to handle UUID and Instant serialization/deserialization
 * compatible with Python's FastAPI output
 */
object GsonTypeAdapters {
    
    /**
     * UUID adapter that handles Python's string representation of UUIDs
     */
    class UUIDAdapter : JsonSerializer<UUID>, JsonDeserializer<UUID> {
        override fun serialize(src: UUID, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.toString())
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UUID {
            return UUID.fromString(json.asString)
        }
    }

    /**
     * Instant adapter that handles Python's ISO 8601 datetime format
     */
    class InstantAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun serialize(src: Instant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(src))
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant {
            return Instant.parse(json.asString)
        }
    }

    /**
     * Creates a configured Gson instance with all required type adapters
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(UUID::class.java, UUIDAdapter())
            .registerTypeAdapter(Instant::class.java, InstantAdapter())
            .create()
    }
}
