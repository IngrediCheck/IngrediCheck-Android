package com.sanket.ingredicheck.response

import com.sanket.ingredicheck.model.Dietary

data class PreferenceResponse(
    val result: String,
    val text: String,
    val annotatedText: String,
    val id: Int,
    val explanation: String?
)