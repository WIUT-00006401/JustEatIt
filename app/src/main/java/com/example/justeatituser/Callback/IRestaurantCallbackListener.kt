package com.example.justeatituser.Callback

import com.example.justeatituser.Model.RestaurantModel

interface IRestaurantCallbackListener {
    fun onRestaurantLoadSuccess(restaurantList:List<RestaurantModel>)
    fun onRestaurantLoadFailed(message:String)
}