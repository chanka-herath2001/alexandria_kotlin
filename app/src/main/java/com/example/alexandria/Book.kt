package com.example.alexandria

import java.io.Serializable

data class Book(
    val author: String = "",
    val availability: String = "",
    val description: String = "",
    val genre: String = "",
    val image: String = "",
    val library: String = "",
    val noOfCopies: Int = 0,
    val title: String = ""
) : Serializable
