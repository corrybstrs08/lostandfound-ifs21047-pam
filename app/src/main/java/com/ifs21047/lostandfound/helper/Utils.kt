package com.ifs21047.lostfounds.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.ifs21047.delcomtodo.data.remote.response.AuthorLostFoundsResponse
import com.ifs21047.delcomtodo.data.remote.response.LostFoundsItemResponse
import com.ifs21047.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21047.lostfounds.data.remote.MyResult

class Utils {
    companion object{
        fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
            val observerWrapper = object : Observer<T> {
                override fun onChanged(value: T) {
                    observer(value)
                    if (value is MyResult.Success<*> ||
                        value is MyResult.Error
                    ) {
                        removeObserver(this)
                    }
                }
            }
            observeForever(observerWrapper)
        }
        fun entitiesToResponses(entities: List<LostFoundEntity>): List<LostFoundsItemResponse> {
            return entities.map {
                LostFoundsItemResponse(
                    cover = it.cover ?: "",
                    updatedAt = it.updatedAt,
                    userId = it.userId, // Sesuaikan dengan kebutuhan Anda, karena tidak ada field yang cocok di LostFoundEntity
                    author = AuthorLostFoundsResponse(
                        name = "Unknown",
                        photo = ""
                    ),
                    description = it.description,
                    createdAt = it.createdAt,
                    id = it.id,
                    title = it.title,
                    isCompleted = it.isCompleted,
                    status = it.status
                )
            }
        }

    }
}

