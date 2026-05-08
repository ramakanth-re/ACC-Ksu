package app.akilesh.qacc.ui

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import app.akilesh.qacc.R
import app.akilesh.qacc.databinding.ActivityMainBinding
import app.akilesh.qacc.utils.AppUtils.getColorAccent
import app.akilesh.qacc.utils.AppUtils.navAnim

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decorView = window.decorView
        decorView.systemUiVisibility = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                window.navigationBarColor = Color.TRANSPARENT
            }
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val useSystemAccent = sharedPreferences.getBoolean("system_accent", false)
        val color = if (useSystemAccent) getColorAccent()
        else ResourcesCompat.getColor(resources, R.color.colorPrimary, theme)
        setColor(color)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController


        // Hide bottom app bar & ext. fab while creating an accent
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.color_picker, R.id.dark_accent, R.id.customisation, R.id.create_all_fragment -> {
                    binding.bottomAppBar.visibility = View.GONE
                    binding.xFab.visibility = View.GONE
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            window.navigationBarColor = Color.TRANSPARENT
                        }
                    }
                }
                else -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.xFab.visibility = View.VISIBLE
                    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_YES -> {
                            window.navigationBarColor = Color.parseColor("#1E1E1E")
                        }
                    }
                }
            }
        }

        binding.xFab.setOnClickListener {
            navController.navigate(R.id.color_picker, null, navAnim)
        }

        binding.bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.settings -> navController.navigate(R.id.settings, null, navAnim)
                R.id.info -> navController.navigate(R.id.info, null, navAnim)
            }
            true
        }

        /*
         * Use navigation icon to navigate home.
         * May not be the correct way, but convenient.
         */
        binding.bottomAppBar.setNavigationOnClickListener {
            navController.navigate(R.id.home, null, navAnim)
        }

    }

    private fun setColor(
        colorAccent: Int
    ) {
        val colorStateList = ColorStateList.valueOf(colorAccent)
        binding.apply {
            xFab.apply {
                strokeColor = colorStateList
                setTextColor(colorAccent)
            }
            bottomAppBar.navigationIcon?.setTintList(colorStateList)
            bottomAppBar.menu.forEach {
                it.iconTintList = colorStateList
            }
        }
    }

}
