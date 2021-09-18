package com.stho.mobipuzzle

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.stho.mobipuzzle.databinding.ActivityMainBinding
import android.content.Intent
import android.view.MenuItem
import androidx.core.app.TaskStackBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: Repository
    private lateinit var theme: Theme

    override fun onCreate(savedInstanceState: Bundle?) {
        repository = application.getRepository()
        theme = repository.settings.theme

        setTheme(theme.resId)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_settings,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        repository.settingsLD.observe(this, { settings -> onObserveSettings(settings) })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun findNavController(): NavController {
        // Note, when the nav_host_fragment fragment is replaced by FragmentContainerView in activity_main.xml
        // as suggested by lint, then
        //          Navigation.findNavController(this, R.id.nav_host_fragment);
        // doesn't work during onCreate() of the activity.
        // The fragment manager is still initializing.
        // See also: https://issuetracker.google.com/issues/142847973?pli=1
        val  navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        return navHostFragment.navController
    }

    private fun onObserveSettings(settings: Settings) {
        if (theme != settings.theme) {
            TaskStackBuilder.create(this)
                .addNextIntent(Intent(this, MainActivity::class.java))
                .addNextIntent(this.intent)
                .startActivities()
        }
    }
}

private val Theme.resId: Int
    get() = when (this) {
        Theme.GREEN -> R.style.Theme_MobiPuzzle_Green
        Theme.BORDEAUX -> R.style.Theme_MobiPuzzle_Bordeaux
        Theme.WHITE -> R.style.Theme_MobiPuzzle_White
        Theme.DEFAULT -> R.style.Theme_MobiPuzzle_Default
        else -> R.style.Theme_MobiPuzzle
    }


