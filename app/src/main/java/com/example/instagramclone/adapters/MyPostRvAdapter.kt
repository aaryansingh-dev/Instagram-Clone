package com.example.instagramclone.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.instagramclone.databinding.MyPostRvDesignBinding
import com.squareup.picasso.Picasso

class MyPostRvAdapter(var context: Context, var postList: ArrayList<String>): RecyclerView.Adapter<MyPostRvAdapter.ViewHolder>() {

    companion object {
        private const val COLS = 3
    }

    inner class ViewHolder(var binding: MyPostRvDesignBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            Log.d("MyPostRVAdapter - URL", postList[position])
            Picasso.get().load(postList[position]).into(binding.myPostRvImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = MyPostRvDesignBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateData(newPosts: List<String>){
        postList = newPosts as ArrayList<String>
        notifyDataSetChanged()
    }
}