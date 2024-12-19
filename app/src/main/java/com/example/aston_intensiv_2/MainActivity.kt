package com.example.aston_intensiv_2

import android.graphics.Color
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

    private val imageColors = arrayOf("VIOLET", "BLUE", "GREEN", "ORANGE")
    private val textColors = arrayOf("RED", "VIOLET", "BLUE LIGHT", "YELLOW")

    companion object {
        const val API_URL = "https://api.api-ninjas.com/v1/randomimage?category=nature"
        const val API_KEY = "xZ5eTS0bHKlniDsi0Lfx0A==0lnU4trnGtw1c2HY"
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

    override fun onDrumStopped(colorName: String) {
        if (colorName in imageColors) {
            binding.customTextView.visibility = View.GONE
            viewModel.getRandomImage(API_URL, API_KEY) { bitmap ->
                runOnUiThread {
                    Glide.with(this)
                        .load(bitmap)
                        .into(binding.imageFromAPI)
                }
            }

            binding.imageFromAPI.visibility = View.VISIBLE
        } else {
            val text = binding.currentColor.text.toString()
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