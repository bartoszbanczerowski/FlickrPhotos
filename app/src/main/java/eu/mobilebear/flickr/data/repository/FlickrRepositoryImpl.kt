package eu.mobilebear.flickr.data.repository

import eu.mobilebear.flickr.domain.model.PhotosValidationModel
import eu.mobilebear.flickr.domain.repository.FlickrRepository
import eu.mobilebear.flickr.networking.FlickrService
import eu.mobilebear.flickr.networking.response.responsedata.APIResponse
import eu.mobilebear.flickr.util.ConnectionChecker
import eu.mobilebear.flickr.util.NetworkException
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickrRepositoryImpl
@Inject
constructor(
        private val flickrService: FlickrService,
        private val connectionChecker: ConnectionChecker
) : FlickrRepository {

    override fun requestPhotos(tags: String): Single<PhotosValidationModel> {
        if (!connectionChecker.isConnected) return Single.error(NetworkException())
        return flickrService.getPhotos(FlickrService.FLICKR_API_KEY, FlickrService.JSON_FORMAT, FlickrService.RAW_JSON, tags)
                .map { response -> mapPhotosResponse(response) }
                .onErrorReturn { mapPhotosError() }
    }

    private fun mapPhotosResponse(response: Response<APIResponse>): PhotosValidationModel {
        val isResponseSuccessful = response.isSuccessful && response.body() != null

        return if (isResponseSuccessful) {
            if (response.body()!!.photos.isEmpty()) {
                PhotosValidationModel(response.body()!!.photos, PhotosValidationModel.NO_PHOTOS)
            } else {
                PhotosValidationModel(response.body()!!.photos, PhotosValidationModel.PHOTOS_DOWNLOADED)
            }
        } else {
            mapPhotosError()
        }
    }

    private fun mapPhotosError(): PhotosValidationModel = PhotosValidationModel(emptyList(), PhotosValidationModel.GENERAL_ERROR)
}

