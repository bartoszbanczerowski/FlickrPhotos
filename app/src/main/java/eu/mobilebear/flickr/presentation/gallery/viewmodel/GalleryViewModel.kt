package eu.mobilebear.flickr.presentation.gallery.viewmodel

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.mobilebear.flickr.domain.interactor.GetPhotosUseCase
import eu.mobilebear.flickr.domain.mapper.PhotoMapper
import eu.mobilebear.flickr.domain.model.PhotoModel
import eu.mobilebear.flickr.domain.model.PhotosValidationAction
import eu.mobilebear.flickr.networking.response.responsedata.Photo
import eu.mobilebear.flickr.util.state.NetworkStatus
import io.reactivex.observers.DisposableSingleObserver
import javax.inject.Inject

class GalleryViewModel @Inject constructor(private val getPhotosUseCase: GetPhotosUseCase,
                                           private val photosMapper: PhotoMapper) :
        ViewModel() {

    @VisibleForTesting
    internal val mutableScreenState: MutableLiveData<ScreenState> = MutableLiveData()

    val posts: LiveData<ScreenState> by lazy { mutableScreenState }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        super.onCleared()
        getPhotosUseCase.dispose()
    }

    @MainThread
    fun getPhotos(tags: String) {
        mutableScreenState.value = ScreenState(emptyList(), NetworkStatus.Running)
        getPhotosUseCase.execute(PhotosObserver(), tags)
    }

    @VisibleForTesting
    inner class PhotosObserver : DisposableSingleObserver<PhotosValidationAction>() {
        override fun onSuccess(action: PhotosValidationAction) {
            when (action) {
                is PhotosValidationAction.PhotosDownloaded -> showPhotos(mapPhotos(action.photosValidationModel.photos))
                is PhotosValidationAction.NoPhotos -> showNoPostsInfo()
                is PhotosValidationAction.GeneralError -> showError(null)
            }
        }

        private fun mapPhotos(photos: List<Photo>): List<PhotoModel> = photos.map { photo: Photo -> photosMapper.transform(photo) }.toList()

        override fun onError(error: Throwable) {
            showError(error)
        }
    }

    private fun showPhotos(posts: List<PhotoModel>) {
        mutableScreenState.value = ScreenState(
                posts = posts,
                networkStatus = NetworkStatus.Success
        )
    }

    private fun showNoPostsInfo() {
        mutableScreenState.value = ScreenState(
                posts = emptyList(),
                networkStatus = NetworkStatus.Success
        )
    }

    private fun showError(e: Throwable?) {
        val posts = mutableScreenState.value?.posts
        mutableScreenState.value = ScreenState(
                posts,
                NetworkStatus.error(e)
        )
    }

    fun sortByDate(isChecked: Boolean) {
            val sortedPosts = mutableScreenState.value?.posts!!
                    .sortedBy { photoModel -> if(isChecked) photoModel.takenDate else photoModel.publishedDate }
                    .toList()
        mutableScreenState.value = ScreenState(
                sortedPosts,
                networkStatus = NetworkStatus.Success
        )
    }

    data class ScreenState(
            val posts: List<PhotoModel>?,
            val networkStatus: NetworkStatus
    )
}
