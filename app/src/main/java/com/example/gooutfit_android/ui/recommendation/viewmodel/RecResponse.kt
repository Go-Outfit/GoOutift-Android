package com.example.gooutfit_android.ui.recommendation.viewmodel

import com.google.gson.annotations.SerializedName

data class RecResponse(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("errors")
	val errors: Any? = null
)

data class Data(
	@field:SerializedName("footwear")
	val footwear: List<List<String?>?>? = null,

	@field:SerializedName("upperwear")
	val upperwear: List<List<String?>?>? = null,

	@field:SerializedName("bottomwear")
	val bottomwear: List<List<String?>?>? = null
)

data class TopwearList(
	@field:SerializedName("upperwear")
	val upperwear: String,
)
