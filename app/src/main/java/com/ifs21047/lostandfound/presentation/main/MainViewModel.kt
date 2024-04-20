package com.ifs21047.lostfounds.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21047.delcomtodo.data.remote.response.DelcomLostFoundsResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21047.lostfounds.data.pref.UserModel
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.data.repository.AuthRepository
import com.ifs21047.lostfounds.data.repository.LostFoundRepository
import com.ifs21047.lostfounds.presentation.ViewModelFactory
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val lostFoundRepository: LostFoundRepository
) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }


    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getTodos(): LiveData<MyResult<DelcomLostFoundsResponse>> {
        return lostFoundRepository.getAll(null, 0, null).asLiveData()
    }

    fun putTodo(
        lostfoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<DelcomResponse>> {
        return lostFoundRepository.putLostFound(
            lostfoundId,
            title,
            description,
            status,
            isCompleted,
        ).asLiveData()
    }

    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            lostFoundRepository: LostFoundRepository
        ): MainViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = MainViewModel(
                    authRepository,
                    lostFoundRepository
                )
            }
            return INSTANCE as MainViewModel
        }
    }
}