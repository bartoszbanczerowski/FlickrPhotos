package eu.mobilebear.flickr.presentation.injection.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import eu.mobilebear.flickr.presentation.gallery.view.GalleryActivity

@Suppress("unused")
@Module
abstract class ActivityModuleBuilder {

    @ContributesAndroidInjector(modules = [FlickrModule::class])
    abstract fun bindGalleryActivity(): GalleryActivity
}
