package com.tigcal.samples.annuaire

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigcal.samples.annuaire.model.User

class UserAdapter : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private val users = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setUsers(usersToAdd: List<User>) {
        users.clear()
        users.addAll(usersToAdd)
        notifyDataSetChanged()
    }

    fun clear() {
        users.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photo: ImageView by lazy {
            itemView.findViewById(R.id.user_pic)
        }
        private val nameText: TextView by lazy {
            itemView.findViewById(R.id.user_name)
        }
        private val phoneText: TextView by lazy {
            itemView.findViewById(R.id.user_phone)
        }

        fun bind(user: User) {
            nameText.text = user.fullName
            phoneText.text = user.phone

            Glide.with(itemView.context)
                .load(user.image)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .centerCrop()
                .into(photo)
        }
    }
}