package com.example.justeatituser.Callback

import com.example.justeatituser.Model.PopularCategoryModel

interface IPopularLoadCallback {
    fun onPopulerLoadSuccess(popularModelList:List<PopularCategoryModel>)
    fun onPopularLoadFailed(message:String)
}
