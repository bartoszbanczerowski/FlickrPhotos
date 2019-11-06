package eu.mobilebear.flickr.domain.repository

import eu.mobilebear.flickr.domain.model.PhotosValidationModel
import io.reactivex.Single

interface FlickrRepository {
    fun requestPhotos(tags: String): Single<PhotosValidationModel>
}
