package eu.mobilebear.flickr.domain.model

data class PhotoModel constructor(
        var title: String = "",
        var link: String = "",
        var mediaUrl: String = "",
        var takenDate: String = "",
        var publishedDate: String = "",
        var author: String = "",
        var authorId: String = "",
        var tags: String = ""
)
