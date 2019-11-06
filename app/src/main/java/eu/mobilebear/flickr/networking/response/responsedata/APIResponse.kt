package eu.mobilebear.flickr.networking.response.responsedata

import com.squareup.moshi.Json

data class APIResponse(
        @Json(name = "title") var title: String = "",
        @Json(name = "link") var link: String = "",
        @Json(name = "description") var description: String = "",
        @Json(name = "modified") var address: String = "",
        @Json(name = "generator") var generator: String = "",
        @Json(name = "items") var photos: List<Photo> = emptyList()
)