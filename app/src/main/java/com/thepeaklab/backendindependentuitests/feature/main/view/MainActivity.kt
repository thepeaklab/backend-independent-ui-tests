package com.thepeaklab.backendindependentuitests.feature.main.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.thepeaklab.backendindependentuitests.R
import com.thepeaklab.backendindependentuitests.databinding.ActivityMainBinding
import com.thepeaklab.backendindependentuitests.feature.main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.setLifecycleOwner(this)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding.viewModel = model

        setSupportActionBar(toolbar)
    }
}
