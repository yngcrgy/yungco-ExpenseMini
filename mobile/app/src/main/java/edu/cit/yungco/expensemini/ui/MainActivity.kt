package edu.cit.yungco.expensemini.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.yungco.expensemini.R
import edu.cit.yungco.expensemini.ui.fragment.AddExpenseFragment
import edu.cit.yungco.expensemini.ui.fragment.DashboardFragment
import edu.cit.yungco.expensemini.ui.fragment.HistoryFragment
import edu.cit.yungco.expensemini.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNav)
        setupBottomNav()

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }
    }

    private fun setupBottomNav() {
        // Set active color from our brand colors
        bottomNav.itemIconTintList = getColorStateList(R.color.nav_active)
        bottomNav.itemTextColor = getColorStateList(R.color.nav_active)

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_dashboard -> DashboardFragment()
                R.id.nav_add -> AddExpenseFragment()
                R.id.nav_history -> HistoryFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> null
            }
            fragment?.let { loadFragment(it) } ?: false
        }
    }

    fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
        return true
    }

    fun navigateToTab(itemId: Int) {
        bottomNav.selectedItemId = itemId
    }
}