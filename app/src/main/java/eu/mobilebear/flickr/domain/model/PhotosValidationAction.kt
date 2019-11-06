package eu.mobilebear.flickr.domain.model

sealed class PhotosValidationAction {

    class PhotosDownloaded(val photosValidationModel: PhotosValidationModel) : PhotosValidationAction()

    object NoPhotos : PhotosValidationAction()

    object GeneralError : PhotosValidationAction()
}
