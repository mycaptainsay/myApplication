package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.PhotoAdapter
import com.example.myapplication.constants.ApiConstants
import com.example.myapplication.constants.AppConstants
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.model.Photo
import com.example.myapplication.ui.detail.DetailActivity
import com.example.myapplication.utils.EndlessScrollListener
import com.example.myapplication.utils.NetworkUtils
import com.example.myapplication.utils.SpacingItemDecoration
import com.example.myapplication.viewmodel.PhotoViewModel
import com.example.myapplication.viewmodel.PhotoViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: PhotoViewModel
    private lateinit var adapter: PhotoAdapter
    private lateinit var scrollListener: EndlessScrollListener
    private var currentQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initRecyclerView()
        initListeners()
        initObservers()

        checkApiKeyAndLoad()
    }

    private fun initViewModel() {
        val factory = PhotoViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory)[PhotoViewModel::class.java]
    }

    private fun initRecyclerView() {
        adapter = PhotoAdapter { photo, imageView ->
            openDetailActivity(photo)
        }

        val layoutManager = StaggeredGridLayoutManager(
            AppConstants.SPAN_COUNT,
            StaggeredGridLayoutManager.VERTICAL
        )
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(
            SpacingItemDecoration(
                resources.getDimensionPixelSize(R.dimen.item_spacing),
                AppConstants.SPAN_COUNT
            )
        )
        binding.recyclerView.adapter = adapter

        scrollListener = object : EndlessScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (viewModel.canLoadMore()) {
                    adapter.addLoadingFooter()
                    if (!currentQuery.isNullOrEmpty()) {
                        viewModel.searchPhotos(currentQuery!!, isRefresh = false)
                    } else {
                        viewModel.fetchPhotos(isRefresh = false)
                    }
                }
            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    private fun initListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            scrollListener.resetState()
            if (!currentQuery.isNullOrEmpty()) {
                viewModel.searchPhotos(currentQuery!!, isRefresh = true)
            } else {
                viewModel.fetchPhotos(isRefresh = true)
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    currentQuery = null
                    scrollListener.resetState()
                    viewModel.fetchPhotos(isRefresh = true)
                }
                return false
            }
        })
    }

    private fun initObservers() {
        viewModel.photos.observe(this) { photos ->
            adapter.removeLoadingFooter()
            adapter.submitList(photos)
            binding.tvEmpty.visibility = if (photos.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.isLoadingMore.observe(this) { isLoadingMore ->
            if (!isLoadingMore) {
                adapter.removeLoadingFooter()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                val formattedMessage = getString(R.string.loading_failed, it)
                Toast.makeText(this, formattedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performSearch(query: String) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
            return
        }
        currentQuery = query
        scrollListener.resetState()
        viewModel.searchPhotos(query, isRefresh = true)
        binding.searchView.clearFocus()
    }

    private fun checkApiKeyAndLoad() {
        if (ApiConstants.API_KEY.isBlank()) {
            Toast.makeText(
                this,
                "请在 local.properties 中配置 pexels.api.key",
                Toast.LENGTH_LONG
            ).show()
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show()
        }

        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchPhotos(isRefresh = true)
    }

    private fun openDetailActivity(photo: Photo) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(AppConstants.INTENT_EXTRA_PHOTO, photo)
        startActivity(intent)
    }
}
