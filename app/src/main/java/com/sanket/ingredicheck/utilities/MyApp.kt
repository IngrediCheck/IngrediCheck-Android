package com.sanket.ingredicheck.utilities

import android.app.Application
import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth

class MyApp : Application() {
    // Declare SupabaseClient as a property in your application class
    lateinit var supabaseClient: SupabaseClient

    override fun onCreate() {
        super.onCreate()

        // Initialize the SupabaseClient
        supabaseClient = createSupabaseClient(
            supabaseUrl = "https://wqidjkpfdrvomfkmefqc.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndxaWRqa3BmZHJ2b21ma21lZnFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDczNDgxODksImV4cCI6MjAyMjkyNDE4OX0.sgRV4rLB79VxYx5a_lkGAlB2VcQRV2beDEK3dGH4_nI"

        ) {
            install(Auth)
        }
    }
}