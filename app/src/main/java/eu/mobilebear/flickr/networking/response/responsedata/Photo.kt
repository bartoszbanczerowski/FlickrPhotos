package eu.mobilebear.flickr.networking.response.responsedata

import com.squareup.moshi.Json

data class Photo constructor(
        @Json(name = "title") var title: String = "",
        @Json(name = "link") var link: String = "",
        @Json(name = "media") var media: Media = Media(),
        @Json(name = "date_taken") var takenDate: String = "",
        @Json(name = "published") var publishedDate: String = "",
        @Json(name = "author") var author: String = "",
        @Json(name = "author_id") var authorId: String = "",
        @Json(name = "tags") var tags: String = ""
)
