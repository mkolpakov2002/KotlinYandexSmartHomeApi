package ru.hse.miem.yandexsmarthomeapi.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import ru.hse.miem.yandexsmarthomeapi.R
import ru.hse.miem.yandexsmarthomeapi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)

        // Setup NavController and AppBarConfiguration
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Setup ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Optional: Setup bottom navigation or drawer navigation if needed
        // NavigationUI.setupWithNavController(binding.bottomNavView, navController)
        // NavigationUI.setupWithNavController(binding.drawerNavView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}