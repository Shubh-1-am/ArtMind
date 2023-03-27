package com.example.artmind

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artmind.adapters.CustomAdapter
import com.example.artmind.fragments.ImageHistoryFragment
import com.example.artmind.interfaces.ImageDownloadListener
import com.example.artmind.interfaces.ImageLoadListener
import com.example.artmind.retrofit.RequestBody
import com.example.artmind.retrofit.RetrofitHelper
import com.example.artmind.viewmodels.ImageHistoryViewModel
import com.example.artmind.viewmodels.ImageHistoryViewModelFactory
import com.example.artmind.viewmodels.OpenAiViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), ImageDownloadListener, ImageLoadListener {
    private lateinit var openAiViewModel: OpenAiViewModel
    private lateinit var imageHistoryViewModel: ImageHistoryViewModel
    private lateinit var adapter: CustomAdapter
    private lateinit var promptEditText: TextInputEditText
    private lateinit var numberOfImagesInput: TextInputEditText
    private lateinit var slider: Slider
    private lateinit var generateButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var history: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openAiViewModel = ViewModelProvider(this)[OpenAiViewModel::class.java]
        val viewModelFactory = ImageHistoryViewModelFactory(application)
        imageHistoryViewModel = ViewModelProvider(this, viewModelFactory)[ImageHistoryViewModel::class.java]
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CustomAdapter(ArrayList(), this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        progressBar = findViewById(R.id.progress_bar)
        history = findViewById(R.id.history)
        promptEditText = findViewById(R.id.prompt_edit_text)
        slider = findViewById(R.id.number_picker_slider)
        numberOfImagesInput = findViewById(R.id.number_picker_input)
        numberOfImagesInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    val value = s.toString().toInt()
                    if (value < 1 || value > 10) {
                        numberOfImagesInput.error = "Number should be between 1 and 10"
                    } else {
                        numberOfImagesInput.error = null
                        slider.value = value.toFloat()
                    }
                } else {
                    numberOfImagesInput.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        slider.addOnChangeListener { _, value, _ ->
            numberOfImagesInput.setText(value.toInt().toString())
        }
        generateButton = findViewById(R.id.generateButton)


        generateButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (isNetworkConnected()){
                val promptText = promptEditText.text.toString()
                var numberOfImages = numberOfImagesInput.text.toString().toIntOrNull()
                val imageSize = "512x512"

                if (promptText.isBlank()) {
                    promptEditText.error = "Please enter a prompt"
                    progressBar.visibility = View.GONE
                    return@setOnClickListener
                }
                if (numberOfImages == null) {
                    numberOfImages = 1
                    numberOfImagesInput.text = Editable.Factory.getInstance().newEditable(numberOfImages.toString())
                }

                lifecycleScope.launch{
                    openAiViewModel.generateImages(promptText, numberOfImages, imageSize).observe(this@MainActivity) { images ->
                        adapter.updateData(images)
                        progressBar.visibility = View.GONE
                    }
                }
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        history.setOnClickListener(View.OnClickListener {
            val childViewContainer = findViewById<CoordinatorLayout>(R.id.activity_main_coordinator_layout)
            childViewContainer.visibility = View.GONE
            val fragment = ImageHistoryFragment.newInstance(imageHistoryViewModel)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_conatiner, fragment)
                .commit()
        })
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onImageDownload(url: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Image Download")
        request.setDescription("Downloading image...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${System.currentTimeMillis()}.jpg")
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        // Display a toast message when the download starts
        Toast.makeText(this, "Image download started", Toast.LENGTH_SHORT).show()

        // Register a BroadcastReceiver to listen for the download completion event
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Display a toast message when the download is complete
                Toast.makeText(context, "Image downloaded", Toast.LENGTH_SHORT).show()
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    override fun onImageLoad(url: String?) {
        url?.let{
            lifecycleScope.launch(Dispatchers.IO) {
                try{
                    val bitmap = Glide.with(this@MainActivity)
                        .asBitmap()
                        .load(url)
                        .submit()
                        .get()
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    val file = File(this@MainActivity.filesDir, fileName)
                    val fos = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                    imageHistoryViewModel.insertImage(file.absolutePath)
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}

//        val imageSizes = arrayOf("256x256", "512x512", "1024x1024")
//        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, imageSizes)
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        imageSizeSpinner.adapter = arrayAdapter
//        imageSizeSpinner.setSelection(0) // Set the default selection to the first item

//        numberOfImagesInput.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                if (s.toString().isNotEmpty()) {
//                    slider.value = s.toString().toFloat()
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                s?.toString()?.let { string ->
//                    if (string.isNotEmpty()) {
//                        val value = string.toInt()
//                        slider.value = value.toFloat()
//
//                    }
//                }
//            }
//        })