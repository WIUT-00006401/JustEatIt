package com.example.justeatituser.ui.view_orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.justeatituser.Model.OrderModel

class ViewOrderModel: ViewModel() {
    val mutableLiveDataOrderList: MutableLiveData<List<OrderModel>>

    init {
        mutableLiveDataOrderList = MutableLiveData()
    }

    fun setMutableLiveDataOrderList(orderList:List<OrderModel>)
    {
        mutableLiveDataOrderList.value = orderList
    }

}