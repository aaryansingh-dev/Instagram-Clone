package com.example.instagramclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramclone.AddPostActivity
import com.example.instagramclone.R
import com.example.instagramclone.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : BottomSheetDialogFragment() {

    companion object {

    }

    private lateinit var binding: FragmentAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        onPostClick()
        onReelClick()

        return binding.root
    }

    private fun onPostClick(){
        binding.addPost.setOnClickListener{
            startActivity(Intent(context, AddPostActivity::class.java))
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun onReelClick(){
        binding.addReel.setOnClickListener{
            //TODO
        }
    }

}