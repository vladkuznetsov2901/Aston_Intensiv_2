package com.example.aston_intensiv_2

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.aston_intensiv_2.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnDrumStoppedListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()
    private var text = ""
    private val imageColors = arrayOf("VIOLET", "BLUE", "GREEN", "ORANGE")
    private val textColors = arrayOf("RED", "VIOLET", "BLUE LIGHT", "YELLOW")
    private var fileName = ""

    companion object {
        const val API_URL = "https://api.api-ninjas.com/v1/randomimage?category=nature"
        const val API_KEY = "xZ5eTS0bHKlniDsi0Lfx0A==0lnU4trnGtw1c2HY"
        const val TEXT_KEY = "text"
        const val IMAGE_KEY = "image"
        const val IMAGE_VISIBILITY = "imageVisibility"
        const val TEXT_VISIBILITY = "textVisibility"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.rainbowDrum.setOnDrumStoppedListener(this)


        binding.spinBtn.setOnClickListener {
            lifecycleScope.launch {

                val countOfSpin = Random.nextInt(8, 32)
                binding.rainbowDrum.rotateDrumToRandomSector(countOfSpin)
                binding.rainbowDrum.currentColor.collect {
                    binding.currentColor.text = it
                }
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, text)
        outState.putString(IMAGE_KEY, fileName)
        outState.putInt(IMAGE_VISIBILITY, binding.imageFromAPI.visibility)
        outState.putInt(TEXT_VISIBILITY, binding.customTextView.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(TEXT_KEY)?.let {
            binding.customTextView.setText(it)
            text = it
            binding.currentColor.text = it
        }
        savedInstanceState.getString(IMAGE_KEY)?.let {
            val file = viewModel.loadImageFromFile(this, it)
            if (file != null) {
                Glide.with(this)
                    .load(file)
                    .into(binding.imageFromAPI)
            }
        }
        val imageVisibility = savedInstanceState.getInt(IMAGE_VISIBILITY, View.GONE)
        val textVisibility = savedInstanceState.getInt(TEXT_VISIBILITY, View.VISIBLE)

        binding.imageFromAPI.visibility = imageVisibility
        binding.customTextView.visibility = textVisibility

    }

    override fun onDrumStopped(colorName: String) {
        if (colorName in imageColors) {
            binding.customTextView.visibility = View.GONE
            viewModel.getRandomImage(API_URL, API_KEY) { bitmap ->
                fileName = "random_image_${System.currentTimeMillis()}.jpg"
                viewModel.saveBitmapToFile(
                    this,
                    bitmap,
                    fileName
                )
                runOnUiThread {
                    Glide.with(this)
                        .load(bitmap)
                        .into(binding.imageFromAPI)
                }
            }

            binding.imageFromAPI.visibility = View.VISIBLE
        } else {
            text = binding.currentColor.text.toString()
            try {
                binding.customTextView.setColor(Color.parseColor(text))
            } catch (e: Exception) {
                binding.customTextView.setColor(Color.BLACK)
            }
            runOnUiThread {
                binding.customTextView.setText(text)

                binding.customTextView.visibility = View.VISIBLE
                binding.imageFromAPI.visibility = View.GONE
            }
        }
    }
}