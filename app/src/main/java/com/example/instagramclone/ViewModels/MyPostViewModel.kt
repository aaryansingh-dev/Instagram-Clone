package com.example.instagramclone.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instagramclone.utils.POSTLIST
import com.example.instagramclone.utils.POST_FOLDER
import com.example.instagramclone.utils.USER_POST_FOLDER
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPostViewModel: ViewModel() {
    private val _postList = MutableLiveData<List<String>>()
    val postList: LiveData<List<String>> get() = _postList


    init {
        fetchUserPostUrl()
    }
    private fun fetchUserPostUrl() {

        val doc = Firebase.firestore.collection(USER_POST_FOLDER).document(Firebase.auth.currentUser!!.uid)

        doc.get().addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()) {
                val listOfPosts = snapshot.get(POSTLIST) as? List<*>
                if (listOfPosts != null) {
                    val tempList = ArrayList<String>()
                    var completedRequests = 0  // Track completed requests

                    listOfPosts.forEach { postId ->
                        Firebase.firestore.collection(POST_FOLDER).document(postId.toString()).get()
                            .addOnSuccessListener { d ->
                                if (d.exists()) {
                                    val imageUrl = d.get("url").toString()
                                    tempList.add(imageUrl)
                                }
                            }
                            .addOnCompleteListener {
                                completedRequests++
                                if (completedRequests == listOfPosts.size) {
                                    _postList.value = tempList  // Update the LiveData when all URLs are fetched
                                }
                            }
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("MyPostViewModel", "Error fetching posts: ${it.localizedMessage}")
        }
    }

}