package com.ifs21047.lostfounds.data.repository

import com.google.gson.Gson
import com.ifs21047.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21047.lostfounds.data.pref.UserModel
import com.ifs21047.lostfounds.data.pref.UserPreference
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.data.remote.retrofit.IApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class AuthRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: IApiService,
) {

    suspend fun saveSession(user: UserModel): Flow<UserModel> {
        return userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun register(name: String, email: String, password: String) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.register(name, email, password)))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }

    fun login(email: String, password: String) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(MyResult.Success(apiService.login(email, password).data))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: IApiService,
        ): AuthRepository {
            synchronized(AuthRepository::class.java) {
                INSTANCE = AuthRepository(
                    userPreference,
                    apiService
                )
            }
            return INSTANCE as AuthRepository
        }
    }
}
