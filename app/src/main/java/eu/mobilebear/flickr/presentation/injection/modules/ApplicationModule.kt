package eu.mobilebear.flickr.presentation.injection.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import eu.mobilebear.flickr.FlickrApplication
import eu.mobilebear.flickr.util.AndroidObjectsFactory
import eu.mobilebear.flickr.util.ConnectionChecker
import javax.inject.Singleton

@Module(includes = [RxModule::class, NetworkingModule::class, ViewModelModule::class])
class ApplicationModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(application: FlickrApplication): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    @Provides
    @Singleton
    fun providesConnectivityManager(application: FlickrApplication, androidObjectsFactory: AndroidObjectsFactory): ConnectionChecker = ConnectionChecker(application, androidObjectsFactory)

    @Provides
    @Singleton
    fun providesAndroidObjectFactory() = AndroidObjectsFactory()

    @Singleton
    @Provides
    fun provideContext(application: FlickrApplication): Context = application

}
