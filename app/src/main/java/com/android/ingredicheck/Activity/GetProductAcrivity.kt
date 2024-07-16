package com.android.ingredicheck.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.DataClass.FeedbackData
import com.android.ingredicheck.DataClass.ImageInfo
import com.android.ingredicheck.DataClass.ProductImage
import com.android.ingredicheck.R
import com.android.ingredicheck.ResponceModelClass.AnalyzeResponce.AnalyzeResponce
import com.android.ingredicheck.ResponceModelClass.HistoryData.ProductResponce
import com.android.ingredicheck.ViewModel.MyViewmodel
import com.android.ingredicheck.ViewModel.ProductModel
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.Views.showToast
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.hours


class GetProductAcrivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {


                    var supabase = SupabaseHelp().get_supaclient(this@GetProductAcrivity)!!

                    var is_imagedata = intent.hasExtra("Imagedata")
                    var _basedata = if (is_imagedata) intent.getStringExtra("Imagedata") ?: ""
                    else intent.getStringExtra("barcodedata") ?: ""
                    //  gatting_product(is_imagedata, _basedata, supabase)


                    gatting_product(
                        is_imagedata,
                        _basedata,
                        supabase,
                        intent.hasExtra("iswithdata"),
                        intent
                    ) {

                        if (it.equals("byimage")) {
                            val resultIntent = Intent().apply {
                                putExtra("result_key", "isphoto")
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun gatting_product(
    is_imagedata: Boolean,
    _basedata: String,
    supabase: SupabaseClient,
    hasExtra: Boolean,
    intent: Intent,
    iscloseresult: (String) -> Unit
) {

    var ismessagetime by remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        ismessagetime = true
    }


    var viewmode: ProductModel = viewModel()
    var context = LocalContext.current
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(initialPage = 0)
    val product_response by viewmode.productdata.observeAsState(null)
    val is_productdata = product_response ?: ProductResponce()


    val uploadimage = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration here
            easing = { it } // You can use different easing functions
        ))


    val loderrespo by viewmode.is_loder.observeAsState(0)
    val is_loderstatus: Int = loderrespo ?: 0

    val fali_apire by viewmode.fali_api.observeAsState(false)
    val analze_response by viewmode.AnalyzeResponcedata.observeAsState(AnalyzeResponce())
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration here
            easing = { it } // You can use different easing functions
        ))

    val scope = rememberCoroutineScope()
    var isfromfv by remember { mutableStateOf(false) }
    isfromfv = intent.hasExtra("isgofv")
    var is_fv by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (hasExtra) {
            var historydata =
                Gson().fromJson(intent.getStringExtra("iswithdata"), ProductResponce::class.java)
            viewmode.product_historydata(context, historydata)
        } else {
            if (is_imagedata) {
                viewmode.get_imagedata(context, _basedata, UUID.randomUUID().toString())
            } else {
                viewmode.get_barcodedata(context, _basedata, UUID.randomUUID().toString())
            }
        }
    }
    var textis = ""


    if (is_loderstatus == 1) {
        var fvdemo = is_productdata.favorited
        is_fv = fvdemo

        if (is_productdata != null && is_productdata!!.ingredients != null && is_productdata!!.ingredients!!.size < 1) {
            isfromfv = true
        }

        if (is_productdata != null && is_productdata!!.ingredients != null && is_productdata!!.ingredients!!.size > 0) {
            is_productdata!!.ingredients!!.forEach {
                textis = textis + " " + it.name
                it.ingredients.forEach {
                    textis = textis + " (" + it.name + ") "
                    it.ingredients.forEach {
                        textis = textis + " (" + it.name + ") "
                    }
                }
            }
        }


        var client_is =
            if (is_productdata!!.client_activity_id.isEmpty()) is_productdata!!.list_item_id
            else is_productdata!!.client_activity_id
        Box(modifier = Modifier.fillMaxSize())
        {

            Log.e("mydatachnagelader", "loader make it part   =-  ")

            if (is_productdata != null && is_productdata!!.ingredients != null && is_productdata!!.ingredients!!.size > 0) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Spacer(modifier = Modifier.padding(vertical = 10.sdp))

                    Row(
                        Modifier
                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {
                                (context as? Activity)?.finish()
                            }
                            .fillMaxWidth(),
                        Arrangement.Center,
                        Alignment.CenterVertically
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
                                .align(Alignment.CenterVertically)
                                .wrapContentHeight(),
                            text = "Back",
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

                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = "",
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

                        Spacer(modifier = Modifier.weight(1f))

                        if (!isfromfv) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_uploadimage),
                                contentDescription = "back",
                                modifier = Modifier
                                    .width(26.sdp)
                                    .height(26.sdp)
                                    .clickable(
                                        interactionSource = NoRippleInteractionSource(),
                                        indication = null
                                    ) {


                                        if (intent.hasExtra("iswithdata")) {
                                            var feedbackdata = FeedbackData()
                                            feedbackdata.rating = 0;
                                            feedbackdata.note = "";
                                            var resonlist = ArrayList<String>()
                                            feedbackdata.reasons = resonlist
                                            scope.launch {
                                                uploadimage.show()
                                            }
                                        } else {

                                            if (intent.hasExtra("Imagedata")) {
                                                val resultIntent = Intent().apply {
                                                    putExtra("result_data", "goforimage")
                                                }
                                                (context as Activity).setResult(
                                                    Activity.RESULT_OK,
                                                    resultIntent
                                                )
                                                (context as Activity).finish()
                                            } else {
                                                var feedbackdata = FeedbackData()
                                                feedbackdata.rating = 0;
                                                feedbackdata.note = "";
                                                var resonlist = ArrayList<String>()
                                                feedbackdata.reasons = resonlist
                                                scope.launch {
                                                    uploadimage.show()
                                                }
                                            }


                                        }


                                        /*val intent =
                                            Intent(context, FeedBackCaptureActivity::class.java)
                                                .putExtra(
                                                    "Feedbackdata",
                                                    Gson().toJson(feedbackdata)
                                                )
                                                .putExtra(
                                                    "clientid",
                                                    client_is
                                                )
                                        context.startActivity(intent)*/
                                    }
                            )
                        }


                        Spacer(modifier = Modifier.padding(horizontal = 8.sdp))


                        Image(
                            painter = painterResource(id = if (is_fv) R.drawable.ic_fvfill else R.drawable.ic_like),
                            contentDescription = "fvdata",
                            modifier = Modifier
                                .width(22.dp)
                                .height(22.dp)
                                .clickable(
                                    interactionSource = NoRippleInteractionSource(),
                                    indication = null
                                ) {
                                    if (is_fv) {
                                        viewmode.removefv(
                                            context,
                                            client_is
                                        )
                                    } else {
                                        viewmode.addfv(
                                            context,
                                            client_is
                                        )
                                    }
                                    if (is_fv) {
                                        is_fv = false
                                        is_productdata.favorited = false
                                    } else {
                                        is_fv = true
                                        is_productdata.favorited = true
                                    }
                                },
                            alignment = Alignment.Center
                        )


                        Spacer(modifier = Modifier.padding(horizontal = 8.sdp))


                        if (!isfromfv) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_feedback),
                                contentDescription = "back",
                                modifier = Modifier
                                    .width(20.sdp)
                                    .height(20.sdp)
                                    .align(Alignment.CenterVertically)
                                    .clickable(
                                        interactionSource = NoRippleInteractionSource(),
                                        indication = null
                                    ) {
                                        scope.launch { sheetState.show() }
                                    }
                            )
                        }

                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .align(Alignment.CenterHorizontally)
                                .wrapContentHeight(),
                            text = (if (is_productdata!!.name == null) "Unknow product" else is_productdata!!.name)!!,
                            fontSize = 17.sp,
                            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.color_font),
                            lineHeight = 18.sp,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false,
                                ),
                            ),
                        )

                        HorizontalPager(
                            count = is_productdata!!.images.size + 1,
                            state = pagerState,
                            modifier = Modifier
                        ) { page ->


                            var image = if (is_productdata!!.images.size > page) {
                                if (is_productdata!!.images[page].imageFileHash.isNotEmpty()) is_productdata!!.images[page].imageFileHash
                                else is_productdata!!.images[page].url
                            } else
                                ""

                            val coroutineScope = rememberCoroutineScope()
                            var context = LocalContext.current


                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .height(200.sdp)
                                    .padding(16.sdp),
                                verticalArrangement = Arrangement.Center,
                                Alignment.CenterHorizontally

                            ) {

                                var imageis by remember { mutableStateOf("") }
                                if (image.isNotEmpty()) {
                                    if (image.isNotEmpty() && is_productdata!!.images[page].imageFileHash.isNotEmpty()) {

                                        LaunchedEffect(Unit) {
                                            coroutineScope.launch {
                                                try {
                                                    imageis =
                                                        supabase.storage.from("productimages")
                                                            .createSignedUrl(
                                                                path = image,
                                                                expiresIn = 1.hours
                                                            )
                                                } catch (e: Exception) {
                                                    imageis = ""
                                                }
                                            }
                                        }
                                    } else imageis = image


                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(imageis)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.ic_imageplase),
                                        contentDescription = "",
                                        contentScale = ContentScale.FillHeight,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.sdp)
                                    )


                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_uploadimage),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()

                                            .clickable(
                                                interactionSource = NoRippleInteractionSource(),
                                                indication = null
                                            ) {

                                                if (intent.hasExtra("iswithdata")) {
                                                    var feedbackdata = FeedbackData()
                                                    feedbackdata.rating = 0;
                                                    feedbackdata.note = "";
                                                    var resonlist = ArrayList<String>()
                                                    feedbackdata.reasons = resonlist
                                                    scope.launch {
                                                        uploadimage.show()
                                                    }
                                                } else {

                                                    if (intent.hasExtra("Imagedata")) {
                                                        val resultIntent = Intent().apply {
                                                            putExtra("result_data", "goforimage")
                                                        }
                                                        (context as Activity).setResult(
                                                            Activity.RESULT_OK,
                                                            resultIntent
                                                        )
                                                        (context as Activity).finish()
                                                    } else {
                                                        var feedbackdata = FeedbackData()
                                                        feedbackdata.rating = 0;
                                                        feedbackdata.note = "";
                                                        var resonlist = ArrayList<String>()
                                                        feedbackdata.reasons = resonlist
                                                        scope.launch {
                                                            uploadimage.show()
                                                        }
                                                    }
                                                }
                                                /*  val intent =
                                                      Intent(context, FeedBackCaptureActivity::class.java)
                                                          .putExtra("Feedbackdata", Gson().toJson(feedbackdata))
                                                          .putExtra("clientid", clientActivityId)
                                                  context.startActivity(intent)*/
                                            }
                                            .height(60.sdp)
                                    )

                                    Spacer(modifier = Modifier.height(20.sdp))

                                    Text(
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .padding(5.sdp)
                                            .wrapContentHeight(),
                                        text = "Upload image",
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
                        }

                        IndicatorDots(
                            pageCount = is_productdata!!.images.size + 1, // Change pageCount as per your ViewPager pages
                            currentPage = pagerState.currentPage
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    color = colorResource(id = if (analze_response != null && analze_response!!.size > 0) R.color.color_unselectred else R.color.color_unselectback),
                                    shape = MaterialTheme.shapes.small
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        )
                        {

                            Image(
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(horizontal = 3.sdp, vertical = 10.sdp)
                                    .align(Alignment.CenterVertically),
                                painter = painterResource(id = if (analze_response != null && analze_response!!.size > 0) R.drawable.ic_unmatch else R.drawable.ic_match),
                                contentDescription = "Capture",
                                alignment = Alignment.Center
                            )
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .wrapContentHeight(),
                                text = if (analze_response != null && analze_response!!.size > 0) "Unmatched" else "Matched",
                                fontSize = 17.sp,
                                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = if (analze_response != null && analze_response!!.size > 0) R.color.color_red else R.color.color_appgreen),
                                lineHeight = 18.sp,
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
                                .padding(10.sdp)
                                .wrapContentHeight(),
                            text = "Ingredients",
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.sfpro_bold)),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.color_font),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false,
                                ),
                            ),
                        )

                        if (analze_response != null && analze_response!!.size > 0) {
                            ShowHighlightedText(
                                fullText = textis,
                                analayzedata = analze_response!!
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(10.sdp)
                                    .wrapContentHeight(),
                                text = textis,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Start,
                                color = colorResource(id = R.color.color_font),
                                lineHeight = 19.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false,
                                    ),
                                ),
                            )
                        }
                    }


                }
            } else {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    Arrangement.Center

                ) {

                    Spacer(modifier = Modifier.padding(vertical = 10.sdp))

                    Row(
                        Modifier
                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {
                                (context as? Activity)?.finish()
                            }
                            .fillMaxWidth(),
                        Arrangement.Center,
                        Alignment.CenterVertically
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
                                .align(Alignment.CenterVertically)
                                .wrapContentHeight(),
                            text = "Back",
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

                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = if (fali_apire) "Congratulations!" else "",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.black),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false,
                                ),
                            ),
                        )

                        Spacer(modifier = Modifier.width(50.sdp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            Arrangement.Center,
                            Alignment.CenterHorizontally,
                        ) {

                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .align(Alignment.CenterHorizontally)
                                    .wrapContentHeight(),
                                text = stringResource(R.string.no_datapro),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = R.color.color_font),
                                lineHeight = 16.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false,
                                    ),
                                ),
                            )

                            Spacer(modifier = Modifier.padding(vertical = 20.sdp))

                            Column(
                                modifier = Modifier
                                    .size(170.sdp)
                                    .align(Alignment.CenterHorizontally)
                                    .background(
                                        color = colorResource(
                                            id = R.color.color_unselectback
                                        )
                                    )
                                    .clickable(
                                        interactionSource = NoRippleInteractionSource(),
                                        indication = null
                                    ) {

                                        iscloseresult("byimage")
                                        /* val resultIntent = Intent().apply {
                                             putExtra("result_key", "Your Result Data")
                                         }
                                         setResult(Activity.RESULT_OK, resultIntent)
                                         finish()
                                         (context as Activity).finish()*/
                                    }
                                    .padding(15.sdp)
                                    .wrapContentWidth(),
                                Arrangement.Center,
                                Alignment.CenterHorizontally

                            )
                            {

                                Image(
                                    painter = painterResource(id = R.drawable.ic_uploadimage),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(40.sdp),
                                    Alignment.Center
                                )

                                Spacer(
                                    modifier = Modifier
                                        .padding(vertical = 10.sdp)
                                )

                                Text(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .padding(5.sdp)
                                        .wrapContentHeight(),
                                    text = "Upload image",
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

                            Spacer(modifier = Modifier.padding(vertical = 20.sdp))

                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .align(Alignment.CenterHorizontally)
                                    .wrapContentHeight(),
                                text = "Product will be analyzed instantly!",
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = R.color.color_font),
                                lineHeight = 16.sp,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false,
                                    ),
                                ),
                            )

                        }
                    }
                }
            }
        }


        var isChecked by remember { mutableStateOf(false) }
        var isChecked_2 by remember { mutableStateOf(false) }
        var isChecked_3 by remember { mutableStateOf(false) }
        var text by remember { mutableStateOf("") }

        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val bottomSheetHeight = screenHeight * 0.95f


        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                // Your bottom sheet content goes here
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(bottomSheetHeight)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                    {

                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(5.sdp)
                                .wrapContentHeight()
                                .clickable(
                                    interactionSource = NoRippleInteractionSource(),
                                    indication = null
                                ) {
                                    scope.launch { sheetState.hide() }
                                },
                            text = "Cancel",
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

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "Help me improve \uD83E\uDD79",
                            fontSize = 15.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(5.sdp)
                                .wrapContentHeight()

                                .clickable(
                                    interactionSource = NoRippleInteractionSource(),
                                    indication = null
                                ) {
                                    var feedbackdata = FeedbackData()
                                    feedbackdata.rating = 0;
                                    feedbackdata.note = text;
                                    var resonlist = ArrayList<String>()
                                    if (isChecked) resonlist.add("Product images.")
                                    if (isChecked_2) resonlist.add("Product information.")
                                    if (isChecked_3) resonlist.add("Incorrect Analysis")
                                    feedbackdata.reasons = resonlist
                                    val intent =
                                        Intent(context, FeedBackCaptureActivity::class.java)
                                            .putExtra("Feedbackdata", Gson().toJson(feedbackdata))
                                            .putExtra(
                                                "clientid",
                                                if (is_productdata!!.client_activity_id.isEmpty()) is_productdata!!.list_item_id
                                                else is_productdata!!.client_activity_id
                                            )
                                    launcher.launch(intent)
                                    // context.startActivity(intent)
                                    scope.launch { sheetState.hide() }
                                },
                            text = "Next",
                            fontSize = 15.sp,
                            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                            textAlign = TextAlign.Center,
                            color = colorResource(id = R.color.color_appgreen),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false,
                                ),
                            )
                        )


                    }

                    Spacer(modifier = Modifier.padding(10.sdp))

                    Text(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(5.sdp)
                            .align(Alignment.CenterHorizontally)
                            .wrapContentHeight(),
                        text = "What should i lok into?",
                        fontSize = 15.sp,
                        fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                        textAlign = TextAlign.Center,
                        color = colorResource(id = R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.padding(15.sdp))

                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                        )
                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentHeight(),
                            text = "Product images.",
                            fontSize = 15.sp,
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


                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Checkbox(
                            checked = isChecked_2,
                            onCheckedChange = { isChecked_2 = it },
                        )
                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentHeight(),
                            text = "Product information.",
                            fontSize = 15.sp,
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


                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {

                        Checkbox(
                            checked = isChecked_3,
                            onCheckedChange = { isChecked_3 = it },
                        )
                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentHeight(),
                            text = "Incorrect Analysis",
                            fontSize = 15.sp,
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

                    val keyboardController = LocalSoftwareKeyboardController.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.sdp)
                            .padding(12.dp)
                            .border(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    1.dp,
                                    colorResource(id = R.color.color_lightfont)
                                )
                            )
                    )
                    {

                        BasicTextField(
                            value = text,
                            onValueChange = {
                                text = it
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = colorResource(id = R.color.color_font),
                                fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            singleLine = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(8.sdp)
                                .onFocusChanged { focusState ->
                                },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Text
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                }
                            )
                        )
                        if (text.isEmpty()) {
                            Text(
                                text = "Optional, leave me a note here.",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = colorResource(id = R.color.color_lightfont),
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    )
                                ),
                                modifier = Modifier
                                    .padding(8.sdp)
                            )
                        }


                    }

                    Spacer(modifier = Modifier.weight(1f))


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

        var context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        var hasCameraPermission by remember { mutableStateOf(false) }
        var selectimagepath by remember { mutableStateOf("") }
        var selectimages by remember { mutableStateOf(ArrayList<ProductImage>()) }
        var viewmode: MyViewmodel = viewModel()
        var loadimage by remember { mutableStateOf(false) }

        ModalBottomSheetLayout(
            sheetState = uploadimage,
            sheetContent = {
                // Your bottom sheet content goes here
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.padding(20.dp))

                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()

                    )
                    {

                        Row(modifier = Modifier
                            .wrapContentWidth()

                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {
                                if (selectimages.size > 0) {
                                    selectimagepath = ""
                                    var filesToDelete: MutableList<String> = mutableListOf()
                                    selectimages.forEach {
                                        filesToDelete.add(it.uploadTask!!)
                                    }
                                    selectimages.clear()

                                    CoroutineScope(Dispatchers.IO).launch {

                                        var result = supabase.storage
                                            .from("productimages")
                                            .delete(
                                                paths = filesToDelete
                                            )
                                        filesToDelete.clear()
                                        scope.launch {
                                            uploadimage.hide()
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        uploadimage.hide()
                                    }
                                }
                            }) {

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

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(5.sdp)
                                .wrapContentHeight(),
                            text = "Add Photo",
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

                        Spacer(modifier = Modifier.weight(1f))

                        if (selectimagepath.isNotEmpty()) {
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(5.sdp)
                                    .wrapContentHeight()

                                    .clickable(
                                        interactionSource = NoRippleInteractionSource(),
                                        indication = null
                                    ) {

                                        var clientis = is_productdata.client_activity_id
                                        if (selectimages.size > 0) {
                                            selectimagepath = ""
                                            val productImagesDTO =
                                                selectimages.map { productImage ->
                                                    val imageFileHash = productImage.uploadTask
                                                    val imageOCRText = productImage.ocrTask
                                                    val barcode = productImage.barcodeDetectionTask
                                                    ImageInfo(imageFileHash, imageOCRText, barcode)
                                                }

                                            var feedbackdata = FeedbackData()

                                            feedbackdata.images =
                                                productImagesDTO as ArrayList<ImageInfo>
                                            val feedbackdatast = Gson().toJson(feedbackdata)
                                            viewmode.feedback(context, feedbackdatast, clientis)
                                            scope.launch {
                                                uploadimage.hide()
                                            }
                                            //  (context as Activity).finish()
                                        } else {
                                            showToast(context, "Please upload image")
                                        }
                                    },
                                text = "Done",
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                                textAlign = TextAlign.Center,
                                color = colorResource(id = R.color.color_appgreen),
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false,
                                    ),
                                )
                            )
                        } else {
                            Spacer(modifier = Modifier.width(50.sdp))
                        }
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    Box(modifier = Modifier.weight(1f))
                    {


                        val requestPermissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) { isGranted: Boolean ->
                            hasCameraPermission = isGranted
                        }

                        val imageCapture: ImageCapture = remember {
                            ImageCapture.Builder()
                                .setJpegQuality(100)
                                .setTargetResolution(Size(500, 700))
                                .build()
                        }
                        val executor: ExecutorService =
                            remember { Executors.newSingleThreadExecutor() }

                        LaunchedEffect(Unit) {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) -> {
                                    hasCameraPermission = true
                                }

                                else -> {
                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        }

                        if (hasCameraPermission) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                Arrangement.Center
                            ) {
                                val display = LocalConfiguration.current.orientation
                                AndroidView(
                                    factory = { AndroidViewContext ->
                                        val previewView =
                                            PreviewView(AndroidViewContext).apply {
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT
                                                )
                                            }

                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder()
                                            .build()
                                            .also {
                                                it.setSurfaceProvider(previewView.surfaceProvider)
                                            }

                                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                        try {
                                            cameraProvider.unbindAll()
                                            cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                cameraSelector,
                                                preview,
                                                imageCapture
                                            )
                                        } catch (exc: Exception) {
                                            exc.printStackTrace()
                                        }

                                        previewView
                                    }, modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.sdp)
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.sdp))
                                        .border(
                                            shape = RoundedCornerShape(10.sdp),
                                            border = BorderStroke(
                                                1.dp,
                                                colorResource(id = R.color.color_lightfont)
                                            )
                                        )
                                    // Set the height here
                                )

                                Spacer(modifier = Modifier.padding(10.dp))

                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Take photo of an ingredient label.",
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.padding(10.dp))


                                Row(
                                    Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                )
                                {

                                    if (selectimagepath.isNotEmpty()) {

                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(selectimagepath)
                                                .crossfade(true)
                                                .build(),
                                            placeholder = painterResource(R.drawable.ic_imageplase),
                                            contentDescription = "",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .width(100.sdp)
                                                .padding(start = 10.sdp)
                                                .aspectRatio(1f)
                                                .align(Alignment.CenterVertically),
                                            alignment = Alignment.Center
                                        )


                                    } else if (loadimage) {
                                        Box(
                                            modifier = Modifier
                                                .size(100.sdp)
                                                .align(Alignment.CenterVertically),
                                            Alignment.Center
                                        )
                                        {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.sdp),
                                                color = colorResource(id = R.color.color_appgreen)
                                            )
                                        }
                                    } else {
                                        Spacer(
                                            modifier = Modifier
                                                .width(100.sdp)
                                                .padding(end = 10.sdp)
                                                .aspectRatio(1f)
                                        )
                                    }

                                    Image(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .align(Alignment.CenterVertically)
                                            .weight(1f)
                                            .clickable(
                                                interactionSource = NoRippleInteractionSource(),
                                                indication = null
                                            ) {
                                                loadimage = true
                                                selectimagepath = ""
                                                val file = File(
                                                    context.externalMediaDirs.firstOrNull(),
                                                    "pic" + System
                                                        .currentTimeMillis()
                                                        .toString() + "pic.jpg"
                                                )

                                                var matadeta =
                                                    androidx.camera.core.ImageCapture.Metadata()
                                                matadeta.isReversedHorizontal = false
                                                matadeta.isReversedVertical = false
                                                val outputOptions = ImageCapture.OutputFileOptions
                                                    .Builder(file)
                                                    .setMetadata(matadeta)
                                                    .build()
                                                imageCapture.takePicture(outputOptions, executor,
                                                    object : ImageCapture.OnImageSavedCallback {


                                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                                                            /* val savedUri = outputFileResults.savedUri
                                                             val exif = ExifInterface(savedUri!!.path!!)
                                                             val orientation = exif.getAttributeInt(
                                                                 ExifInterface.TAG_ORIENTATION,
                                                                 ExifInterface.ORIENTATION_NORMAL
                                                             )*/

                                                            MediaScannerConnection.scanFile(
                                                                context,
                                                                arrayOf<String>(file.absolutePath),
                                                                null,
                                                                MediaScannerConnection.OnScanCompletedListener { path, uri ->


                                                                    val bitmap =
                                                                        getBitmapFromUri(
                                                                            context,
                                                                            uri
                                                                        )

                                                                    /* val bitmap =
                                                                         getBitmapFromOutputFileResults(
                                                                             outputFileResults,
                                                                             context.contentResolver
                                                                         )*/
                                                                    if (bitmap != null) {
                                                                        var product_image =
                                                                            ProductImage()

                                                                        val outputStream =
                                                                            java.io.ByteArrayOutputStream()
                                                                        bitmap!!.compress(
                                                                            Bitmap.CompressFormat.JPEG,
                                                                            100,
                                                                            outputStream
                                                                        )
                                                                        val imageData =
                                                                            outputStream.toByteArray()

                                                                        val digest =
                                                                            MessageDigest.getInstance(
                                                                                "SHA-256"
                                                                            )
                                                                        val hash =
                                                                            digest.digest(imageData)
                                                                        val imageFileName =
                                                                            hash.joinToString("") {
                                                                                "%02x".format(it)
                                                                            }


                                                                        CoroutineScope(Dispatchers.IO).launch {

                                                                            var result =
                                                                                supabase.storage
                                                                                    .from("productimages")
                                                                                    .upload(
                                                                                        path = imageFileName,
                                                                                        data = imageData,
                                                                                        upsert = true
                                                                                    )

                                                                            product_image.uploadTask =
                                                                                imageFileName
                                                                            product_image.image =
                                                                                bitmap

                                                                            product_image.barcodeDetectionTask =
                                                                                startBarcodeDetectionTask(
                                                                                    bitmap
                                                                                )
                                                                            processImage(
                                                                                outputFileResults.savedUri!!,
                                                                                context
                                                                            ) { text ->

                                                                                product_image.ocrTask =
                                                                                    text
                                                                                selectimages.add(
                                                                                    product_image
                                                                                )
                                                                                loadimage = false
                                                                                selectimagepath =
                                                                                    path
                                                                            }
                                                                        }
                                                                    } else {
                                                                        loadimage = false
                                                                    }
                                                                })
                                                        }

                                                        override fun onError(exception: ImageCaptureException) {
                                                        }
                                                    })
                                            },
                                        painter = painterResource(id = R.drawable.ic_capture),
                                        contentDescription = "Capture",
                                        alignment = Alignment.Center
                                    )

                                    Spacer(
                                        modifier = Modifier
                                            .width(100.sdp)
                                            .padding(end = 10.sdp)
                                            .aspectRatio(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.padding(10.dp))
                            }
                        }
                    }


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

    } else {

        Column(
            modifier = Modifier
                .padding(20.sdp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = if (is_imagedata) "Analyzing Image..." else "Looking up ${_basedata}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(60.dp))

            CircularProgressIndicator()
        }
    }



    if (ismessagetime) {
        scope.launch {
            delay(2000)
            ismessagetime = false
        }
    }
}


@Composable
fun ShowHighlightedText(
    fullText: String,
    analayzedata: AnalyzeResponce
) {

    var higlitestring = ArrayList<String>()
    analayzedata.forEach {
        higlitestring.add(it.ingredientName)
    }

    var showDialog by remember { mutableStateOf(false) }
    var clickedWord by remember { mutableStateOf("") }

    HighlightTextWithClickableWords(
        fullText = fullText,
        wordsToHighlight = higlitestring,
    ) { word ->
        for (data in analayzedata) {
            if (data.ingredientName.contains(word)) {
                clickedWord = data.preference
            }
        }
        showDialog = true
    }

    if (showDialog) {
        MessageDialog(
            word = clickedWord,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun MessageDialog(word: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        text = {
            Text("$word")
        }
    )
}

@Composable
fun HighlightTextWithClickableWords(
    fullText: String,
    wordsToHighlight: List<String>,
    onWordClick: (String) -> Unit
) {
    val context = LocalContext.current


    val annotatedString = buildAnnotatedString {
        var startIndex = 0
        var currentIndex: Int
        while (startIndex < fullText.length) {
            // Find the next word to highlight
            val matchResult = wordsToHighlight.mapNotNull { word ->
                val index = fullText.indexOf(word, startIndex, ignoreCase = true)
                if (index != -1) Pair(index, word) else null
            }.minByOrNull { it.first }

            if (matchResult == null) {
                // No more words to highlight, append the rest of the text
                append(fullText.substring(startIndex))
                break
            }

            currentIndex = matchResult.first
            val word = matchResult.second

            // Append the text before the highlight
            append(fullText.substring(startIndex, currentIndex))

            // Annotate the highlighted word
            pushStringAnnotation(tag = "highlight", annotation = word)
            withStyle(
                style = SpanStyle(
                    color = colorResource(id = R.color.color_red),
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(word)
            }
            pop()
            startIndex = currentIndex + word.length
        }
    }

    androidx.compose.foundation.text.ClickableText(
        text = annotatedString,

        style = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            lineHeight = 19.sp,
            color = colorResource(id = R.color.color_font),

            ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "highlight", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onWordClick(annotation.item)
                }
        },
        modifier = Modifier.padding(horizontal = 5.sdp)
    )
}







