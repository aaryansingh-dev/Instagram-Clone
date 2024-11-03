package com.example.instagramclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.instagramclone.ViewModels.MyPostViewModel
import com.example.instagramclone.adapters.MyPostRvAdapter
import com.example.instagramclone.databinding.FragmentMyPostBinding


class MyPost : Fragment() {

    companion object {
        private const val COLS = 3
        private const val FRAGMENT_NAME = "MyPostFragment"
    }

    private lateinit var binding: FragmentMyPostBinding
    private lateinit var recyclerView: RecyclerView
    private val viewModel: MyPostViewModel by viewModels()
    private lateinit var adapter: MyPostRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)

        recyclerView = binding.myPostRecyclerView
        adapter =  MyPostRvAdapter(requireContext(), arrayListOf())
        binding.myPostRecyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.postList.observe(viewLifecycleOwner) { posts ->
            adapter.updateData(posts)  // Update the RecyclerView with new data
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(FRAGMENT_NAME, "Fragment destroyed.")
    }

}