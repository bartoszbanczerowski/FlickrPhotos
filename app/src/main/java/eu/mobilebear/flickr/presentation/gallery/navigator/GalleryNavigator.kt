package eu.mobilebear.flickr.presentation.gallery.navigator

import android.content.Context
import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class GalleryNavigator @Inject constructor(private val context: Context) {

    fun goToPhotoUrl(mediaUrl: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mediaUrl))
        context.startActivity(webIntent)
    }

}
