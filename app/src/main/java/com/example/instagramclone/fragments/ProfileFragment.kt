package com.example.instagramclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagramclone.R
import com.example.instagramclone.SignupActivity
import com.example.instagramclone.adapters.ViewPagerAdapter
import com.example.instagramclone.databinding.FragmentProfileBinding
import com.example.instagramclone.model.User
import com.example.instagramclone.utils.USER_NODE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)

        viewPagerAdapter.addFragments(MyPost(), "MY POSTS")
        viewPagerAdapter.addFragments(MyReels(), "MY REELS")

        binding.profileViewPager.adapter = viewPagerAdapter
        binding.profileTabLayout.setupWithViewPager(binding.profileViewPager)

        return binding.root
    }

    companion object {
    }

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            val user = it.toObject<User>()!!
            setEditButton(user)
            binding.profileUsername.text = user.name
            binding.profileBio.text = user.email
            if(!user.image.equals(null))
            {
                Picasso.get().load(user.image).into(binding.profileProfileImageView)
            }
        }
    }

    private fun setEditButton(user: User){
        binding.profileEditProfileButton.setOnClickListener{
            val intent = Intent(context, SignupActivity::class.java)
            intent.putExtra("MODE", 1)
            intent.putExtra("USERNAME", user.name)
            intent.putExtra("EMAIL", user.email)
            intent.putExtra("PASSWORD", user.password)
            intent.putExtra("PROFILE_URL", user.image)
            startActivity(intent)
        }
    }
}