package eu.mobilebear.flickr.data.repository

import com.nhaarman.mockitokotlin2.whenever
import eu.mobilebear.flickr.networking.FlickrService
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
        private const val POST_ID = 1234
    }

    @JvmField
    @Rule
    var rule = MockitoJUnit.rule()

    @Mock
    lateinit var mockFlickrService: FlickrService

    @Mock
    lateinit var mockConnectionChecker: ConnectionChecker

    @Mock
    lateinit var mockPhotosResponse: Response<List<Photo>>

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
    fun `Should getPosts() throw network exception when there is no connection`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(false)

        // when
        val testObserver = flickrRepositoryImpl.requestPosts().test()

        // then
        testObserver.assertError(NetworkException::class.java)
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel PHOTOS_DOWNLOADED state`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPosts()).thenReturn(Single.just(mockPhotosResponse))
        whenever(mockPhotosResponse.isSuccessful).thenReturn(true)
        whenever(mockPhotosResponse.body()).thenReturn(listOf(mockPost))

        // when
        val testObserver = flickrRepositoryImpl.requestPosts().test()

        // then
        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValue(PostsValidationModel(listOf(mockPost), PostsValidationModel.POSTS_DOWNLOADED))
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel NO_POSTS state`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPosts()).thenReturn(Single.just(mockPhotosResponse))
        whenever(mockPhotosResponse.isSuccessful).thenReturn(true)
        whenever(mockPhotosResponse.body()).thenReturn(emptyList())

        // when
        val testObserver = flickrRepositoryImpl.requestPosts().test()

        // then
        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValue(PostsValidationModel(emptyList(), PostsValidationModel.NO_POSTS))
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel GENERAL ERROR state when response is not successful`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPosts()).thenReturn(Single.just(mockPhotosResponse))
        whenever(mockPhotosResponse.isSuccessful).thenReturn(false)
        whenever(mockPhotosResponse.body()).thenReturn(emptyList())

        // when
        val testObserver = flickrRepositoryImpl.requestPosts().test()

        // then
        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValue(PostsValidationModel(emptyList(), PostsValidationModel.GENERAL_ERROR))
    }

    @Test
    fun `Should getPhotos() return PhotosValidationModel GENERAL ERROR state when error happens`() {
        // given
        whenever(mockConnectionChecker.isConnected).thenReturn(true)
        whenever(mockFlickrService.getPosts()).thenReturn(Single.error(mockThrowable))
        whenever(mockPhotosResponse.isSuccessful).thenReturn(false)
        whenever(mockPhotosResponse.body()).thenReturn(emptyList())

        // when
        val testObserver = flickrRepositoryImpl.requestPosts().test()

        // then
        testObserver
            .assertComplete()
            .assertNoErrors()
            .assertValue(PostsValidationModel(emptyList(), PostsValidationModel.GENERAL_ERROR))
    }
}

