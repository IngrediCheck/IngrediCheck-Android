package com.android.ingredicheck.Activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.Fregment.History_item
import com.android.ingredicheck.R
import com.android.ingredicheck.ResponceModelClass.HistoryData.Productdata
import com.android.ingredicheck.ViewModel.GetHistoryModel
import com.android.ingredicheck.ui.Views.Loader_view
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.Views.showToast
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import io.github.jan.supabase.SupabaseClient
import ir.kaaveh.sdpcompose.sdp


class ListViewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            IngrediCheckTheme {

                Surface(color = MaterialTheme.colorScheme.background) {

                    var supabase = SupabaseHelp().get_supaclient(this@ListViewActivity)!!

                    list_view(supabase, intent.getBooleanExtra("is_fvlist", false))
                }
            }
        }
    }

    @Composable
    private fun list_view(supabase: SupabaseClient, is_fv: Boolean) {

        var viewmode: GetHistoryModel = viewModel()
        var context = LocalContext.current

        val history_responce by viewmode.historydata.observeAsState(Productdata())
        val fvlist_responce by viewmode.fvlist.observeAsState(Productdata())
        val loderrespo by viewmode.is_loder.observeAsState(0)

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (!intent.hasExtra("serachpage")) {
                if (is_fv) viewmode.get_fvlistdata(context, true)
                else viewmode.get_history(context, true)
            }
        }

        if (!intent.hasExtra("serachpage")) {
            LaunchedEffect(Unit) {
                if (is_fv) viewmode.get_fvlistdata(context, true)
                else viewmode.get_history(context, true)
            }
        }

        var is_searchmode by remember { mutableStateOf(false) }
        if (intent.hasExtra("serachpage")) is_searchmode = true
        val focusRequester = remember { FocusRequester() }


        var textcolor_grey = colorResource(id = R.color.color_grey)
        var text by remember { mutableStateOf("") }
        var textcolor = colorResource(id = R.color.color_font)
        val keyboardController = LocalSoftwareKeyboardController.current

        val is_loderstatus: Int = loderrespo
        var productdata_list = if (is_fv) fvlist_responce else history_responce ?: emptyList()

        if (text.isNotEmpty()) {
            viewmode.get_serachdata(context, text)
        }

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.padding(vertical = 15.sdp))

            if (is_searchmode) {

                Row(modifier = Modifier.fillMaxWidth()) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                            .weight(1f)
                    )
                    {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(5.sdp)
                                .background(
                                    shape = RoundedCornerShape(8.dp),
                                    color = textcolor_grey
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {

                            Spacer(modifier = Modifier.padding(start = 5.dp))

                            Image(
                                painter = painterResource(id = R.drawable.ic_serach),
                                contentDescription = "back",
                                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.color_line)),
                                modifier = Modifier
                                    .width(22.sdp)
                                    .height(22.sdp)
                                    .align(Alignment.CenterVertically)
                            )

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 10.sdp, vertical = 5.sdp)
                                    .weight(1f)
                            ) {
                                BasicTextField(
                                    value = text,
                                    onValueChange = {
                                        text = it
                                    },

                                    textStyle = TextStyle(
                                        fontSize = 16.sp,
                                        color = textcolor,
                                        fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false
                                        )
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .onFocusChanged { focusState ->

                                        }
                                        .focusRequester(focusRequester),
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Text
                                    ),

                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            if (text.isEmpty()) {
                                                showToast(
                                                    context,
                                                    "Search"
                                                )
                                                return@KeyboardActions
                                            } else {
                                                keyboardController?.hide()
                                            }
                                        }
                                    )
                                )
                                if (text.isEmpty()) {
                                    Text(
                                        text = "Search ...",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.Gray,
                                            fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                                            platformStyle = PlatformTextStyle(
                                                includeFontPadding = false
                                            )
                                        ),
                                        modifier = Modifier
                                            .padding(start = 5.dp)
                                            .align(Alignment.CenterStart)
                                    )
                                }
                            }

                            if (text.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        text = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Clear",
                                    )
                                }
                            }
                        }

                    }

                    Text(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(5.sdp)
                            .wrapContentHeight()
                            .align(Alignment.CenterVertically)
                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {
                                if (intent.hasExtra("serachpage")) {
                                    (context as? Activity)?.finish()
                                } else {
                                    is_searchmode = false
                                    viewmode.get_history(context, true)
                                }
                            },
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        )
                    )

                    Spacer(modifier = Modifier.padding(end = 10.sdp))

                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            (context as? Activity)?.finish()
                        }

                )
                {

                    Spacer(modifier = Modifier.padding(horizontal = 2.sdp))

                    Row(modifier = Modifier
                        .wrapContentWidth()
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            (context as? Activity)?.finish()
                        }
                    ) {
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
                                .padding(5.sdp)
                                .wrapContentHeight(),
                            text = "Back",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.color_appgreen),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false,
                                ),
                            ),
                        )
                    }




                    Text(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(5.sdp)
                            .weight(1f)
                            .wrapContentHeight(),
                        text = "Lists",
                        fontSize = 17.sp,
                        fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(
                        modifier = Modifier
                            .width(30.sdp)
                            .height(20.sdp)
                    )

                    if (is_fv) {
                        Spacer(
                            modifier = Modifier
                                .width(20.sdp)
                                .height(20.sdp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_serach),
                            contentDescription = "back",
                            modifier = Modifier
                                .width(20.sdp)
                                .height(20.sdp)
                                .align(Alignment.CenterVertically)
                                .clickable(
                                    interactionSource = NoRippleInteractionSource(),
                                    indication = null
                                ) {
                                    is_searchmode = true
                                }
                        )
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 10.sdp))

                }
            }

            Spacer(modifier = Modifier.padding(vertical = 10.sdp))

            if (productdata_list!!.size > 0) {
                LazyColumn {
                    items(productdata_list) { item ->
                        History_item(context, supabase, launcher, item) { selectedItemId ->
                        }
                    }
                }
            }
        }

        if (is_loderstatus == 1) {
            Loader_view()
        }
    }
}