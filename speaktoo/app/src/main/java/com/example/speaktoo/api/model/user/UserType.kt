package com.example.speaktoo.api.model.user

data class ChangeUserTypeRequest (
    val userType: String
)

data class ChangeUserTypeResponse (
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ChangeUserTypeData?
)

data class ChangeUserTypeData (
    val userType: String
)