package com.impressiontrackingrecyclerview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.impressiontrackingrecyclerview.databinding.ActivityMainBinding
import com.impressiontrackingrecyclerview.verticalrecycler.VerticalRecyclerViewImpressionTrackingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.btnVerticalImpression.setOnClickListener {
            Intent(this, VerticalRecyclerViewImpressionTrackingActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}