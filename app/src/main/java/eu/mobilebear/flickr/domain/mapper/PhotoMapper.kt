package eu.mobilebear.flickr.domain.mapper

import eu.mobilebear.flickr.domain.model.PhotoModel
import eu.mobilebear.flickr.networking.response.responsedata.Photo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoMapper @Inject constructor() {

    fun transform(photo: Photo): PhotoModel {
        return PhotoModel(
            title = photo.title,
            author = photo.author,
            authorId = photo.authorId,
            link = photo.link,
            publishedDate = photo.publishedDate,
            takenDate = photo.takenDate,
            tags = photo.tags,
            mediaUrl = photo.media.mediaUrl
        )
    }

}
