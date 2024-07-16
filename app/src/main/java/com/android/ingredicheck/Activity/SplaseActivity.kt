package com.android.ingredicheck.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import com.android.ingredicheck.Constant.SharePrefrence
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.R
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import io.github.jan.supabase.gotrue.auth
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SplashScreen()
                }
            }
        }


        // Delay for 4 seconds and then start MainActivity
        SupabaseHelp().supabaseclient(this@SplaseActivity)

        lifecycleScope.launch {
            delay(4000)

            val token =
                SupabaseHelp().get_supaclient(this@SplaseActivity)!!.auth.currentSessionOrNull()?.accessToken
                    ?: ""
            if (token.isNotEmpty()) {
                SupabaseHelp.userSession =
                    SupabaseHelp().get_supaclient(this@SplaseActivity)!!.auth.currentSessionOrNull()
                SharePrefrence(this@SplaseActivity).set_token(token)
                startActivity(
                    Intent(this@SplaseActivity, MainActivity::class.java)
                        .putExtra(
                            "is_gatbydirect",
                            SharePrefrence(this@SplaseActivity).get_openscan()
                        )
                )
                finish()
            } else {
                startActivity(Intent(this@SplaseActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.img_splasetop),
            contentDescription = "Top Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(350.sdp),
            alignment = Alignment.TopStart
        )

        Image(
            painter = painterResource(id = R.drawable.img_splasebot),
            contentDescription = "Bottom Image",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .height(260.sdp),
            Alignment.BottomEnd
        )

        Image(
            painter = painterResource(id = R.drawable.img_splase),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(180.sdp)
                .align(Alignment.Center) // Adjust size as needed
        )
    }
}
