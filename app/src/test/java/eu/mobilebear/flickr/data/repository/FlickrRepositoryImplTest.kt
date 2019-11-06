package eu.mobilebear.flickr.data.repository

import com.nhaarman.mockitokotlin2.whenever
import eu.mobilebear.flickr.domain.model.PhotosValidationModel
import eu.mobilebear.flickr.networking.FlickrService
import eu.mobilebear.flickr.networking.response.responsedata.APIResponse
import eu.mobilebear.flickr.networking.response.responsedata.Photo
import eu.mobilebear.flickr.util.ConnectionChecker
import eu.mobilebear.flickr.util.NetworkException
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import retrofit2.Response

class FlickrRepositoryImplTest {

    companion object {
        private const val PHOTO_TAGS = "photoTags"
    }

    @JvmField
    @Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var mockFlickrService: FlickrService

    @Mock
    lateinit var mockConnectionChecker: ConnectionChecker

    @Mock
    lateinit var mockRetrofitApiResponse: Response<APIResponse>

    @Mock
    lateinit var mockApiResponse: APIResponse

    @Mock
    lateinit var mockPhoto: Photo

    @Mock
    lateinit var mockThrowable: Throwable

    lateinit var flickrRepositoryImpl: FlickrRepositoryImpl

    @Before
    fun setUp() {
        flickrRepositoryImpl = FlickrRepositoryImpl(mockFlickrService, mockConnectionChecker)
    }

    @Test
    fun `Should getPhotos() throw network exception when there is no connection`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(false)

        // when
        val testObserver = flickrRepositoryImpl.requestPhotos(PHOTO_TAGS).test()

        // then
        testObserver.assertError(NetworkException::class.java)
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel PHOTOS_DOWNLOADED state`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPhotos(FlickrService.FLICKR_API_KEY, FlickrService.JSON_FORMAT, FlickrService.RAW_JSON, PHOTO_TAGS))
                .thenReturn(Single.just(mockRetrofitApiResponse))
        whenever(mockRetrofitApiResponse.isSuccessful).thenReturn(true)
        whenever(mockRetrofitApiResponse.body()).thenReturn(mockApiResponse)
        whenever(mockApiResponse.photos).thenReturn(listOf(mockPhoto))

        // when
        val testObserver = flickrRepositoryImpl.requestPhotos(PHOTO_TAGS).test()

        // then
        testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue(PhotosValidationModel(listOf(mockPhoto), PhotosValidationModel.PHOTOS_DOWNLOADED))
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel NO_POSTS state`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPhotos(FlickrService.FLICKR_API_KEY, FlickrService.JSON_FORMAT, FlickrService.RAW_JSON, PHOTO_TAGS))
                .thenReturn(Single.just(mockRetrofitApiResponse))
        whenever(mockRetrofitApiResponse.isSuccessful).thenReturn(true)
        whenever(mockRetrofitApiResponse.body()).thenReturn(mockApiResponse)
        whenever(mockApiResponse.photos).thenReturn(emptyList())

        // when
        val testObserver = flickrRepositoryImpl.requestPhotos(PHOTO_TAGS).test()

        // then
        testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue(PhotosValidationModel(emptyList(), PhotosValidationModel.NO_PHOTOS))
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel GENERAL ERROR state when response is not successful`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPhotos(FlickrService.FLICKR_API_KEY, FlickrService.JSON_FORMAT, FlickrService.RAW_JSON, PHOTO_TAGS))
                .thenReturn(Single.just(mockRetrofitApiResponse))
        whenever(mockRetrofitApiResponse.isSuccessful).thenReturn(false)
        whenever(mockRetrofitApiResponse.body()).thenReturn(mockApiResponse)
        whenever(mockApiResponse.photos).thenReturn(emptyList())

        // when
        val testObserver = flickrRepositoryImpl.requestPhotos(PHOTO_TAGS).test()

        // then
        testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue(PhotosValidationModel(emptyList(), PhotosValidationModel.GENERAL_ERROR))
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel GENERAL ERROR state when error happens`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPhotos(FlickrService.FLICKR_API_KEY, FlickrService.JSON_FORMAT, FlickrService.RAW_JSON, PHOTO_TAGS))
                .thenReturn(Single.error(mockThrowable))
        whenever(mockRetrofitApiResponse.isSuccessful).thenReturn(false)
        whenever(mockRetrofitApiResponse.body()).thenReturn(mockApiResponse)
        whenever(mockApiResponse.photos).thenReturn(emptyList())

        // when
        val testObserver = flickrRepositoryImpl.requestPhotos(PHOTO_TAGS).test()

        // then
        testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue(PhotosValidationModel(emptyList(), PhotosValidationModel.GENERAL_ERROR))
    }
}

