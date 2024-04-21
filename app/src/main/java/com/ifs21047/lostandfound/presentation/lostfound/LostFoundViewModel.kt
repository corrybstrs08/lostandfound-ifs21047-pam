package com.ifs21047.lostfounds.presentation.lostfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21047.delcomtodo.data.remote.response.DataAddLostFoundResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomLostFoundResponse
import com.ifs21047.delcomtodo.data.remote.response.DelcomResponse
import com.ifs21047.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21047.lostandfound.data.repository.LocalLostFoundRepository
import com.ifs21047.lostfounds.data.remote.MyResult
import com.ifs21047.lostfounds.data.repository.LostFoundRepository
import com.ifs21047.lostfounds.presentation.ViewModelFactory

class LostFoundViewModel (
    private val lostFoundRepository : LostFoundRepository,
    private val localLostFoundRepository: LocalLostFoundRepository
) : ViewModel() {

    fun getLostFound(lostfoundId: Int) : LiveData<MyResult<DelcomLostFoundResponse>> {
        return lostFoundRepository.getDetail(lostfoundId).asLiveData()
    }

    fun postLostFound(
        title: String,
        description : String,
        status: String,
    ) : LiveData<MyResult<DataAddLostFoundResponse>> {
        return lostFoundRepository.postLostFound(
            title,
            description,
            status
        ).asLiveData()
    }

    fun putLostFound(
        lostfoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ) : LiveData<MyResult<DelcomResponse>> {
        return lostFoundRepository.putLostFound(
            lostfoundId,
            title,
            description,
            status,
            isCompleted
        ).asLiveData()
    }

    fun delete(lostfoundId: Int) : LiveData<MyResult<DelcomResponse>> {
        return lostFoundRepository.delete(lostfoundId).asLiveData()
    }
    fun getLocalLostFounds(): LiveData<List<LostFoundEntity>?> {
        return localLostFoundRepository.getAllLostFounds()
    }

    fun getLocalLostFound(lostfoundId: Int): LiveData<LostFoundEntity?> {
        return localLostFoundRepository.get(lostfoundId)
    }
    fun insertLocalLostFound(lostfound: LostFoundEntity) {
        localLostFoundRepository.insert(lostfound)
    }
    fun deleteLocalLostFound(lostfound: LostFoundEntity) {
        localLostFoundRepository.delete(lostfound)
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundViewModel? = null
        fun getInstance (
            lostFoundRepository: LostFoundRepository,
            localLostFoundRepository: LocalLostFoundRepository,
            ) : LostFoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostFoundViewModel(
                    lostFoundRepository,
                    localLostFoundRepository
                )
            }
            return INSTANCE as LostFoundViewModel
        }
    }
}