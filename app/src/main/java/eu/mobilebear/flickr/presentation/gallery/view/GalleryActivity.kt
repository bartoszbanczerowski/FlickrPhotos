package eu.mobilebear.flickr.presentation.gallery.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import eu.mobilebear.flickr.presentation.gallery.navigator.GalleryNavigator
import eu.mobilebear.flickr.presentation.gallery.view.adapter.PhotoModelAdapter
import eu.mobilebear.flickr.presentation.gallery.viewmodel.GalleryViewModel
import eu.mobilebear.flickr.presentation.gallery.viewmodel.GalleryViewModel.ScreenState
import eu.mobilebear.flickr.R
import eu.mobilebear.flickr.util.ViewModelFactory
import eu.mobilebear.flickr.util.state.NetworkStatus
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.view_status.*
import javax.inject.Inject

class GalleryActivity : DaggerAppCompatActivity(), PhotoModelOnClickListener {

    @Inject
    lateinit var socialNavigator: GalleryNavigator

    @Inject
    lateinit var postsAdapter: PhotoModelAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var viewModel: GalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        initView()

        viewModel = viewModelFactory.create(GalleryViewModel::class.java)
        viewModel.posts.observe(this, ScreenStateObserver())
    }

    private fun initView() {
        postsAdapter.setListener(this)
        val layoutManager = LinearLayoutManager(this)
        postsRecyclerView.adapter = postsAdapter
        postsRecyclerView.layoutManager = layoutManager
        retryButton.setOnClickListener { viewModel.getPhotos(tagEditTextView.text.toString()) }
        searchButton.setOnClickListener { viewModel.getPhotos(tagEditTextView.text.toString()) }
        sortSwitch.setOnCheckedChangeListener { switch, isChecked -> viewModel.sortByDate(isChecked) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPhotoClicked(mediaUrl: String) {
        socialNavigator.goToPhotoUrl(mediaUrl)
    }

    private fun updateViewForSuccessNetworkStatus(screenState: ScreenState) {
        hideLoadingView()
        sortSwitch.visibility = View.VISIBLE
        postsAdapter.submitList(screenState.posts)
    }

    private fun updateViewForRunningNetworkStatus() {
        showLoadingView()
        hideErrors()
    }

    private fun updateViewForErrorNetworkStatus() {
        hideLoadingView()
        showGeneralError()
    }

    private fun updateViewForNoConnectionErrorNetworkStatus() {
        hideLoadingView()
        showNetworkError()
    }

    private fun showLoadingView() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        progressBar.visibility = View.GONE
    }

    private fun hideErrors() {
        networkError.visibility = View.GONE
        generalError.visibility = View.GONE
        errorDescription.visibility = View.GONE
        retryButton.visibility = View.GONE
    }

    private fun showNetworkError() {
        generalError.visibility = View.GONE
        networkError.visibility = View.VISIBLE
        errorDescription.text = getString(R.string.network_error)
        errorDescription.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
        sortSwitch.visibility = View.INVISIBLE
    }

    private fun showGeneralError() {
        generalError.visibility = View.VISIBLE
        networkError.visibility = View.GONE
        errorDescription.text = getString(R.string.something_went_wrong)
        errorDescription.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
        sortSwitch.visibility = View.INVISIBLE
    }

    private inner class ScreenStateObserver : Observer<ScreenState> {

        override fun onChanged(screenState: ScreenState?) {
            screenState ?: return

            when (screenState.networkStatus) {
                NetworkStatus.Running -> {
                    updateViewForRunningNetworkStatus()
                }
                NetworkStatus.Success -> {
                    updateViewForSuccessNetworkStatus(screenState)
                }
                NetworkStatus.NoConnectionError -> {
                    updateViewForNoConnectionErrorNetworkStatus()
                }
                is NetworkStatus.Error -> {
                    updateViewForErrorNetworkStatus()
                }
            }
        }
    }
}
