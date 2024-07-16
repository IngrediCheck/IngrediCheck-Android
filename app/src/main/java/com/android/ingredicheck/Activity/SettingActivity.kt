package com.android.ingredicheck.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.ingredicheck.Constant.SharePrefrence
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.R
import com.android.ingredicheck.ViewModel.MyViewmodel
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.launch

class SettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    var supabase = SupabaseHelp().get_supaclient(this@SettingActivity)!!

                    setting_screen(supabase)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun setting_screen(supabase: SupabaseClient) {
    var context = LocalContext.current
    var isChecked by remember { mutableStateOf(false) }
    val greyColor = colorResource(id = R.color.color_back)
    isChecked = SharePrefrence(context).get_openscan()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var viewmode: MyViewmodel = viewModel()

    val pref_response by viewmode.is_delete.observeAsState(false)
    val deleteme: Boolean = pref_response ?: false

    if (deleteme) {
        SharePrefrence(context).clear_Pref()

        context.startActivity(Intent(context, LoginActivity::class.java))
        (context as Activity).finishAffinity()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(colorResource(id = R.color.color_back))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            Arrangement.Center,
            Alignment.CenterVertically
        )
        {

            Spacer(modifier = Modifier.padding(20.dp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.sdp)
                    .weight(1f)
                    .wrapContentHeight(),
                text = "SETTINGS",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.color_font),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )


            Image(
                painter = painterResource(id = R.drawable.ic_closeback),
                contentDescription = "Top Image",
                modifier = Modifier
                    .wrapContentWidth()
                    .height(40.sdp)
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        (context as Activity).finish()
                    }
            )
            Spacer(modifier = Modifier.padding(5.dp))

        }

        Spacer(modifier = Modifier.padding(20.dp))

        Text(
            modifier = Modifier
                .padding(5.sdp)
                .align(Alignment.Start),
            text = "SETTINGS",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.color_lightfont),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                ),
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = colorResource(id = R.color.white)
                )
                .wrapContentHeight()
        )
        {

            Text(
                modifier = Modifier
                    .padding(10.sdp),
                text = "Start Scanning on App Start",
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.color_font),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = it
                    SharePrefrence(context).set_openscan(isChecked)
                }
            )

            Spacer(modifier = Modifier.padding(horizontal = 6.sdp))

        }


        Spacer(modifier = Modifier.padding(15.dp))

        Text(
            modifier = Modifier
                .padding(5.sdp)
                .align(Alignment.Start),
            text = "ACCOUNT",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.color_lightfont),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                ),
            ),
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = colorResource(id = R.color.white)
                )
                .wrapContentHeight()
        ) {

            if (SharePrefrence(context).get_boolvalue("Googlelogin")) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {

                            scope.launch {
                                supabase.auth.signOut()

                            }
                            SharePrefrence(context).clear_Pref()
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            (context as Activity).finishAffinity()

                        }
                        .wrapContentHeight(),
                    Arrangement.Start,
                    Alignment.CenterVertically
                )
                {

                    Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_singout),
                        contentDescription = "Top Image",
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(20.sdp)
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 2.sdp))

                    Text(
                        modifier = Modifier
                            .padding(10.sdp),
                        text = "Sign out",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_red),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 7.sdp))
                }


                Spacer(modifier = Modifier.height(2.dp))

                DrawHorizontalLine(greyColor)

                Spacer(modifier = Modifier.height(5.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        scope.launch { sheetState.show() }
                    }
                    .wrapContentHeight(),
                Arrangement.Start,
                Alignment.CenterVertically
            )
            {

                Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_resetapp),
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(40.sdp)
                )

                Spacer(modifier = Modifier.padding(horizontal = 2.sdp))

                Text(
                    modifier = Modifier
                        .padding(10.sdp),
                    text = if (SharePrefrence(context).get_boolvalue("Googlelogin")) "Delete Data & Account" else "Reset App State",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.color_red),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.padding(horizontal = 7.sdp))
            }

        }

        Spacer(modifier = Modifier.padding(15.dp))

        Text(
            modifier = Modifier
                .padding(5.sdp)
                .align(Alignment.Start),
            text = "ABOUT",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.color_lightfont),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                ),
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = colorResource(id = R.color.white)
                )
                .wrapContentHeight()
        ) {

            Spacer(modifier = Modifier.padding(vertical = 3.sdp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        val intent = Intent(context, WebviewPageActivity::class.java).putExtra(
                            "is_link", "https://www.ingredicheck.app/about"
                        )
                        context.startActivity(intent)
                    }
                    .wrapContentHeight(),
                Arrangement.Start
            )
            {

                Spacer(modifier = Modifier.padding(horizontal = 4.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(30.sdp)
                        .padding(5.sdp)
                )


                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    Arrangement.Center
                ) {

                    Text(
                        text = "About me",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawHorizontalLine(greyColor)
                }

                Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier
                        .width(18.sdp)
                        .height(18.sdp)
                        .rotate(180f)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

            }

            Spacer(modifier = Modifier.padding(vertical = 3.sdp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        val intent = Intent(context, WebviewPageActivity::class.java).putExtra(
                            "is_link", "https://www.ingredicheck.app/about"
                        )
                        context.startActivity(intent)
                    }

                    .wrapContentHeight(),
                Arrangement.Start
            )
            {

                Spacer(modifier = Modifier.padding(horizontal = 4.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_help),
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(30.sdp)
                        .padding(5.sdp)
                )


                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    Arrangement.Center
                ) {

                    Text(
                        text = "Help",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawHorizontalLine(greyColor)

                }

                Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier
                        .width(18.sdp)
                        .height(18.sdp)
                        .rotate(180f)
                        .align(Alignment.CenterVertically)
                )



                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

            }

            Spacer(modifier = Modifier.padding(vertical = 3.sdp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        val intent = Intent(context, WebviewPageActivity::class.java).putExtra(
                            "is_link", "https://www.ingredicheck.app/terms-conditions"
                        )
                        context.startActivity(intent)
                    }
                    .wrapContentHeight(),
                Arrangement.Start
            )
            {

                Spacer(modifier = Modifier.padding(horizontal = 4.sdp))


                Image(
                    painter = painterResource(id = R.drawable.ic_term),
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(30.sdp)
                        .padding(5.sdp)
                )


                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    Arrangement.Center
                ) {

                    Text(
                        text = "Terms of Use",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawHorizontalLine(greyColor)

                }

                Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier
                        .width(18.sdp)
                        .height(18.sdp)
                        .rotate(180f)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))
            }

            Spacer(modifier = Modifier.padding(vertical = 3.sdp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        val intent = Intent(context, WebviewPageActivity::class.java).putExtra(
                            "is_link", "https://www.ingredicheck.app/privacy-policy"
                        )
                        context.startActivity(intent)
                    }

                    .wrapContentHeight(),
                Arrangement.Start
            )
            {

                Spacer(modifier = Modifier.padding(horizontal = 4.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_policy),
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(30.sdp)

                        .padding(5.sdp)
                )

                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    Arrangement.Center
                ) {

                    Text(
                        text = "Privacy Policy",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DrawHorizontalLine(greyColor)

                }

                Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier
                        .width(18.sdp)
                        .height(18.sdp)
                        .rotate(180f)
                        .align(Alignment.CenterVertically)
                )



                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

            }

            Spacer(modifier = Modifier.padding(vertical = 3.sdp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                Arrangement.Start
            )
            {

                Spacer(modifier = Modifier.padding(horizontal = 4.sdp))

                Image(
                    painter = painterResource(id = R.drawable.ic_app),
                    contentDescription = "Top Image",
                    modifier = Modifier
                        .wrapContentWidth()
                        .size(30.sdp)
                        .padding(5.sdp)
                )


                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    Arrangement.Center
                ) {

                    Text(
                        text = "IngrediCheck for android1.0(1)",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                }

                Spacer(modifier = Modifier.padding(horizontal = 5.sdp))

                Spacer(modifier = Modifier.padding(horizontal = 8.sdp))

            }

            Spacer(modifier = Modifier.padding(vertical = 3.sdp))

        }
    }


    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            // Your bottom sheet content goes here
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 100.sdp)
            ) {


                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(5.sdp)
                        .align(Alignment.CenterHorizontally)
                        .wrapContentHeight(),
                    text = "Your data cannot be recovered",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_font),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                DrawHorizontalLine(greyColor)

                Spacer(modifier = Modifier.padding(8.sdp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.sdp)
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            scope.launch {
                                supabase.auth.signOut()
                            }
                            viewmode.delete_me(context)
                        }
                        .wrapContentHeight(),
                    text = "I Understand",
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_appgreen),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.padding(8.sdp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.sdp)
                        .align(Alignment.CenterHorizontally)
                        .wrapContentHeight()
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            scope.launch { sheetState.hide() }
                        },
                    text = "Cancel",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_font),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )


            }
        },
        sheetShape = RoundedCornerShape(topStart = 20.sdp, topEnd = 20.sdp),
        content = {
            // Your main screen content goes here
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

            }
        }
    )

}