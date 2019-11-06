package eu.mobilebear.flickr.domain.interactor

import eu.mobilebear.flickr.rx.RxFactory
import eu.mobilebear.flickr.domain.interactor.base.SingleUseCase
import eu.mobilebear.flickr.domain.model.PhotosValidationAction
import eu.mobilebear.flickr.domain.model.PhotosValidationModel
import eu.mobilebear.flickr.domain.repository.FlickrRepository
import io.reactivex.Single
import javax.inject.Inject

class GetPhotosUseCase
@Inject constructor(
        private val flickRepository: FlickrRepository,
        rxFactory: RxFactory
) : SingleUseCase<PhotosValidationAction, String>(IO_THREAD, rxFactory) {

    override fun buildUseCaseSingle(params: String?): Single<PhotosValidationAction> {
        if (params == null) {
            return Single.just(PhotosValidationAction.GeneralError)
        }

        return flickRepository.requestPhotos(params)
                .flatMap { photosValidationModel: PhotosValidationModel -> mapIntoPhotosAction(photosValidationModel) }
    }

    private fun mapIntoPhotosAction(photosValidationModel: PhotosValidationModel): Single<PhotosValidationAction> =
            Single.just(when (photosValidationModel.status) {
                PhotosValidationModel.PHOTOS_DOWNLOADED -> PhotosValidationAction.PhotosDownloaded(photosValidationModel)
                PhotosValidationModel.NO_PHOTOS -> PhotosValidationAction.NoPhotos
                else -> PhotosValidationAction.GeneralError
            })
}
