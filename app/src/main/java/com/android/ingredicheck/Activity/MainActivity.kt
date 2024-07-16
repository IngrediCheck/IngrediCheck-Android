package com.android.ingredicheck.Activity


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Switch
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.ingredicheck.BaseClass.Constant
import com.android.ingredicheck.Constant.SharePrefrence
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.DataClass.ImageInfo
import com.android.ingredicheck.DataClass.ProductImage
import com.android.ingredicheck.Fregment.HomeScreen_ui
import com.android.ingredicheck.Fregment.ListScreen
import com.android.ingredicheck.R
import com.android.ingredicheck.ViewModel.MyViewmodel
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {


                    var supabase = SupabaseHelp().get_supaclient(this@MainActivity)!!
                    MainScreen(supabase, intent)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(supabase: SupabaseClient, intent: Intent) {


    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration here
            easing = { it } // You can use different easing functions
        ))

    val settingsheet = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration here
            easing = { it } // You can use different easing functions
        ))

    val context = LocalContext.current
    var background = colorResource(id = R.color.color_unselect)
    var background_select = colorResource(id = R.color.color_unselectback)
    var background_gray = colorResource(id = R.color.color_menuback)
    var shape = CircleShape
    var is_home by remember { mutableStateOf(true) }
    val select_icon = colorResource(id = R.color.color_appgreen)
    val unselect_icon = colorResource(id = R.color.color_line)
    var _nowcan_show by remember { mutableStateOf(false) }
    val greyColor = colorResource(id = R.color.color_back)
    var isChecked by remember { mutableStateOf(false) }
    isChecked = SharePrefrence(context).get_openscan()
    var viewmode: MyViewmodel = viewModel()
    val pref_response by viewmode.is_delete.observeAsState(false)
    val deleteme: Boolean = pref_response ?: false
    val scope = rememberCoroutineScope()
    var nowCanShow by remember { mutableStateOf(false) }
    var selectimages by remember { mutableStateOf(ArrayList<ProductImage>()) }
    var _isphoto by remember { mutableStateOf(false) }
    var _ishaveimage by remember { mutableStateOf(false) }
    var do_clear by remember { mutableStateOf(false) }
    var textcolor_grey = colorResource(id = R.color.color_grey)

    val photoColor by animateColorAsState(targetValue = if (_isphoto) select_icon else textcolor_grey)
    val barcodeColor by animateColorAsState(targetValue = if (!_isphoto) select_icon else textcolor_grey)


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringExtra("result_key")
            val result_data = result.data?.getStringExtra("result_data")

            if (result_data != null) {
                _isphoto = true
                _ishaveimage = true
                do_clear = false
            } else {
                if (data != null) {
                    _isphoto = true
                    selectimages.clear()
                    _ishaveimage = false
                    nowCanShow = true
                    do_clear = true
                } else {
                    selectimages.clear()
                    nowCanShow = true
                    _ishaveimage = false
                    do_clear = true
                }
            }
        } else {
            selectimages.clear()
            _ishaveimage = false
            nowCanShow = true
            do_clear = true
        }
    }

    LaunchedEffect(nowCanShow) {
        if (nowCanShow) {
            Constant.isBarcodeDetected = false
            sheetState.show()
        }
    }

    if (deleteme) {
        SharePrefrence(context).clear_Pref()

        context.startActivity(Intent(context, LoginActivity::class.java))
        (context as Activity).finishAffinity()
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            _nowcan_show = false
        } else if (sheetState.currentValue == ModalBottomSheetValue.Expanded || sheetState.currentValue == ModalBottomSheetValue.HalfExpanded) {
            _nowcan_show = true
        }
    }


    val logoutState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        animationSpec = tween(
            durationMillis = 300, // Adjust the duration here
            easing = { it } // You can use different easing functions
        )
    )


    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(15.sdp))

        Box(modifier = Modifier.weight(1f))
        {
            if (is_home) {
                HomeScreen_ui() {
                    scope.launch { settingsheet.show() }
                }
            } else {
                ListScreen()
            }
        }

        Column(
            Modifier
                .wrapContentHeight()
                .wrapContentWidth()
        ) {
            val greyColor = colorResource(id = R.color.color_line)
            DrawHorizontalLine(greyColor)

            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(background_gray)
            )
            {
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(10.sdp)

                ) {

                    Spacer(modifier = Modifier.padding(horizontal = 10.sdp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = " image1",
                        Modifier
                            .height(40.sdp)
                            .width(40.sdp)
                            .background(
                                color = if (is_home) background_select else background,
                                shape
                            )
                            .padding(10.sdp)
                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {
                                if (!is_home) is_home = true
                            },
                        colorFilter = ColorFilter.tint(if (is_home) select_icon else unselect_icon),
                        alignment = Alignment.CenterStart
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(id = R.drawable.ic_barcode),
                        contentDescription = " image2",
                        modifier = Modifier
                            .height(45.sdp)
                            .width(45.sdp)
                            .background(select_icon, shape)
                            .padding(10.sdp)
                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {


                                scope.launch {
                                    _nowcan_show = true
                                    Constant.isBarcodeDetected = false
                                    sheetState.show()
                                }

                                /*  context.startActivity(
                                      Intent(
                                          context,
                                          ImageOrBarcodeActivity::class.java
                                      )
                                  )*/
                            },
                        alignment = Alignment.Center
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.ic_list),
                        contentDescription = " image3",
                        Modifier
                            .height(40.sdp)
                            .width(40.sdp)
                            .background(
                                color = if (!is_home) background_select else background,
                                shape
                            )
                            .padding(10.sdp)
                            .clickable(
                                interactionSource = NoRippleInteractionSource(),
                                indication = null
                            ) {
                                if (is_home) is_home = false
                            },
                        colorFilter = ColorFilter.tint(if (!is_home) select_icon else unselect_icon),
                        alignment = Alignment.CenterEnd
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 10.sdp))
                }
            }
        }
    }

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
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxHeight()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                    {
                        if (_isphoto && _ishaveimage) {
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(5.sdp)
                                    .wrapContentHeight()

                                    .clickable(
                                        interactionSource = NoRippleInteractionSource(),
                                        indication = null
                                    ) {
                                        do_clear = true
                                        if (selectimages.size > 0) {
                                            var filesToDelete: MutableList<String> = mutableListOf()
                                            selectimages.forEach {
                                                filesToDelete.add(it.uploadTask!!)
                                            }

                                            CoroutineScope(Dispatchers.IO).launch {

                                                var result = supabase.storage
                                                    .from("productimages")
                                                    .delete(
                                                        paths = filesToDelete
                                                    )
                                                filesToDelete.clear()

                                            }
                                        }
                                    },
                                text = "Clear",
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

                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .background(
                                    shape = RoundedCornerShape(8.dp),
                                    color = textcolor_grey
                                ), horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 3.dp, horizontal = 4.dp)
                                    .background(
                                        shape = RoundedCornerShape(6.dp),
                                        color = barcodeColor
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        _isphoto = false
                                    }
                            ) {
                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                                    text = "Barcode",
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false,
                                        ),
                                    ),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .padding(vertical = 3.dp, horizontal = 6.dp)
                                    .background(
                                        shape = RoundedCornerShape(6.dp),
                                        color = photoColor
                                    )

                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        _isphoto = true
                                    }
                            ) {

                                Text(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp),
                                    text = "Photo",
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false,
                                        ),
                                    ),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))


                        if (_isphoto && _ishaveimage) {
                            Text(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(5.sdp)
                                    .wrapContentHeight()
                                    .clickable(
                                        interactionSource = NoRippleInteractionSource(),
                                        indication = null
                                    ) {

                                        val productImagesDTO = selectimages.map { productImage ->
                                            val imageFileHash = productImage.uploadTask
                                            val imageOCRText = productImage.ocrTask
                                            val barcode = productImage.barcodeDetectionTask
                                            ImageInfo(imageFileHash, imageOCRText, barcode)
                                        }

                                        val productImagesJsonString =
                                            Gson().toJson(productImagesDTO)

                                        val intent = Intent(
                                            context,
                                            GetProductAcrivity::class.java
                                        ).putExtra(
                                            "Imagedata", productImagesJsonString
                                        )
                                        launcher.launch(intent)
                                        nowCanShow = false
                                        // context.startActivity(intent)
                                        scope.launch {
                                         //   _nowcan_show = false
                                        //    sheetState.hide()
                                            Constant.isBarcodeDetected = false
                                        }
                                        // (context as? Activity)!!.finish()
                                    },
                                text = "Check",
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
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    Box(modifier = Modifier.weight(1f)) {

                        is_emaptyview()

                        if (_nowcan_show) {
                            when (_isphoto) {
                                false -> {
                                    BarcodeScannerView(launcher) { is_close ->
                                        scope.launch {
                                            _nowcan_show = false
                                            nowCanShow = false
                                            sheetState.hide()
                                            Constant.isBarcodeDetected = false
                                        }
                                    }
                                }

                                true -> {

                                    CameraCaptureView(
                                        context,
                                        supabase,
                                        do_clear
                                    ) { image_data, loderstatus ->
                                        Log.e(
                                            "capturedata",
                                            "dataimage_data  --= " + Gson().toJson(image_data)
                                        )
                                        if (image_data != null) {
                                            selectimages.add(image_data)
                                            _ishaveimage = true
                                        }
                                    }
                                    do_clear = false
                                    /* val requestPermissionLauncher =
                                         rememberLauncherForActivityResult(
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

                                                     val cameraSelector =
                                                         CameraSelector.DEFAULT_BACK_CAMERA

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
                                                     .padding(5.sdp)
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

                                             Column(
                                                 modifier = Modifier.height(135.sdp),
                                                 Arrangement.Center,
                                                 Alignment.CenterHorizontally
                                             ) {

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
                                                             model = ImageRequest.Builder(
                                                                 LocalContext.current
                                                             )
                                                                 .data(selectimagepath)
                                                                 // .data(save_bitmap)
                                                                 .crossfade(true)
                                                                 .build(),
                                                             placeholder = painterResource(R.drawable.ic_imageplase),
                                                             contentDescription = "",
                                                             contentScale = ContentScale.FillWidth,
                                                             modifier = Modifier
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
                                                                 matadeta.isReversedHorizontal =
                                                                     false
                                                                 matadeta.isReversedVertical = false
                                                                 val outputOptions =
                                                                     ImageCapture.OutputFileOptions
                                                                         .Builder(file)
                                                                         .setMetadata(matadeta)
                                                                         .build()
                                                                 imageCapture.takePicture(
                                                                     outputOptions,
                                                                     executor,
                                                                     object :
                                                                         ImageCapture.OnImageSavedCallback {

                                                                         override fun onImageSaved(
                                                                             outputFileResults: ImageCapture.OutputFileResults
                                                                         ) {

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
                                                                                             digest.digest(
                                                                                                 imageData
                                                                                             )
                                                                                         val imageFileName =
                                                                                             hash.joinToString(
                                                                                                 ""
                                                                                             ) {
                                                                                                 "%02x".format(
                                                                                                     it
                                                                                                 )
                                                                                             }

                                                                                         CoroutineScope(
                                                                                             Dispatchers.IO
                                                                                         ).launch {

                                                                                             var result =
                                                                                                 supabase.storage
                                                                                                     .from(
                                                                                                         "productimages"
                                                                                                     )
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
                                                                                                 loadimage =
                                                                                                     false
                                                                                                 product_image.ocrTask =
                                                                                                     text
                                                                                                 selectimages.add(
                                                                                                     product_image
                                                                                                 )
                                                                                                 selectimagepath =
                                                                                                     path
                                                                                             }
                                                                                         }
                                                                                     } else {
                                                                                         loadimage =
                                                                                             false
                                                                                     }
                                                                                 })
                                                                         }

                                                                         override fun onError(
                                                                             exception: ImageCaptureException
                                                                         ) {
                                                                             loadimage = false
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
                                     }*/
                                }

                                else -> {
                                    CameraCaptureView(
                                        context,
                                        supabase,
                                        do_clear
                                    ) { image_data, loderstatus ->

                                        if (image_data != null) {
                                            selectimages.add(image_data)
                                            _ishaveimage = true
                                        }
                                    }
                                    do_clear = false
                                }
                            }
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




    ModalBottomSheetLayout(
        sheetState = settingsheet,
        sheetContent = {
            // Your bottom sheet content goes here
            Column(
                modifier = Modifier
                    .height(bottomSheetHeight)
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.color_back))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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
                                scope.launch { settingsheet.hide() }
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
                                    context.startActivity(
                                        Intent(
                                            context,
                                            LoginActivity::class.java
                                        )
                                    )
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
                                scope.launch { logoutState.show() }
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
                                val intent =
                                    Intent(context, WebviewPageActivity::class.java).putExtra(
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
                                val intent =
                                    Intent(context, WebviewPageActivity::class.java).putExtra(
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
                                val intent =
                                    Intent(context, WebviewPageActivity::class.java).putExtra(
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
                                val intent =
                                    Intent(context, WebviewPageActivity::class.java).putExtra(
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


    ModalBottomSheetLayout(
        sheetState = logoutState,
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
                            scope.launch { logoutState.hide() }
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

    LaunchedEffect(Unit) {
        if (intent.hasExtra("is_gatbydirect") && intent.getBooleanExtra(
                "is_gatbydirect",
                false
            )
        ) {
            scope.launch {
                _nowcan_show = true
                Constant.isBarcodeDetected = false
                sheetState.show()
            }
            // startActivity(Intent(this@MainActivity, ImageOrBarcodeActivity::class.java))
        }
    }

}

@Composable
fun DrawHorizontalLine(greyColor: Color) {

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.sdp)
    ) {
        drawLine(
            color = greyColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2f
        )
    }
}


suspend fun startBarcodeDetectionTask(image: Bitmap): String? {
    return withContext(Dispatchers.Default) {
        try {
            val inputImage = InputImage.fromBitmap(image, 0)
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,

                    )
                .build()
            val scanner = BarcodeScanning.getClient(options)

            val barcodes = scanner.process(inputImage).await()

            val barcode = barcodes.firstOrNull {
                it.format == Barcode.FORMAT_EAN_8 || it.format == Barcode.FORMAT_EAN_13 || it.format == Barcode.FORMAT_UPC_A || it.format == Barcode.FORMAT_UPC_E
            }
            barcode?.rawValue
        } catch (e: Exception) {
            ""
        }
    }
}

fun getBitmapFromUri(
    context: Context, uri: Uri
): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val orientation = getExifOrientation(context, uri)
        rotateBitmap(bitmap, orientation)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.preRotate(90f)
            matrix.preScale(-1f, 1f)
        }

        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.preRotate(270f)
            matrix.preScale(-1f, 1f)
        }

        else -> return bitmap
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun getExifOrientation(context: Context, uri: Uri): Int {
    val inputStream =
        context.contentResolver.openInputStream(uri) ?: return ExifInterface.ORIENTATION_UNDEFINED
    val exif = ExifInterface(inputStream)
    inputStream.close()
    return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
}


@Composable
fun is_emaptyview() {

    Column(
        modifier = Modifier.fillMaxSize(),
        Arrangement.Center
    ) {

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.sdp)
                .weight(1f)
                .clip(RoundedCornerShape(10.sdp))
                .background(color = colorResource(id = R.color.black))
                .border(
                    shape = RoundedCornerShape(10.sdp),
                    border = BorderStroke(
                        1.dp,
                        colorResource(id = R.color.color_lightfont)
                    )
                )
        )
        Column(
            modifier = Modifier.height(135.sdp),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally),
                text = "",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )
        }
    }
}

@Composable
fun CameraCaptureView(
    context: Context,
    supabase: SupabaseClient,
    do_clear: Boolean,
    isimage_data: (ProductImage?, Boolean) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    var loadimage by remember { mutableStateOf(false) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var selectimagepath by remember { mutableStateOf("") }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    if (do_clear) {
        selectimagepath = ""
        loadimage = false
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                hasCameraPermission = true
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    if (hasCameraPermission) {

        val requestPermissionLauncher =
            rememberLauncherForActivityResult(
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

                        val cameraSelector =
                            CameraSelector.DEFAULT_BACK_CAMERA

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
                        .padding(5.sdp)
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

                Column(
                    modifier = Modifier.height(135.sdp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
                ) {

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
                                model = ImageRequest.Builder(
                                    LocalContext.current
                                )
                                    .data(selectimagepath)
                                    // .data(save_bitmap)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_imageplase),
                                contentDescription = "",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
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
                                    isimage_data(null, true)
                                    selectimagepath = ""
                                    val file = File(
                                        context.externalMediaDirs.firstOrNull(),
                                        "pic" + System
                                            .currentTimeMillis()
                                            .toString() + "pic.jpg"
                                    )

                                    var matadeta =
                                        androidx.camera.core.ImageCapture.Metadata()
                                    matadeta.isReversedHorizontal =
                                        false
                                    matadeta.isReversedVertical = false
                                    val outputOptions =
                                        ImageCapture.OutputFileOptions
                                            .Builder(file)
                                            .setMetadata(matadeta)
                                            .build()
                                    imageCapture.takePicture(
                                        outputOptions,
                                        executor,
                                        object :
                                            ImageCapture.OnImageSavedCallback {

                                            override fun onImageSaved(
                                                outputFileResults: ImageCapture.OutputFileResults
                                            ) {

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
                                                                digest.digest(
                                                                    imageData
                                                                )
                                                            val imageFileName =
                                                                hash.joinToString(
                                                                    ""
                                                                ) {
                                                                    "%02x".format(
                                                                        it
                                                                    )
                                                                }

                                                            CoroutineScope(
                                                                Dispatchers.IO
                                                            ).launch {

                                                                var result =
                                                                    supabase.storage
                                                                        .from(
                                                                            "productimages"
                                                                        )
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
                                                                    /*selectimages.add(
                                                                        product_image
                                                                    )*/
                                                                    selectimagepath =
                                                                        path


                                                                    isimage_data(
                                                                        product_image,
                                                                        false
                                                                    )
                                                                }
                                                            }
                                                        } else {
                                                            isimage_data(null, false)
                                                            loadimage =
                                                                false
                                                        }
                                                    })
                                            }

                                            override fun onError(
                                                exception: ImageCaptureException
                                            ) {
                                                isimage_data(null, false)
                                                loadimage = false
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
}

fun processImage(url: Uri, context: Context, onTextRecognized: (String) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, url)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = visionText.text
                onTextRecognized(resultText)
            }
            .addOnFailureListener { e ->
                onTextRecognized("")
            }
    } catch (e: Exception) {
        onTextRecognized("")
    }

}

@Composable
fun BarcodeScannerView(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    isclose: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecyclele = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    var hasCameraPermission by remember { mutableStateOf(false) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
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
            AndroidView(
                factory = { AndroidViewContext ->
                    val previewView = androidx.camera.view.PreviewView(AndroidViewContext).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            /* it.setAnalyzer(
                                 Executors.newSingleThreadExecutor(),
                                 BarcodeAnalyzer(context, barcodeScanner)
                             )*/

                            it.setAnalyzer(
                                Executors.newSingleThreadExecutor(),
                                BarcodeAnalyzer(context, barcodeScanner, launcher) { close ->
                                    isclose(close)
                                    try {
                                        cameraProvider.unbindAll()
                                    } catch (exc: Exception) {
                                        exc.printStackTrace()
                                    }
                                }
                            )
                        }

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecyclele, cameraSelector, preview, imageAnalyzer
                        )
                    } catch (exc: Exception) {
                        exc.printStackTrace()
                    }

                    previewView
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.sdp)
                    .weight(1f)
                    .clip(RoundedCornerShape(10.sdp))
                    .border(
                        shape = RoundedCornerShape(10.sdp),
                        border = BorderStroke(
                            1.dp,
                            colorResource(id = R.color.color_lightfont)
                        )
                    )
            )

            Column(
                modifier = Modifier.height(135.sdp),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.CenterHorizontally),
                    text = "Scan barcode of a packaged food item.",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                )
            }


        }
    }
}


class BarcodeAnalyzer(
    val context: Context,
    private val scanner: BarcodeScanner,
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    val isclose: (Boolean) -> Unit
) :
    ImageAnalysis.Analyzer {


    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (!Constant.isBarcodeDetected) {
                        if (barcodes.size > 0) {
                            Constant.isBarcodeDetected = true
                            isclose(true)
                            val intent = Intent(context, GetProductAcrivity::class.java).putExtra(
                                "barcodedata", barcodes.get(0).rawValue
                            )
                            launcher.launch(intent)

                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("myscandopnedata", "addOnFailureListener is =-  ")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}


