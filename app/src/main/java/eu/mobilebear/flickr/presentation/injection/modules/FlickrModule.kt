package eu.mobilebear.flickr.presentation.injection.modules

import android.content.Context
import com.bumptech.glide.Glide
import dagger.Module
import dagger.Provides
import eu.mobilebear.babylon.rx.RxFactory
import eu.mobilebear.flickr.domain.interactor.GetPhotosUseCase
import eu.mobilebear.flickr.domain.mapper.PhotoMapper
import eu.mobilebear.flickr.domain.repository.FlickrRepository
import eu.mobilebear.flickr.presentation.gallery.navigator.GalleryNavigator
import eu.mobilebear.flickr.presentation.gallery.view.adapter.PhotoModelAdapter

@Module
class FlickrModule {

    @Provides
    fun provideSocialNavigator(context: Context): GalleryNavigator = GalleryNavigator(context)

    @Provides
    fun provideSocialPostMapper(): PhotoMapper = PhotoMapper()

    @Provides
    fun provideGetPostsUseCase(
        flickrRepository: FlickrRepository,
        rxFactory: RxFactory): GetPhotosUseCase = GetPhotosUseCase(flickrRepository, rxFactory)

    @Provides
    fun provideSocialPostsAdapter() = PhotoModelAdapter()

}
