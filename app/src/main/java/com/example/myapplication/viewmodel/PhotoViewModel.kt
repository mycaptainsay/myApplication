package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.constants.ApiConstants
import com.example.myapplication.model.Photo
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
    private var replaceList = false

    fun fetchPhotos(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentQuery = null
        }
        loadPhotos(isRefresh, currentQuery)
    }

    fun searchPhotos(query: String, isRefresh: Boolean = false) {
        if (currentQuery != query || isRefresh) {
            currentQuery = query
            replaceList = true
        }
        loadPhotos(isRefresh, query)
    }

    private fun loadPhotos(isRefresh: Boolean, query: String?) {
        if (_isLoading.value == true) return

        if (isRefresh) {
            currentPage = ApiConstants.FIRST_PAGE
            hasMoreData = true
            replaceList = true
        }
        if (!hasMoreData && !isRefresh) return

        if (isRefresh) {
            _isLoading.value = true
        } else {
            _isLoadingMore.value = true
        }

        viewModelScope.launch {
            try {
                val response = if (query.isNullOrEmpty()) {
                    repository.getCuratedPhotos(currentPage, forceNetwork = isRefresh)
                } else {
                    repository.searchPhotos(query, currentPage, forceNetwork = isRefresh)
                }

                val newPhotos = response.photos
                if (newPhotos.isEmpty()) {
                    hasMoreData = false
                } else {
                    val list = if (replaceList) {
                        mutableListOf()
                    } else {
                        _photos.value ?: mutableListOf()
                    }
                    list.addAll(newPhotos)
                    _photos.value = list
                    currentPage++
                    replaceList = false
                    hasMoreData = newPhotos.size >= ApiConstants.DEFAULT_PER_PAGE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.message ?: "加载失败"
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun canLoadMore(): Boolean {
        return hasMoreData && _isLoading.value != true && _isLoadingMore.value != true
    }
}
