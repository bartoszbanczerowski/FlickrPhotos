package eu.mobilebear.flickr.presentation.injection.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import eu.mobilebear.flickr.rx.RxFactory
import eu.mobilebear.flickr.data.repository.FlickrRepositoryImpl
import eu.mobilebear.flickr.domain.repository.FlickrRepository
import javax.inject.Singleton

@Module
abstract class RxModule {

    @Module
    companion object {

        @Provides
        @Singleton
        fun providesRxFactory() = RxFactory()
    }


    @Binds
    @Singleton
    abstract fun providesFlickrRepository(flickrRepositoryImpl: FlickrRepositoryImpl): FlickrRepository
}
