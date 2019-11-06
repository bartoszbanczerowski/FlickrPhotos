package eu.mobilebear.flickr.presentation.injection.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import eu.mobilebear.flickr.presentation.injection.keys.ViewModelKey
import eu.mobilebear.flickr.presentation.gallery.viewmodel.GalleryViewModel
import eu.mobilebear.flickr.util.ViewModelFactory

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    abstract fun bindGalleryViewModel(socialViewModel: GalleryViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
