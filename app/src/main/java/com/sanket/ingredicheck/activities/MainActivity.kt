package com.sanket.ingredicheck.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sanket.ingredicheck.R
import com.sanket.ingredicheck.databinding.ActivityMainBinding
import com.sanket.ingredicheck.databinding.FragmentHomeBinding
import com.sanket.ingredicheck.fragments.HomeFragment
import com.sanket.ingredicheck.utilities.MyApp
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.signInAnonymously
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        init()
        setListener()
        loadFragments()
        signIn()
    }

    fun signIn() {
        CoroutineScope(Dispatchers.IO).launch {
            // Get the current session
            val supabaseClient = (applicationContext as MyApp).supabaseClient
            val session: UserSession? = supabaseClient.auth.sessionManager.loadSession()
            if (session == null) {
                supabaseClient.auth.signInAnonymously()
            }
        }
    }

    fun init(){
        binding.active = "home"
    }

    fun setListener(){
        binding.home.setOnClickListener {
            binding.listIm.setColorFilter(ContextCompat.getColor(this, R.color.bottom_gray), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.homeIm.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.active = "home"
        }

        binding.list.setOnClickListener {
            binding.listIm.setColorFilter(ContextCompat.getColor(this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.homeIm.setColorFilter(ContextCompat.getColor(this, R.color.bottom_gray), android.graphics.PorterDuff.Mode.SRC_IN)
            binding.active = "list"
        }
    }

    fun loadFragments() {
        val homeFragment = HomeFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.fragment, homeFragment, "homeFragment")
        transaction.commit()
    }
}