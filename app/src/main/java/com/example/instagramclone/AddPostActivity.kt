package com.example.instagramclone

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.databinding.ActivityAddPostBinding
import com.example.instagramclone.databinding.ActivitySignupBinding

class AddPostActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddPostBinding.inflate(layoutInflater)
    }

    private var postImageUri: Uri? = null

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

        onClickImageView()



    }

    private fun onClickImageView(){
        binding.addPostImage.setOnClickListener{
            launcher.launch("image/*")
        }
    }
}