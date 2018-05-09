package cg.lastfm;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cg.lastfm.ui.ArtistAdapter;
import cg.lastfm.ui.ArtistsViewModel;
import cg.lastfm.ui.ListItemClickListener;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initArtistsList();

        initAppBar();
    }

    private void initArtistsList() {
        RecyclerView recyclerView = findViewById(R.id.artistsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArtistsViewModel viewModel = ViewModelProviders.of(this).get(ArtistsViewModel.class);

        final ArtistAdapter artistAdapter = new ArtistAdapter(this);

        viewModel.getArtistsList().observe(this, artistAdapter::setList);

        viewModel.getNetworkState().observe(this, artistAdapter::setNetworkState);

        recyclerView.setAdapter(artistAdapter);
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
            Toast toast = new Toast(searchView.getContext());

            @Override
            public boolean onQueryTextSubmit(String query) {
                return onQueryTextChange(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                toast.cancel();
                toast = Toast.makeText(searchView.getContext(), "Search query changed: " + newText, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, getResources().getDimensionPixelOffset(R.dimen.app_bar_height));
                toast.show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(this, "Item at position " + position + " clicked.", Toast.LENGTH_LONG);
    }
}
