package com.example.miprimerhuerto.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Gender {
    JARDINERO,   // Male gardener
    JARDINERA    // Female gardener
}

@Serializable
data class User(
    val name: String,
    val gender: Gender,
    val createdAt: Long = System.currentTimeMillis()
)

