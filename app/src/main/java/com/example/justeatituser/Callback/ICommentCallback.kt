package com.example.justeatituser.Callback

import com.example.justeatituser.Model.CommentModel

interface ICommentCallback {
    fun onCommentLoadSuccess(categoriesList:List<CommentModel>)
    fun onCommentLoadFailed(message:String)

}