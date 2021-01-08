package com.example.justeatituser.Common

import com.example.justeatituser.Model.CategoryModel
import com.example.justeatituser.Model.FoodModel
import com.example.justeatituser.Model.UserModel

object Common {
    const val ORDER_REF: String = "Order"
    const val COMMENT_REF: String = "Comments"
    var foodSelected: FoodModel?=null
    var categorySelected: CategoryModel?=null
    val CATEGORY_REF: String="Category"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int=0
    const val BEST_DEALS_REF: String="BestDeals"
    const val POPULAR_REF: String="MostPopular"
    var USER_REFERENCE="Users"
    var currentUser: UserModel?=null

}