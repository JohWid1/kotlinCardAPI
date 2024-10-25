package com.example.cardapi

data class Card(
    val name: String,
    val mana_cost: String,
    val type_line: String,
    val oracle_text: String,
    val image_uris: ImageUris
)

data class ImageUris(
    val normal: String
)

