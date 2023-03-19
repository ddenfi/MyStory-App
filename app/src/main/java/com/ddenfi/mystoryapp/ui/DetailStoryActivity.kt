package com.ddenfi.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ddenfi.mystoryapp.database.ListStoryItem
import com.ddenfi.mystoryapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA_USER)

        supportActionBar?.title = story?.name + "'s Story"
        binding.apply {
            tvDetailDescription.text = story?.description
        }
        Glide.with(this)
            .load(story?.photoUrl)
            .into(binding.ivDetailPhoto)

    }


    companion object {
        const val EXTRA_DATA_USER = "extra_dataUser"
    }
}