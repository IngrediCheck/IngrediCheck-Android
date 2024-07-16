package com.android.ingredicheck.ResponceModelClass.HistoryData

data class Ingredient(
    val ingredients: ArrayList<Ingredient> = ArrayList<Ingredient>(),
    val name: String = "",
    val vegan: String? = "",
    val vegetarian: String? = ""
)