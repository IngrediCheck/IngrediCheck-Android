package com.android.ingredicheck.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.android.ingredicheck.R
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import ir.kaaveh.sdpcompose.sdp

class WelcomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IngrediCheckTheme {
                welcome_ui()
            }
        }
    }
}

@Composable
private fun welcome_ui() {

    var context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.sdp)
    )
    {

        Spacer(modifier = Modifier.height(110.sdp))

        Image(
            painter = painterResource(id = R.drawable.logo), contentDescription = "image",
            modifier = Modifier.height(80.sdp),
            alignment = Alignment.TopStart
        )

        Spacer(modifier = Modifier.height(20.sdp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.welcome_to),
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_bold)),
            color = colorResource(id = R.color.color_font),
            lineHeight = 18.sp,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )

        Spacer(modifier = Modifier.height(15.sdp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.welcome_to_ingradi),
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
            color = colorResource(id = R.color.color_lightfont),
            lineHeight = 19.sp,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )

        Spacer(modifier = Modifier.height(60.sdp))

        var background = colorResource(id = R.color.color_appgreen)
        var shape = CircleShape

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.sdp)
                .background(background, shape)
                .clickable(
                    interactionSource = NoRippleInteractionSource(),
                    indication = null
                ) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? Activity)?.finish()
                },
            contentAlignment = Alignment.Center
        )
        {
            Text(

                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                text = "Get Started",
                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                fontSize = 18.sp,
                color = colorResource(id = R.color.white),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )

            )
        }


    }


}
