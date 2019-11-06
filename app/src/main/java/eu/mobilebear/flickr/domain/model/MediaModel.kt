package eu.mobilebear.flickr.domain.model

import com.squareup.moshi.Json

data class MediaModel constructor(
        @Json(name = "m") var mediaUrl: String = ""
)
