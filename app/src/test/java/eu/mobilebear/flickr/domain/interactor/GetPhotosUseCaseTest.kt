package eu.mobilebear.flickr.domain.interactor

import com.nhaarman.mockitokotlin2.whenever
import eu.mobilebear.flickr.domain.model.PhotosValidationAction
import eu.mobilebear.flickr.domain.model.PhotosValidationModel
import eu.mobilebear.flickr.domain.repository.FlickrRepository
import eu.mobilebear.flickr.networking.response.responsedata.Photo
import eu.mobilebear.flickr.rx.RxFactory
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class GetPhotosUseCaseTest {

    companion object {
        private const val PHOTO_TAGS = "photoTags"
    }

    @JvmField
    @Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var mockFlickrRepository: FlickrRepository

    @Mock
    lateinit var mockRxFactory: RxFactory

    @Mock
    lateinit var mockPhotosValidationModel: PhotosValidationModel

    @Mock
    lateinit var mockPhoto: Photo

    lateinit var getPhotosUseCase: GetPhotosUseCase

    @Before
    fun setUp() {
        getPhotosUseCase = GetPhotosUseCase(mockFlickrRepository, mockRxFactory)
        whenever(mockFlickrRepository.requestPhotos(PHOTO_TAGS)).thenReturn(Single.just(mockPhotosValidationModel))
    }

    @Test
    fun `Should return NO_PHOTOS action when flickr repository return no photos`() {
        // given
        whenever(mockPhotosValidationModel.status).thenReturn(PhotosValidationModel.NO_PHOTOS)

        // when
        val testObserver = getPhotosUseCase.buildUseCaseSingle(PHOTO_TAGS).test()

        // then
        testObserver
                .assertNoErrors()
                .assertComplete()
                .assertValue(PhotosValidationAction.NoPhotos)
    }

    @Test
    fun `Should return GENERAL ERROR action when flickr repository return general error`() {
        // given
        whenever(mockPhotosValidationModel.status).thenReturn(PhotosValidationModel.GENERAL_ERROR)

        // when
        val testObserver = getPhotosUseCase.buildUseCaseSingle(PHOTO_TAGS).test()

        // then
        testObserver
                .assertNoErrors()
                .assertComplete()
                .assertValue(PhotosValidationAction.GeneralError)
    }

    @Test
    fun `Should return POSTS_DOWNLOADED action when flickr repository return photos`() {
        // given
        whenever(mockPhotosValidationModel.status).thenReturn(PhotosValidationModel.PHOTOS_DOWNLOADED)

        // when
        val testObserver = getPhotosUseCase.buildUseCaseSingle(PHOTO_TAGS).test()

        // then
        testObserver
                .assertNoErrors()
                .assertComplete()
                .assertOf { PhotosValidationAction.PhotosDownloaded::class.java }
    }
}
