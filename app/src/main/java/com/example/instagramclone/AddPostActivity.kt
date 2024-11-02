package com.example.instagramclone

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.databinding.ActivityAddPostBinding
import com.example.instagramclone.databinding.ActivitySignupBinding
import com.example.instagramclone.model.Post
import com.example.instagramclone.utils.POST_FOLDER
import com.example.instagramclone.utils.uploadImage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddPostActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddPostBinding.inflate(layoutInflater)
    }

    private var postImageUri: Uri? = null
    private var post: Post? = null
    private var userPost:String? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        uri?.let {
            postImageUri = uri
            Log.d(
                "AddPostActivity",
                "URI added: $uri"
            )
            binding.addPostImage.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.addPostMaterialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.addPostMaterialToolbar.setNavigationOnClickListener{
            finish()
        }


        onClickImageView()
        onUploadButtonClick()


    }

    private fun onClickImageView(){
        binding.addPostImage.setOnClickListener{
            launcher.launch("image/*")
        }
    }

    private fun onUploadButtonClick()
    {
        binding.addPostUploadButton.setOnClickListener{
            if(postImageUri == null){
                Toast.makeText(baseContext, "Please add an image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.loadingPanel.visibility = View.VISIBLE
            uploadPost(){
                Log.d("AddPostActivity", "Post Added to the folders")
                Toast.makeText(baseContext, "Post added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun uploadPost(callback:() -> Unit){
        uploadImage(postImageUri!!, POST_FOLDER){url ->
            Log.d("AddPostActivity", "Post added to folder: $POST_FOLDER")
            if(url != null){
                val doc = Firebase.firestore.collection(POST_FOLDER).document()
                val authorId = Firebase.auth.currentUser!!.uid
                post = Post(authorId, url, binding.addPostCaption.editText?.text.toString())
                doc.set(post!!).addOnSuccessListener {
                    Log.d("AddPostActivity", "Post Entry added to firestore with id: ${doc.id}")

                    callback()
                }


            }
        }

    }

    private fun updateUserPost(postID: String, userId: String){
        Firebase.firestore.collection("")
    }
}