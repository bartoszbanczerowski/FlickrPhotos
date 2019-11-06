package eu.mobilebear.flickr.networking.response.responsedata

import com.squareup.moshi.Json

data class Media constructor(
        @Json(name = "m") var mediaUrl: String = ""
)
