package cg.lastfm;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import cg.lastfm.data.ArtistDetails;
import cg.lastfm.datasource.NetworkState;
import cg.lastfm.datasource.Status;
import cg.lastfm.ui.ArtistDetailsViewModel;
import dagger.android.AndroidInjection;

import static cg.lastfm.data.ImageURL.Size.EXTRALARGE;

public class ArtistDetailsActivity extends AppCompatActivity {
    public static final String ARTIST_NAME_INTENT_KEY = "ARTIST_NAME";

    private TextView summaryTextView;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_details_activity);
        initViews();
        initToolbar();
        initViewModel();
    }

    private void initViews() {
        summaryTextView = findViewById(R.id.artist_summary);
        imageView = findViewById(R.id.artist_image);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getArtistNameFromIntent());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViewModel() {
        ArtistDetailsViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(ArtistDetailsViewModel.class);
        viewModel.getNameLiveData().setValue(getArtistNameFromIntent());
        viewModel.getArtistLiveData().observe(this, this::updateViews);
        viewModel.getNetworkStateLiveData().observe(this, this::updateViews);
    }

    private void updateViews(NetworkState networkState) {
        if (networkState == null || networkState.getStatus() == Status.RUNNING) {
            progressBar.setVisibility(View.VISIBLE);
            summaryTextView.setText("");
        } else {
            progressBar.setVisibility(View.GONE);
            if (networkState.getStatus() == Status.FAILED) {
                summaryTextView.setText(networkState.getMsg());
            }
        }
    }

    private void updateViews(ArtistDetails artist) {
        updateSummaryTextView(artist);
        updateImageView(artist);
    }

    private void updateImageView(ArtistDetails artist) {
        Glide.with(this)
                .load(artist.getImageURL(EXTRALARGE).url)
                .into(imageView);
    }

    private void updateSummaryTextView(ArtistDetails artist) {
        String summary = artist.biography.content;
        summary = summary.replaceAll("\n", "<br>");
        summaryTextView.setText(Html.fromHtml(summary));
        summaryTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getArtistNameFromIntent() {
        return getIntent().getStringExtra(ARTIST_NAME_INTENT_KEY);
    }
}
