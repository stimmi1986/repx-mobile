package com.hi.repx_mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hi.repx_mobile.data.database.AppDatabase
import com.hi.repx_mobile.data.database.entities.*
import com.hi.repx_mobile.data.repository.RepXRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RepXViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = RepXRepository(
        userDao = database.userDao()
    )

    // User state
    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _currentUserId.map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Auth state
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    // ============ Auth Methods ============
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            repository.registerUser(email, password, displayName)
                .onSuccess { userId ->
                    _currentUserId.value = userId
                    loadCurrentUser()
                }
                .onFailure { error ->
                    _authError.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            repository.loginUser(email, password)
                .onSuccess { user ->
                    _currentUserId.value = user.id
                    _currentUser.value = user
                }
                .onFailure { error ->
                    _authError.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun logout() {
        _currentUserId.value = null
        _currentUser.value = null
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.deleteUser(userId)
                logout()
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.getUserFlow(userId).collect { user ->
                    _currentUser.value = user
                }
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }
}
