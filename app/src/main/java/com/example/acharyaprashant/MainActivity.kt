package com.example.acharyaprashant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ImageGridAdapter
    private lateinit var viewModel: MediaCoverageViewModel

    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, ViewModelFactory(MediaCoverageRepository(RetrofitInstance.api)))
            .get(MediaCoverageViewModel::class.java)

        setupRecyclerView()

        viewModel.mediaCoverages.observe(this, Observer {
            adapter.setImages(it)
            viewModel.getMediaCache(this)
        })

        viewModel.error.observe(this, Observer {
            Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
        })

        viewModel.bitMapData.observe(this, Observer {
            adapter.setImagesBitmap(it)
        })

        viewModel.getMediaCoverages(100)
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.rvMain)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.setItemViewCacheSize(10)
        adapter = ImageGridAdapter(this, listOf(), emptyMap()) { position, loading ->
            if (position == adapter.itemCount - 1 && !isLoading) {
                viewModel.getMediaCache(this)
            }
            isLoading = loading
        }
        recyclerView.adapter = adapter




    }
}
