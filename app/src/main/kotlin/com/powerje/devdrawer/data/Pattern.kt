package com.powerje.devdrawer.data

import kotlinx.serialization.Serializable

@Serializable
data class Pattern(
    val label: String,
    val regex: String,
)
