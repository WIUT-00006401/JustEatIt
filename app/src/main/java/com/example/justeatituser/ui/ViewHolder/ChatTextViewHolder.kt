package com.example.justeatituser.ui.ViewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.justeatituser.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_message_text.view.*

class ChatTextViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {

    var txt_time:TextView?=null
    var txt_chat_message:TextView?=null
    var txt_email:TextView?=null
    var profile_image:CircleImageView?=null

    init {
        txt_chat_message = itemView.findViewById(R.id.txt_chat_message) as TextView
        txt_time = itemView.findViewById(R.id.txt_time) as TextView
        txt_email = itemView.findViewById(R.id.txt_email) as TextView

        profile_image = itemView.findViewById(R.id.profile_image) as CircleImageView

    }

}