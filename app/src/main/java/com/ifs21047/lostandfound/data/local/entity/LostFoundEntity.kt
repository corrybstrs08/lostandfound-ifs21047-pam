package com.ifs21047.lostandfound.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "delcom_lostfounds")

data class LostFoundEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "is_completed")
    var isCompleted: Int,
    @ColumnInfo(name = "cover")
    var cover: String?,
    @ColumnInfo(name = "created_at")
    var createdAt: String,
    @ColumnInfo(name = "updated_at")
    var updatedAt: String,
    @ColumnInfo(name = "status")
    var status: String,
    @ColumnInfo(name = "userId")
    var userId: Int
) : Parcelable
