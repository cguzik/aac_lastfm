package cg.lastfm

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cg.lastfm.data.Artist
import cg.lastfm.datasource.NetworkState
import cg.lastfm.ui.ArtistAdapter
import cg.lastfm.ui.ArtistsViewModel
import cg.lastfm.ui.ListItemClickListener
import cg.lastfm.util.Keyboard
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.main_activity.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ListItemClickListener {

    lateinit private var viewModel: ArtistsViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initSwipeRefreshLayout()
        initRecyclerView()
        initViewModel()
        initAdapter()
        initAppBar()
    }

    private fun initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLoadedData()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateSearchQuery(query: String) {
        val queryLiveData = viewModel.queryLiveData
        if (query != queryLiveData.value) {
            queryLiveData.value = query
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)[ArtistsViewModel::class.java]
    }

    private fun initAdapter() {
        val artistAdapter = ArtistAdapter(this)
        artistsRecyclerView.swapAdapter(artistAdapter, true)
        viewModel.artistsList.observe(this, Observer<PagedList<Artist>> { artistAdapter.submitList(it) })
        viewModel.networkState.observe(this, Observer<NetworkState> { artistAdapter.setNetworkState(it) })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        artistsRecyclerView.layoutManager = linearLayoutManager
    }

    private fun initAppBar() {
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view -> appBarLayout.setExpanded(false) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        val searchQuery = viewModel.queryLiveData.value
        if ("" != searchQuery) {
            searchMenuItem.expandActionView()
            searchView.setQuery(searchQuery, false)
            searchView.clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val keyboard = Keyboard(this@MainActivity)
                keyboard.hide()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                updateSearchQuery(newText)
                return false
            }
        })

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                appBarLayout.setExpanded(false, true)
                return true // Return true to expand action view
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                appBarLayout.setExpanded(true, true)
                return true // Return true to collapse action view
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Settings menu item selected", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.menu_refresh -> {
                viewModel.refreshLoadedData()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View, position: Int) {
        val adapter = artistsRecyclerView.adapter as ArtistAdapter
        when (adapter.getItemViewType(position)) {
            R.layout.network_state_item -> viewModel.refreshLoadedData()
            R.layout.artist_list_item -> {
                val artistName = adapter.currentList!![position]!!.name

                val intent = Intent(this, ArtistDetailsActivity::class.java)
                intent.putExtra(ArtistDetailsActivity.ARTIST_NAME_INTENT_KEY, artistName)

                startActivity(intent)
            }
        }
    }

}
