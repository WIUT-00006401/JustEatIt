package com.example.justeatituser.Callback

import com.example.justeatituser.Model.OrderModel

interface ILoadOrderCallbackListener {
    fun onLoadOrderSuccess(orderList:List<OrderModel>)
    fun onLoadOrderFailed(message:String)
}