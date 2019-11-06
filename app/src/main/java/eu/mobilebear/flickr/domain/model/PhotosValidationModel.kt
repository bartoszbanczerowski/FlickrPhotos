package eu.mobilebear.flickr.domain.model

import androidx.annotation.StringDef
import eu.mobilebear.flickr.networking.response.responsedata.Photo

data class PhotosValidationModel(
        val photos: List<Photo>,
        @PhotosStatus val status: String
) {
    companion object {
        const val PHOTOS_DOWNLOADED = "PHOTOS_DOWNLOADED"
        const val NO_PHOTOS = "NO_PHOTOS"
        const val GENERAL_ERROR = "GENERAL_LIST"

        @Retention(AnnotationRetention.SOURCE)
        @StringDef(PHOTOS_DOWNLOADED, NO_PHOTOS, GENERAL_ERROR)
        annotation class PhotosStatus
    }
}
