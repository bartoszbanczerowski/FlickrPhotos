package eu.mobilebear.flickr.presentation.injection.components

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import eu.mobilebear.flickr.FlickrApplication
import eu.mobilebear.flickr.presentation.injection.modules.ActivityModuleBuilder
import eu.mobilebear.flickr.presentation.injection.modules.ApplicationModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityModuleBuilder::class,
        ApplicationModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<FlickrApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun create(application: FlickrApplication): Builder

        fun build(): ApplicationComponent
    }
}
