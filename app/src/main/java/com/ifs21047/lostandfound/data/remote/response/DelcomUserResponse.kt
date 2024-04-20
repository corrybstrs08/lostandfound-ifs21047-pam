package com.ifs21047.delcomtodo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DelcomUserResponse(

	@field:SerializedName("data")
	val data: DataUserResponse,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class DataUserResponse(

	@field:SerializedName("user")
	val user: UserResponse
)

data class UserResponse(

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("email")
	val email: String
)
