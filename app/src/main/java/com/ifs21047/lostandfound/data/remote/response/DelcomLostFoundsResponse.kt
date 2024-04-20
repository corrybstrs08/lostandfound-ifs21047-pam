package com.ifs21047.delcomtodo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DelcomLostFoundsResponse(
	@field:SerializedName("data")
	val data: DataLostFoundsResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LostFoundsItemResponse(
	@field:SerializedName("cover")
	val cover: String?,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("is_completed")
  	var isCompleted: Int,

	@field:SerializedName("is_me")
	var isMe: Int
)

data class DataLostFoundsResponse(
	@field:SerializedName("lost_founds")
	val todos: List<LostFoundsItemResponse>
)











