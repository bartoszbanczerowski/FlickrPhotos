package eu.mobilebear.flickr.presentation.social.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import eu.mobilebear.flickr.domain.interactor.GetPhotosUseCase
import eu.mobilebear.flickr.domain.mapper.PhotoMapper
import eu.mobilebear.flickr.domain.model.PhotoModel
import eu.mobilebear.flickr.domain.model.PhotosValidationAction
import eu.mobilebear.flickr.domain.model.PhotosValidationModel
import eu.mobilebear.flickr.networking.response.responsedata.Photo
import eu.mobilebear.flickr.presentation.gallery.viewmodel.GalleryViewModel
import eu.mobilebear.flickr.util.NetworkException
import eu.mobilebear.flickr.util.state.NetworkStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class GalleryViewModelTest {

    companion object {
        private const val PHOTO_TAGS = "photoTags"
    }

    @JvmField
    @Rule
    var rule = MockitoJUnit.rule()

    @JvmField
    @Rule
    var viewModelRule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockGetPhotosUseCase: GetPhotosUseCase

    @Mock
    lateinit var mockNetworkException: NetworkException

    @Mock
    lateinit var mockPhotosValidationModel: PhotosValidationModel

    @Mock
    lateinit var mockPhoto: Photo

    @Mock
    lateinit var mockPhotoModel: PhotoModel

    @Mock
    lateinit var mockPhotoMapper: PhotoMapper

    lateinit var viewModel: GalleryViewModel

    @Before
    fun setUp() {
        viewModel = GalleryViewModel(mockGetPhotosUseCase, mockPhotoMapper)
    }

    @Test
    fun `Should clean useCase when viewModel is cleared`() {

        // when
        viewModel.onCleared()

        // then
        verify(mockGetPhotosUseCase).dispose()
    }

    @Test
    fun `Should execute call to get photos when it's called`() {
        // when
        viewModel.getPhotos(PHOTO_TAGS)

        // then
        verify(mockGetPhotosUseCase).execute(observer = any(), params = eq(PHOTO_TAGS))
        assertEquals("Network Status is Running", NetworkStatus.Running, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no photos", emptyList<PhotoModel>(), viewModel.mutableScreenState.value?.photos)
    }

    @Test
    fun `Should be in error state when get Photos return general error`() {
        //given
        viewModel.getPhotos(PHOTO_TAGS)
        val captorPostsObserver = argumentCaptor<GalleryViewModel.PhotosObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), eq(PHOTO_TAGS))
        val photosObserver = captorPostsObserver.firstValue

        // when
        photosObserver.onSuccess(PhotosValidationAction.GeneralError)

        // then
        assertEquals("Network Status is Error", NetworkStatus.Error(null), viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no photos", emptyList<PhotoModel>(), viewModel.mutableScreenState.value?.photos)
    }

    @Test
    fun `Should be in error state when get Photos return network exception`() {
        //given
        viewModel.getPhotos(PHOTO_TAGS)
        val captorPostsObserver = argumentCaptor<GalleryViewModel.PhotosObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), eq(PHOTO_TAGS))
        val photosObserver = captorPostsObserver.firstValue

        // when
        photosObserver.onError(mockNetworkException)

        // then
        assertEquals("Network Status is Network Error", NetworkStatus.NoConnectionError, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no photos", emptyList<PhotoModel>(), viewModel.mutableScreenState.value?.photos)
    }

    @Test
    fun `Should be in success state when get Photos return no photos`() {
        //given
        viewModel.getPhotos(PHOTO_TAGS)
        val captorPostsObserver = argumentCaptor<GalleryViewModel.PhotosObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), eq(PHOTO_TAGS))
        val photosObserver = captorPostsObserver.firstValue

        // when
        photosObserver.onSuccess(PhotosValidationAction.NoPhotos)

        // then
        assertEquals("Network Status is Success", NetworkStatus.Success, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no photos", emptyList<PhotoModel>(), viewModel.mutableScreenState.value?.photos)
    }

    @Test
    fun `Should be in success state when get Photos return photos`() {
        //given
        whenever(mockPhotosValidationModel.photos).thenReturn(listOf(mockPhoto))
        whenever(mockPhotoMapper.transform(mockPhoto)).thenReturn(mockPhotoModel)
        viewModel.getPhotos(PHOTO_TAGS)
        val captorPostsObserver = argumentCaptor<GalleryViewModel.PhotosObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), eq(PHOTO_TAGS))
        val photosObserver = captorPostsObserver.firstValue

        // when
        photosObserver.onSuccess(PhotosValidationAction.PhotosDownloaded(mockPhotosValidationModel))

        // then
        assertEquals("Network Status is Success", NetworkStatus.Success, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has exactly same amount of photos", listOf(mockPhotoModel), viewModel.mutableScreenState.value?.photos)
    }
}
