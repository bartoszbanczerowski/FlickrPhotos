package eu.mobilebear.flickr.networking

import eu.mobilebear.flickr.networking.response.responsedata.APIResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {

    companion object {
        const val FLICKR_API_KEY = "13ac121b2bb98045435c042ddcd7b859"
        const val JSON_FORMAT = "json"
        const val RAW_JSON = "1"
        private const val QUERY_API_KEY = "api_key"
        private const val QUERY_FORMAT = "format"
        private const val QUERY_TAGS = "tags"
        private const val QUERY_NO_JSON_CALLBACK = "nojsoncallback"
    }

    @GET("photos_public.gne")
    fun getPhotos(@Query(QUERY_API_KEY) apiKey: String,
                  @Query(QUERY_FORMAT) format: String,
                  @Query(QUERY_NO_JSON_CALLBACK) noJsonCallBack: String,
                  @Query(QUERY_TAGS) tags: String): Single<Response<APIResponse>>
}
