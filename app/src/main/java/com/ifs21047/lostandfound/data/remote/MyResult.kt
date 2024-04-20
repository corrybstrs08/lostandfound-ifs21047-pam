package com.ifs21047.lostfounds.data.remote

sealed class MyResult<out R> private constructor() {
    data class Success<out T>(val data: T) : MyResult<T>()
    data class Error(val error: String) : MyResult<Nothing>()
    object Loading : MyResult<Nothing>()
}