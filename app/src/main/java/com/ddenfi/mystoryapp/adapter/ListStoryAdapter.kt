package com.ddenfi.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ddenfi.mystoryapp.R
import com.ddenfi.mystoryapp.database.ListStoryItem
import com.ddenfi.mystoryapp.ui.DetailStoryActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem,ListStoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        private var tvName = itemView.findViewById<TextView>(R.id.tv_item_name)
        private var tvTime = itemView.findViewById<TextView>(R.id.tv_item_time)

        fun bind(user: ListStoryItem) {
            Glide.with(itemView.context)
                .load(user.photoUrl)
                .into(imgPhoto)
            tvName.text = user.name

            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("UTC")
            try {
                val date = df.parse(user.createdAt)
                val dfLocal = SimpleDateFormat("EEE, d MMM yyyy h:mm a",Locale.getDefault())
                dfLocal.timeZone = TimeZone.getDefault()

                tvTime.text = date?.let { dfLocal.format(it) }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_DATA_USER, user)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(imgPhoto, "photo"),
                        Pair(tvName, "name"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}