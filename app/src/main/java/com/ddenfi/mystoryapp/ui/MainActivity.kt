package com.ddenfi.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddenfi.mystoryapp.R
import com.ddenfi.mystoryapp.adapter.ListStoryAdapter
import com.ddenfi.mystoryapp.adapter.LoadingStateAdapter
import com.ddenfi.mystoryapp.databinding.ActivityMainBinding
import com.ddenfi.mystoryapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val  adapter = ListStoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getStories()
        setRecyclerView()
        setFab()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun setFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_map -> {
                startActivity(Intent(this,MapsActivity::class.java))
                true
            }
            R.id.submenu_lang -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                finish()
                true
            }
            R.id.submenu_logout -> {
                logOut()
                true}
            else -> false
        }
    }

    private fun setRecyclerView() {
        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
    }

    private fun logOut(){
            viewModel.resetSavedData()
        val intent = Intent(this,LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        Toast.makeText(application,"Logged Out!",Toast.LENGTH_SHORT).show()
    }

    private fun getStories() {
        viewModel.getToken().observe(this) { token ->
            viewModel.getStory(token).observe(this@MainActivity) {
                adapter.submitData(lifecycle,it)
                }
            }
        }
    }