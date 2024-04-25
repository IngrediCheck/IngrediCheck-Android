package com.sanket.ingredicheck.response

data class ErrorResponse(
    val result: String,
    val explanation: String?
)