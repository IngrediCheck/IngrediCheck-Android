package com.android.ingredicheck.Activity

import android.app.Activity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.ingredicheck.R
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import ir.kaaveh.sdpcompose.sdp

class WebviewPageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    WebViewPage(intent.getStringExtra("is_link") ?: "")
                }
            }
        }
    }
}

@Composable
fun WebViewPage(url: String) {

    var context = LocalContext.current

    Column {

        Spacer(modifier = Modifier.height(30.sdp))

        Row(
            Modifier
                .clickable(
                    interactionSource = NoRippleInteractionSource(),
                    indication = null
                ) {
                    (context as? Activity)?.finish()
                }
                .fillMaxWidth())
        {

            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "back",
                modifier = Modifier
                    .width(22.sdp)
                    .height(22.sdp)
                    .align(Alignment.CenterVertically)
            )

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically)
                    .wrapContentHeight(),
                text = "SETTING",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.color_appgreen),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )

        }

        Spacer(modifier = Modifier.height(10.sdp))

        AndroidView(factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        }, update = { webView ->
            webView.loadUrl(url)
        })
    }
}