package com.android.ingredicheck.Fregment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.ingredicheck.Activity.GetProductAcrivity
import com.android.ingredicheck.Activity.ListViewActivity
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.R
import com.android.ingredicheck.ResponceModelClass.HistoryData.ProductResponce
import com.android.ingredicheck.ResponceModelClass.HistoryData.Productdata
import com.android.ingredicheck.ViewModel.GetHistoryModel
import com.android.ingredicheck.ui.Views.Loader_view
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.gson.Gson
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration.Companion.hours

@Composable
fun ListScreen() {

    var context = LocalContext.current
    var supabase = SupabaseHelp().get_supaclient(context)!!
    var viewmode: GetHistoryModel = viewModel()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewmode.get_history(context, true)
        viewmode.get_fvlistdata(context, false)
    }

    LaunchedEffect(Unit) {
        viewmode.get_history(context, true)
        viewmode.get_fvlistdata(context, false)
    }

    val history_responce by viewmode.historydata.observeAsState(Productdata())
    val fvlist_responce by viewmode.fvlist.observeAsState(Productdata())
    var isRefreshing by remember { mutableStateOf(false) }

    val loderrespo by viewmode.is_loder.observeAsState(0)
    val is_loderstatus: Int = loderrespo


    val history_list = history_responce ?: emptyList()
    val fvproductlist = fvlist_responce ?: emptyList()


    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.padding(top = 10.sdp, bottom = 10.sdp))

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

            Spacer(modifier = Modifier.padding(horizontal = 10.sdp))

            Spacer(modifier = Modifier.size(20.sdp))

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.sdp)
                    .weight(1f)
                    .wrapContentHeight(),
                text = "Lists",
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.color_font),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )

            if (history_list.size > 0) {
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
                            val intent =
                                Intent(context, ListViewActivity::class.java)
                                    .putExtra("serachpage", true)
                                    .putExtra("is_fvlist", false)
                            //   context.startActivity(intent)
                            launcher.launch(intent)
                        }

                )
            }else
            {
                Spacer(modifier = Modifier.size( 20.sdp))
            }
            Spacer(modifier = Modifier.padding(horizontal = 10.sdp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.sdp)
                    .wrapContentHeight(),
                text = "Favorites",
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.black),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )


            if (fvproductlist.size > 0) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(5.sdp)
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            val intent =
                                Intent(context, ListViewActivity::class.java)
                                    .putExtra("is_fvlist", true)
                            //   context.startActivity(intent)
                            launcher.launch(intent)
                        }
                        .wrapContentHeight(),
                    text = "View all",
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

        }

        if (fvproductlist.size > 0) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fvproductlist) { item ->
                    fvlist_item(
                        context = context,
                        supabase = supabase,
                        item = item,
                        launcher = launcher
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(5.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_imageplase),
                    contentDescription = "back",
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(80.sdp)

                )

                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(5.sdp)
                        .wrapContentHeight(),
                    text = "No Favorite products yet",
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_lightfont),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(5.sdp)
                    .wrapContentHeight(),
                text = "Recents Scans",
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.black),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                ),
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (history_list.size > 0) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(5.sdp)
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            val intent =
                                Intent(context, ListViewActivity::class.java)
                                    .putExtra("is_fvlist", false)
                            //   context.startActivity(intent)
                            launcher.launch(intent)
                        }

                        .wrapContentHeight(),
                    text = "View all",
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
        }

        if (history_list.size > 0) {

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    isRefreshing = true
                    viewmode.get_history(context, false)
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(2000) // 2 seconds delay
                        isRefreshing = false
                    }
                }
            )
            {
                LazyColumn {
                    items(history_list) { item ->
                        History_item(context, supabase, launcher, item) { selectedItemId ->
                        }
                    }
                }
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp),
                Arrangement.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.img_emaptylisty),
                    contentDescription = "back",
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(100.sdp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(5.sdp)
                        .align(Alignment.CenterHorizontally)
                        .wrapContentHeight(),
                    text = "No products scanned yet",
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_lightfont),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )
            }
        }
    }

    if (is_loderstatus == 1) {
        Loader_view()
    }
}

@Composable
fun History_item(
    context: Context,
    supabase: SupabaseClient,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    item: ProductResponce,
    content: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var app_greencolor = colorResource(id = R.color.color_appgreen)
    var app_red = colorResource(id = R.color.color_red)

    if (item != null) {
        Row(
            Modifier
                .fillMaxWidth()

                .clickable(
                    interactionSource = NoRippleInteractionSource(),
                    indication = null
                ) {
                    val intent = Intent(context, GetProductAcrivity::class.java).putExtra(
                        "iswithdata", Gson().toJson(item)
                    )
                    //context.startActivity(intent)
                    launcher.launch(intent)
                }
                .padding(10.sdp)
        ) {

            if (item.images != null && item.images!!.size > 0) {

                var image by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {

                        image = if (item.images.get(0).url.isEmpty()) {
                            supabase.storage.from("productimages")
                                .createSignedUrl(
                                    path = item.images.get(0).imageFileHash,
                                    expiresIn = 1.hours
                                )
                        } else item.images.get(0).url
                    }
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_imageplase),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(60.sdp)
                        .height(75.sdp)
                )

            } else {
                Spacer(
                    modifier = Modifier
                        .size(60.sdp)
                        .background(
                            color = colorResource(id = R.color.black),
                            shape = RoundedCornerShape(8.dp)
                        )
                )

            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 15.sdp)
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.Start)
                        .wrapContentHeight(),
                    text = if (item.brand == null) "Unknow Brand" else item.brand,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.color_font),
                    lineHeight = 18.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.padding(1.sdp))


                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.Start)
                        .wrapContentHeight(),
                    text = if (item.name == null) "Unknow Brand" else item.name,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Start,
                    color = colorResource(id = R.color.color_lightfont),
                    lineHeight = 15.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.padding(top = 8.sdp))


                Text(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.Start)
                        .wrapContentHeight(),
                    text = if (item.name == null) "" else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatDate(item.created_at)
                    } else {
                        ""
                    },
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_lightfont),
                    lineHeight = 14.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )


                Spacer(modifier = Modifier.height(16.dp))

            }

            var chnage =
                if (item.ingredient_recommendations != null && item.ingredient_recommendations.size > 0) false
                else true

            DrawCircleExample(chnage)

            Spacer(modifier = Modifier.padding(5.sdp))

        }
    }
}


@Composable
fun fvlist_item(
    context: Context,
    supabase: SupabaseClient,
    item: ProductResponce,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val coroutineScope = rememberCoroutineScope()

    var app_greencolor = colorResource(id = R.color.color_appgreen)
    var app_red = colorResource(id = R.color.color_red)

    if (item != null) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = NoRippleInteractionSource(),
                    indication = null
                ) {
                    val intent = Intent(context, GetProductAcrivity::class.java)
                        .putExtra("iswithdata", Gson().toJson(item))
                        .putExtra("isgofv", true)
                    //  context.startActivity(intent)
                    launcher.launch(intent)
                }
        ) {

            if (item.images != null && item.images!!.size > 0) {

                var image by remember { mutableStateOf("") }

                LaunchedEffect(Unit) {
                    coroutineScope.launch {

                        image = if (item.images.get(0).url.isEmpty()) {
                            supabase.storage.from("productimages")
                                .createSignedUrl(
                                    path = item.images.get(0).imageFileHash,
                                    expiresIn = 1.hours
                                )
                        } else item.images.get(0).url
                    }
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_imageplase),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(60.sdp)
                        .height(80.sdp)
                )

                /*GlideImage(
                    imageModel = { image }, // loading a network image using an URL.
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    ),
                    modifier = Modifier
                        .width(60.sdp)
                        .height(90.sdp),
                    requestOptions = { RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL) }
                )*/

            } else {
                Spacer(
                    modifier = Modifier
                        .size(60.sdp)
                        .background(
                            color = colorResource(id = R.color.black),
                            shape = RoundedCornerShape(8.dp)
                        )
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(inputDate: String): String {
    // Parse the ISO 8601 date string to a ZonedDateTime object
    val zonedDateTime = ZonedDateTime.parse(inputDate)

    // Define the desired format
    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH)
    } else {
        ""
    }
    // Format the date
    return zonedDateTime.format(formatter as DateTimeFormatter?)
}



