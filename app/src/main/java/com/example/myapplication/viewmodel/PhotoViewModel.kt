package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.constants.ApiConstants
import com.example.myapplication.model.Photo
import com.example.myapplication.model.Result
import com.example.myapplication.repository.PhotoRepository
import kotlinx.coroutines.launch

class PhotoViewModel(private val repository: PhotoRepository) : ViewModel() {
    private val _photos = MutableLiveData<MutableList<Photo>>(mutableListOf())
    val photos: LiveData<MutableList<Photo>> = _photos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentPage = ApiConstants.FIRST_PAGE
    private var hasMoreData = true
    private var currentQuery: String? = null
    private var isNewSearch = false

    fun fetchPhotos(isRefresh: Boolean = false) {
        if (isRefresh) {
            isNewSearch = false
            currentQuery = null
        }
        internalFetchPhotos(isRefresh, currentQuery)
    }

    fun searchPhotos(query: String, isRefresh: Boolean = false) {
        if (currentQuery != query || isRefresh) {
            currentQuery = query
            isNewSearch = true
        }
        internalFetchPhotos(isRefresh, query)
    }

    private fun internalFetchPhotos(isRefresh: Boolean, query: String?) {
        if (_isLoading.value == true) return
        if (isRefresh) {
            currentPage = ApiConstants.FIRST_PAGE
            hasMoreData = true
            isNewSearch = true
        }
        if (!hasMoreData && !isRefresh) return

        if (isRefresh) {
            _isLoading.value = true
        } else {
            _isLoadingMore.value = true
        }

        viewModelScope.launch {
            val result = if (query.isNullOrEmpty()) {
                repository.getCuratedPhotos(currentPage)
            } else {
                repository.searchPhotos(query, currentPage)
            }

            when (result) {
                is Result.Loading -> {
                    
                }
                is Result.Success -> {
                    val newPhotos = result.data.photos
                    if (newPhotos.isEmpty()) {
                        hasMoreData = false
                    } else {
                        val currentList = if (isNewSearch) {
                            mutableListOf()
                        } else {
                            _photos.value ?: mutableListOf()
                        }
                        currentList.addAll(newPhotos)
                        _photos.value = currentList
                        currentPage++
                        isNewSearch = false
                        hasMoreData = newPhotos.size >= ApiConstants.DEFAULT_PER_PAGE
                    }
                }
                is Result.Error -> {
                    _error.value = result.message
                }
            }
            _isLoading.value = false
            _isLoadingMore.value = false
        }
    }

    fun canLoadMore(): Boolean = hasMoreData && _isLoading.value != true && _isLoadingMore.value != true
}
