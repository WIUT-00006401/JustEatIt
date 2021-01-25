package com.example.justeatituser.Callback

import com.example.justeatituser.Database.CartItem
import com.example.justeatituser.Model.CategoryModel

interface ISearchCategoryCallbackListener {
    fun onSearchFound(category:CategoryModel,cartItem: CartItem)
    fun onSearchNotFound(message:String)
}