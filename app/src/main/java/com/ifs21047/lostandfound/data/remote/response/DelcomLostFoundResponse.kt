package com.ifs21047.delcomtodo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DelcomLostFoundResponse(

	@field:SerializedName("data")
	val data: DataLostFoundResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LostFoundResponse(

	@field:SerializedName("cover")
	val cover: String?,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("author")
	val author: AuthorLostFoundResponse,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("is_completed")
	val isCompleted: Int,

	@field:SerializedName("status")
	val status: String
)

data class AuthorLostFoundResponse(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("photo")
	val photo: Any
)

data class DataLostFoundResponse(

	@field:SerializedName("lost_found")
	val todo: LostFoundResponse
)