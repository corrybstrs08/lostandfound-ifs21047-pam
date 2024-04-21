package com.ifs21047.lostandfound.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ifs21047.lostandfound.data.local.entity.LostFoundEntity
import com.ifs21047.lostandfound.data.local.room.LostFoundDatabase
import com.ifs21047.lostandfound.data.local.room.IDelcomLostFoundDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
class LocalLostFoundRepository(context: Context) {
    private val mDelcomLostFoundDao: IDelcomLostFoundDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = LostFoundDatabase.getInstance(context)
        mDelcomLostFoundDao = db.delcomLostFoundDao()
    }
    fun getAllLostFounds(): LiveData<List<LostFoundEntity>?> = mDelcomLostFoundDao.getAllLostFounds()
    fun get(lostfoundId: Int): LiveData<LostFoundEntity?> = mDelcomLostFoundDao.get(lostfoundId)
    fun insert(lostfound: LostFoundEntity) {
        executorService.execute { mDelcomLostFoundDao.insert(lostfound) }
    }
    fun delete(lostfound: LostFoundEntity) {
        executorService.execute { mDelcomLostFoundDao.delete(lostfound) }
    }
    companion object {
        @Volatile
        private var INSTANCE: LocalLostFoundRepository? = null
        fun getInstance(
            context: Context
        ): LocalLostFoundRepository {
            synchronized(LocalLostFoundRepository::class.java) {
                INSTANCE = LocalLostFoundRepository(
                    context
                )
            }
            return INSTANCE as LocalLostFoundRepository
        }
    }
}