package cg.lastfm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import cg.lastfm.data.Artist
import cg.lastfm.data.ImageURL.Size.EXTRALARGE
import cg.lastfm.datasource.NetworkState
import cg.lastfm.datasource.Status
import cg.lastfm.ui.ArtistDetailsViewModel
import com.bumptech.glide.Glide
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.artist_details_activity.*
import javax.inject.Inject

class ArtistDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val artistNameFromIntent: String
        get() = intent.getStringExtra(ARTIST_NAME_INTENT_KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.artist_details_activity)
        initToolbar()
        initViewModel()
    }

    private fun initToolbar() {
        toolbar.title = artistNameFromIntent
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViewModel() {
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get<ArtistDetailsViewModel>(ArtistDetailsViewModel::class.java)
        viewModel.nameLiveData.setValue(artistNameFromIntent)
        viewModel.artistLiveData.observe(this, Observer<Artist> { updateViews(it) })
        viewModel.networkStateLiveData.observe(this, Observer<NetworkState> { updateViews(it) })
    }

    private fun updateViews(networkState: NetworkState?) {
        if (networkState == null || networkState.status === Status.RUNNING) {
            progressBar.visibility = View.VISIBLE
            summaryTextView.text = ""
        } else {
            progressBar.visibility = View.GONE
            if (networkState.status === Status.FAILED) {
                summaryTextView.text = networkState.msg
            }
        }
    }

    private fun updateViews(artist: Artist?) {
        if (artist != null) {
            updateSummaryTextView(artist)
            updateImageView(artist)
        }
    }

    private fun updateImageView(artist: Artist) {
        Glide.with(this)
                .load(artist.getImageURL(EXTRALARGE).url)
                .into(imageView)
    }

    private fun updateSummaryTextView(artist: Artist) {
        var summary = artist.biography?.content ?: ""
        summary = summary.replace("\n".toRegex(), "<br>")
        summaryTextView.text = Html.fromHtml(summary)
        summaryTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    companion object {
        val ARTIST_NAME_INTENT_KEY = "ARTIST_NAME"
    }
}
