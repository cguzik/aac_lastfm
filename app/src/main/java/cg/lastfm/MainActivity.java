package cg.lastfm;

import android.app.SearchManager;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cg.lastfm.ui.ArtistAdapter;
import cg.lastfm.ui.ArtistsViewModel;
import cg.lastfm.ui.ListItemClickListener;
import cg.lastfm.util.Keyboard;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    private AppBarLayout mAppBarLayout;
    private ArtistsViewModel viewModel;
    private RecyclerView recyclerView;
    private ArtistAdapter artistAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSwipeRefreshLayout();

        initRecyclerView();

        initViewModel();

        initAdapter();

        initAppBar();
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent);

        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    viewModel.refreshLoadedData();
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
    }

    private void updateSearchQuery(String query) {
        MutableLiveData<String> queryLiveData = viewModel.getQueryLiveData();
        if (!query.equals(queryLiveData.getValue())) {
            queryLiveData.setValue(query);
        }
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArtistsViewModel.class);
    }

    private void initAdapter() {
        artistAdapter = new ArtistAdapter(this);
        recyclerView.swapAdapter(artistAdapter, true);
        viewModel.getArtistsList().observe(this, artistAdapter::submitList);
        viewModel.getNetworkState().observe(this, artistAdapter::setNetworkState);
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.artistsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initAppBar() {
        mAppBarLayout = findViewById(R.id.app_bar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> mAppBarLayout.setExpanded(false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Keyboard keyboard = new Keyboard(MainActivity.this);
                keyboard.hide();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateSearchQuery(newText);
                return false;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mAppBarLayout.setExpanded(false, true);
                return true; // Return true to expand action view
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mAppBarLayout.setExpanded(true, true);
                return true; // Return true to collapse action view
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings menu item selected", Toast.LENGTH_LONG);
                return true;
            case R.id.menu_refresh:
                viewModel.refreshLoadedData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        switch (artistAdapter.getItemViewType(position)) {
            case R.layout.network_state_item:
                viewModel.refreshLoadedData();
                break;
            case R.layout.artist_list_item:
                //TODO load artist details activity
                break;
        }
    }

}
