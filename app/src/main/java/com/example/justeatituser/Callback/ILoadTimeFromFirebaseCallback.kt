package com.example.justeatituser.Callback

import com.example.justeatituser.Model.OrderModel

interface ILoadTimeFromFirebaseCallback {
    fun onLoadTimeSuccess(order: OrderModel, estimatedTimeMs:Long)
    fun onLoadOnlyTimeSuccess(estimatedTimeMs:Long)
    fun onLoadTimeFailed(message:String)
}