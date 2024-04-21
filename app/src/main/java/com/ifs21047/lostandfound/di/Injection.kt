package com.ifs21047.lostfounds.di

import android.content.Context
import com.ifs21047.lostandfound.data.repository.LocalLostFoundRepository
import com.ifs21047.lostfounds.data.pref.UserPreference
import com.ifs21047.lostfounds.data.pref.dataStore
import com.ifs21047.lostfounds.data.remote.retrofit.ApiConfig
import com.ifs21047.lostfounds.data.remote.retrofit.IApiService
import com.ifs21047.lostfounds.data.repository.AuthRepository
import com.ifs21047.lostfounds.data.repository.LostFoundRepository
import com.ifs21047.lostfounds.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService: IApiService = ApiConfig.getApiService(user.token)
        return AuthRepository.getInstance(pref, apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService: IApiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService)
    }

    fun provideLostFoundRepository(context: Context): LostFoundRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService: IApiService = ApiConfig.getApiService(user.token)
        return LostFoundRepository.getInstance(apiService)
    }

    fun provideLocalLostFoundRepository(context: Context): LocalLostFoundRepository {
        return LocalLostFoundRepository.getInstance(context)
    }


}
