package eu.mobilebear.flickr.presentation.social.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import eu.mobilebear.flickr.domain.interactor.GetPhotosUseCase
import eu.mobilebear.flickr.domain.mapper.PhotoMapper
import eu.mobilebear.flickr.domain.model.PhotoModel
import eu.mobilebear.flickr.domain.model.PhotosValidationModel
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
    fun `Should execute call to get posts when it's called`() {
        // when
        viewModel.getPosts()

        // then
        verify(mockGetPhotosUseCase).execute(observer = any(), params = isNull())
        assertEquals("Network Status is Running", NetworkStatus.Running, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no posts", emptyList<SocialPost>(), viewModel.mutableScreenState.value?.posts)
    }

    @Test
    fun `Should be in error state when get Posts return general error`() {
        //given
        viewModel.getPosts()
        val captorPostsObserver = argumentCaptor<SocialViewModel.PostsObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), isNull())
        val socialValidationModelObserver = captorPostsObserver.firstValue

        // when
        socialValidationModelObserver.onSuccess(SocialValidationAction.GeneralError)

        // then
        assertEquals("Network Status is Error", NetworkStatus.Error(null), viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no posts", emptyList<SocialPost>(), viewModel.mutableScreenState.value?.posts)
    }

    @Test
    fun `Should be in error state when get Posts return network exception`() {
        //given
        viewModel.getPosts()
        val captorPostsObserver = argumentCaptor<SocialViewModel.PostsObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), isNull())
        val socialValidationModelObserver = captorPostsObserver.firstValue

        // when
        socialValidationModelObserver.onError(mockNetworkException)

        // then
        assertEquals("Network Status is Network Error", NetworkStatus.NoConnectionError, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no posts", emptyList<SocialPost>(), viewModel.mutableScreenState.value?.posts)
    }

    @Test
    fun `Should be in success state when get Posts return no posts`() {
        //given
        viewModel.getPosts()
        val captorPostsObserver = argumentCaptor<SocialViewModel.PostsObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), isNull())
        val socialValidationModelObserver = captorPostsObserver.firstValue

        // when
        socialValidationModelObserver.onSuccess(SocialValidationAction.NoPosts)

        // then
        assertEquals("Network Status is Success", NetworkStatus.Success, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has no posts", emptyList<SocialPost>(), viewModel.mutableScreenState.value?.posts)
    }

    @Test
    fun `Should be in success state when get Posts return posts`() {
        //given
        whenever(mockPhotosValidationModel.posts).thenReturn(listOf(mockPhotoModel))
        viewModel.getPosts()
        val captorPostsObserver = argumentCaptor<SocialViewModel.PostsObserver>()
        verify(mockGetPhotosUseCase).execute(captorPostsObserver.capture(), isNull())
        val socialValidationModelObserver = captorPostsObserver.firstValue

        // when
        socialValidationModelObserver.onSuccess(SocialValidationAction.SocialPostsDownloaded(mockPhotosValidationModel))

        // then
        assertEquals("Network Status is Success", NetworkStatus.Success, viewModel.mutableScreenState.value?.networkStatus)
        assertEquals("ViewModel has exactly same amount of posts", listOf(mockPhotoModel), viewModel.mutableScreenState.value?.posts)
    }
}
