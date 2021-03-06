package com.example.justeatituser.ui.ViewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.justeatituser.R
import de.hdodenhof.circleimageview.CircleImageView

class ChatPictureViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    var txt_time: TextView?=null
    var txt_chat_message: TextView?=null
    var txt_email: TextView?=null
    var profile_image: CircleImageView?=null
    var img_preview:ImageView?=null

    init {
        txt_chat_message = itemView.findViewById(R.id.txt_chat_message) as TextView
        txt_time = itemView.findViewById(R.id.txt_time) as TextView
        txt_email = itemView.findViewById(R.id.txt_email) as TextView

        profile_image = itemView.findViewById(R.id.profile_image) as CircleImageView
        img_preview = itemView.findViewById(R.id.img_preview) as ImageView

    }

}