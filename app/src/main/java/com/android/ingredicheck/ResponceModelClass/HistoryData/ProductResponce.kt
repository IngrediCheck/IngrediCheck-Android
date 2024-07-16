package com.android.ingredicheck.ResponceModelClass.HistoryData

import com.android.ingredicheck.ResponceModelClass.AnalyzeResponce.AnalyzeResponceItem

data class ProductResponce(
    val barcode: String = "",
    val brand: String? = "",
    var client_activity_id: String = "",
    val list_item_id: String = "",
    val created_at: String = "",
    var favorited: Boolean = false,
    var images: ArrayList<Image> = ArrayList<Image>(),
    val ingredient_recommendations: ArrayList<AnalyzeResponceItem> = ArrayList<AnalyzeResponceItem>(),
    val ingredients: ArrayList<Ingredient>? = ArrayList<Ingredient>(),
    val name: String? = "",
    val rating: Int = 0
)