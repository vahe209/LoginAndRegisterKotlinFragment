package com.example.loginandregisterkotlinfragment.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.loginandregisterkotlinfragment.R
import com.example.loginandregisterkotlinfragment.databinding.ActivityMainBinding
import com.example.loginandregisterkotlinfragment.viewModel.MainActivityVM
import com.example.loginandregisterkotlinfragment.viewModel.SharedPreferencesDataBase
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityVM: MainActivityVM
    private  var bundle  = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivityVM = ViewModelProvider(this)[MainActivityVM::class.java]
        binding.menuBar.setOnClickListener {
            binding.navigationContainer.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener(this)

            createFragment(LoginFragment())
    }

    private fun createFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_fragment_layout, fragment)
        fragment.arguments = (bundle)
        ft.commit()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_login -> {
                createFragment(LoginFragment())
            }
            R.id.nav_register -> {
                createFragment(RegisterFragment())
            }
            R.id.nav_logOut -> {
                val sharedPreferencesDataBase = SharedPreferencesDataBase(this)
                createFragment(LoginFragment())
                sharedPreferencesDataBase.delToken()
            }
        }
        binding.navigationContainer.closeDrawer(GravityCompat.START)
        return true
    }
}



