package com.android.ingredicheck.Activity

import android.Manifest
import android.app.Activity
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
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.DataClass.FeedbackData
import com.android.ingredicheck.DataClass.ImageInfo
import com.android.ingredicheck.DataClass.ProductImage
import com.android.ingredicheck.R
import com.android.ingredicheck.ViewModel.MyViewmodel
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.Views.showToast
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import com.google.gson.Gson
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FeedBackCaptureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {


                    var feedbackdata = Gson().fromJson(
                        intent.getStringExtra("Feedbackdata"),
                        FeedbackData::class.java
                    )
                    Image_capture(
                        SupabaseHelp().get_supaclient(this@FeedBackCaptureActivity)!!,
                        feedbackdata,
                        intent.getStringExtra("clientid")
                    )
                }
            }
        }
    }
}

@Composable
fun Image_capture(supabase: SupabaseClient, feedbackdata: FeedbackData, client_id: String?) {

    var context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    var selectimagepath by remember { mutableStateOf("") }
    var selectimages by remember { mutableStateOf(ArrayList<ProductImage>()) }
    var viewmode: MyViewmodel = viewModel()
    var loadimage by remember { mutableStateOf(false) }

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
                            (context as? Activity)?.finish()
                        }
                    } else {
                        (context as? Activity)?.finish()
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
                            if (selectimages.size > 0) {
                                selectimagepath = ""
                                val productImagesDTO = selectimages.map { productImage ->
                                    val imageFileHash = productImage.uploadTask
                                    val imageOCRText = productImage.ocrTask
                                    val barcode = productImage.barcodeDetectionTask
                                    ImageInfo(imageFileHash, imageOCRText, barcode)
                                }
                                feedbackdata.images = productImagesDTO as ArrayList<ImageInfo>
                                val feedbackdatast = Gson().toJson(feedbackdata)
                                viewmode.feedback(context, feedbackdatast, client_id)
                                (context as Activity).finish()
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
            }else
            {
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
            val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

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
                                androidx.camera.view.PreviewView(AndroidViewContext).apply {
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

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner, cameraSelector, preview, imageCapture
                                )
                            } catch (exc: Exception) {
                                exc.printStackTrace()
                            }

                            previewView
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.sdp)
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
                        }else {
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
                                                            getBitmapFromUri(context, uri)

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
                                                                MessageDigest.getInstance("SHA-256")
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
                                                                product_image.image = bitmap

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
                                                                    selectimagepath = path
                                                                }
                                                            }
                                                        }else
                                                        {
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

}
