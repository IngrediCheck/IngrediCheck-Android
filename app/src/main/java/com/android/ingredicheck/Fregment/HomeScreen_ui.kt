package com.android.ingredicheck.Fregment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.ingredicheck.Activity.DrawHorizontalLine
import com.android.ingredicheck.Activity.IndicatorDots
import com.android.ingredicheck.BaseClass.Constant
import com.android.ingredicheck.Constant.SharePrefrence
import com.android.ingredicheck.DataClass.PagerItem
import com.android.ingredicheck.R
import com.android.ingredicheck.ResponceModelClass.PreferencelistsModel.PreferencelistsModedataItem
import com.android.ingredicheck.ViewModel.MyViewmodel
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.Views.showToast
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen_ui(
    isclick_set: (String) -> Unit
) {

    var context = LocalContext.current
    var viewmode: MyViewmodel = viewModel()
    LaunchedEffect(Unit) {
        viewmode.fatchdata(context)
    }
    val pref_response by viewmode.prefhdata.observeAsState(ArrayList<PreferencelistsModedataItem>())
    var preferenceList: List<PreferencelistsModedataItem> = pref_response ?: emptyList()
    Constant.users_prefrance = ""

    for (prefdata in preferenceList) {
        if (prefdata.annotatedText != null && prefdata.annotatedText.isNotEmpty() && !prefdata.annotatedText.equals(
                "null"
            )
        ) {
            if (preferenceList.indexOf(prefdata) == 0) {
                Constant.users_prefrance = Constant.users_prefrance + prefdata.annotatedText
            } else {
                Constant.users_prefrance = Constant.users_prefrance + "\n" + prefdata.annotatedText
            }
        }
    }

    val loderrespo by viewmode.is_loder.observeAsState(0)
    val is_loderstatus: Int = loderrespo
    /*LaunchedEffect(Unit) {
        viewmode.fatchdata()
    }*/
    var app_greencolor = colorResource(id = R.color.color_appgreen)
    var app_red = colorResource(id = R.color.color_red)
    var textcolor = colorResource(id = R.color.color_font)
    var textcolor_grey = colorResource(id = R.color.color_grey)
    var text by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var select_id by remember { mutableStateOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isChecked by remember { mutableStateOf(false) }
    isChecked = SharePrefrence(context).get_openscan()
    var select_data by remember { mutableStateOf(-1) }
    val focusManager: FocusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        // Header Row

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(40.dp))

            Text(
                text = "Your Dietary Preferences",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f)
                    .fillMaxWidth(),
                fontSize = 17.sp,
                fontFamily = FontFamily(Font(R.font.sfpro_bold)),
                textAlign = TextAlign.Center,
            )

            Image(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "image",
                modifier = Modifier
                    .height(40.dp)
                    .width(40.dp)
                    .padding(8.dp)
                    .clickable(
                        interactionSource = NoRippleInteractionSource(),
                        indication = null
                    ) {
                        // Handle click action
                        isclick_set("")
                    },
                colorFilter = ColorFilter.tint(app_greencolor)
            )
        }

        // Text Entry Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isFocused) Color.White else textcolor_grey
                    )
                    .border(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            2.dp,
                            if (is_loderstatus == 2) app_red else if (isFocused) app_greencolor else Color.Transparent
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 10.dp)
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
                            .padding(vertical = 8.dp)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (isFocused) {
                                    viewmode.chnage_loader(0)
                                }
                            }
                            .focusRequester(focusRequester),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (text.isEmpty()) {
                                    showToast(context, "Please enter dietary Preferences")
                                    return@KeyboardActions
                                } else {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()

                                    if (select_id == 0) {
                                        viewmode.add_data(context, text)

                                    } else {
                                        viewmode.edit_data(context, text, select_id)
                                        select_id = 0
                                    }
                                }
                            }
                        ),
                    )
                    if (text.isEmpty() && !isFocused) {
                        Text(
                            text = "Enter dietary preference here",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterStart)
                        )
                    }
                }

                if (text.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            text = ""
                            viewmode.chnage_loader(0)
                            if(select_data != -1) select_data = -1
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear",
                        )
                    }
                }
            }

            // Text to show above keyboard
            /* if (isKeyboardOpen && text.isNotEmpty()) {

                 Text(
                     text = "Text to show above keyboard",
                     modifier = Modifier
                         .align(Alignment.TopCenter)
                         .wrapContentHeight()
                         .padding(8.dp)
                         .offset(y = -keyboardHeight),
                     style = TextStyle(
                         color = Color.Black,
                         fontSize = 14.sp,
                         fontWeight = FontWeight.Bold
                     )
                 )
             }*/

            LaunchedEffect(Unit) {
                if (isFocused) {
                    focusRequester.requestFocus()
                }
            }
        }


        // Loader or Error Message
        when (is_loderstatus) {

            1 -> Loader_think()
            2 -> Text(
                text = "This doesn't make sense. Please provide a dietary preference.",
                modifier = Modifier
                    .padding(start = 10.dp),
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                color = colorResource(id = R.color.color_red),
                fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false,
                    ),
                )
            )

            3 -> {
                text = ""
                viewmode.chnage_loader(0)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        var isRefreshing by remember { mutableStateOf(false) }

        // Swipe Refresh and List
        if (preferenceList.isNotEmpty()) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = {
                    isRefreshing = true
                    viewmode.fatchdata(context)
                    GlobalScope.launch {
                        delay(2000) // 2 seconds delay
                        isRefreshing = false
                    }
                }
            ) {
                LazyColumn {
                    items(preferenceList) { item ->

                        if (select_data != preferenceList.indexOf(item)) {
                            ItemComposable(item) { selectedItemId ->
                                if (selectedItemId == 1) {
                                    text = item.text
                                    select_id = item.id
                                    focusRequester.requestFocus()
                                    preferenceList.forEach {
                                        if (select_id == it.id) {
                                            select_data = preferenceList.indexOf(it)
                                        }
                                    }

                                } else if (selectedItemId == 2) {
                                    viewmode.deletedata(context, item.id)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Empty State UI
            val pagerItems = listOf(
                PagerItem(
                    0,
                    stringResource(R.string.text1), ""
                ),
                PagerItem(
                    0,
                    stringResource(R.string.text2), ""
                ),
                PagerItem(
                    0,
                    stringResource(R.string.text4), ""
                )
            )
            val pagerState = rememberPagerState(initialPage = 0)

            LaunchedEffect(key1 = pagerState) {
                while (true) {
                    yield()
                    delay(3000) // change slide interval as needed
                    pagerState.animateScrollToPage(
                        page = (pagerState.currentPage + 1) % pagerItems.size
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_emaptylist),
                    contentDescription = "empty view",
                    modifier = Modifier.size(120.dp)
                )

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = 20.dp),
                    text = "You don’t have any dietary\npreferences entered yet",
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_lightfont),
                    lineHeight = 18.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.height(45.sdp))

                Text(
                    modifier = Modifier
                        .wrapContentSize(),
                    text = "Try the following",
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.color_lightfont),
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false,
                        ),
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalPager(
                    count = pagerItems.size,
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.sdp)
                ) { page ->
                    emptyPagerItemView(pagerItems[page])
                }

                IndicatorDots(
                    pageCount = 3, // Change pageCount as per your ViewPager pages
                    currentPage = pagerState.currentPage
                )
            }

            Text(
                text = "No items to display",
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}


@Composable
fun emptyPagerItemView(item: PagerItem) {

    var app_greencolor = colorResource(id = R.color.color_unselectback)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                shape = RoundedCornerShape(8.dp),
                color = app_greencolor
            )
            .padding(16.sdp),
        Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {


        StyledText(text = item.title, true)
        /*Text(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentHeight(),
            text = item.title,
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_medium)),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.color_font),
            lineHeight = 17.sp
        )*/


    }
}

@Composable
fun Loader_think() {
    var color = colorResource(id = R.color.color_appgreen)
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.sdp)
    ) {

        Row(Modifier.wrapContentWidth()) {
            CircularProgressIndicator(modifier = Modifier.size(20.sdp), color = color)
            Text(
                text = "Thinking...",
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 10.sdp)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemComposable(item: PreferencelistsModedataItem, onMenuItemClick: (Int) -> Unit) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val greyColor = colorResource(id = R.color.color_back)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { isMenuOpen = true },
                onLongClick = {
                    isMenuOpen = true
                }
            )
    ) {
        if (item != null && item.text != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(10.sdp)
                    .combinedClickable(
                        onClick = { },
                        onLongClick = { isMenuOpen = true }
                    )
            ) {
                DrawCircleExample(true)
                Column(modifier = Modifier.padding(horizontal = 15.sdp)) {
                    StyledText(
                        text = item.annotatedText ?: "No preference available",
                        iscenter = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DrawHorizontalLine(greyColor)
                }
            }
        }
    }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if (isMenuOpen) {
        val menuOptions = listOf("Copy", "Edit", "Delete")
        Box {
            PopupMenu(
                menuOptions = menuOptions,
                onDismissRequest = { isMenuOpen = false },
                onMenuItemClick = { optionIndex ->

                    if (optionIndex == 0) {
                        clipboardManager.setText(AnnotatedString(item.text))
                    } else if (optionIndex == 1) {
                        onMenuItemClick(1)
                    } else if (optionIndex == 2) {
                        onMenuItemClick(2)
                    }
                    isMenuOpen = false
                }
            )
        }
    }
}

@Composable
fun PopupMenu(
    menuOptions: List<String>,
    onDismissRequest: () -> Unit,
    onMenuItemClick: (Int) -> Unit
) {


    DropdownMenu(
        expanded = true,
        modifier = Modifier.background(
            color = colorResource(id = R.color.white),
            shape = RoundedCornerShape(8.dp)
        ),
        onDismissRequest = { onDismissRequest() }
    ) {
        menuOptions.forEachIndexed { index, option ->
            DropdownMenuItem(
                onClick = { onMenuItemClick(index) },

                ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = NoRippleInteractionSource(),
                            indication = null
                        ) {
                            onMenuItemClick(index)
                        }

                )
                {

                    Text(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(80.sdp)
                            .align(Alignment.CenterVertically),
                        text = option,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                        textAlign = TextAlign.Start,
                        color = colorResource(id = if (index == 2) R.color.color_red else R.color.color_font),
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false,
                            ),
                        ),
                    )

                    Spacer(modifier = Modifier.width(30.sdp))

                    Image(
                        painter = painterResource(
                            id = if (index == 0) R.drawable.ic_copytext
                            else if (index == 1) R.drawable.ic_edit
                            else R.drawable.ic_delete
                        ),
                        contentDescription = "",
                        modifier = Modifier
                            .width(20.sdp)
                            .height(20.sdp)
                            .align(Alignment.CenterVertically)
                    )


                }
            }
        }
    }
}

@Composable
fun DrawCircleExample(chnage: Boolean) {
    var color = if (chnage) colorResource(id = R.color.color_appgreen)
    else colorResource(id = R.color.color_red)

    Canvas(
        modifier = Modifier
            .width(10.sdp)
            .padding(end = 15.sdp)
    ) {
        // Draw a circle with center at (x = 200f, y = 200f) and radius 100f
        drawCircle(
            color = color,
            radius = 10f,
            center = Offset(20f, 20f)
        )
    }
}

@Composable
fun StyledText(text: String, iscenter: Boolean) {

    var color_font = colorResource(id = R.color.color_font)
    var color_blck = colorResource(id = R.color.black)

    val annotatedText = remember(text) {
        buildAnnotatedString {
            val parts = parseText(text)
            parts.forEach { part ->
                if (part.isBold) {
                    withStyle(
                        style = SpanStyle(
                            color = color_blck,
                            fontSize = 16.sp,
                            textDecoration = TextDecoration.None,
                            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                        )
                    ) {
                        append(part.text)
                    }
                } else {
                    withStyle(
                        style = SpanStyle(
                            color = color_font,
                            fontSize = 16.sp,
                            textDecoration = TextDecoration.None,
                            fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                        )
                    ) {
                        append(part.text)
                    }
                }
            }
        }
    }
    BasicText(
        text = annotatedText,
        modifier = Modifier.wrapContentHeight(),
        style = TextStyle(
            textAlign = if (iscenter) TextAlign.Center else TextAlign.Start,
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
            textDecoration = TextDecoration.None
        ),

        )
    /*BasicText(buildAnnotatedString {
        val parts = parseText(text)
        parts.forEach { part ->
            if (part.isBold) {
                withStyle(
                    style = SpanStyle(
                        color = color_blck,
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.None,
                        fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
                    )
                ) {
                    append(part.text)
                }
            } else {
                withStyle(
                    style = SpanStyle(
                        color = color_font,
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.None,
                        fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
                    )
                ) {
                    append(part.text)
                }
                //  append(part.text)
            }
        }
    }
    )*/


}

fun parseText(text: String): List<TextPart> {
    val parts = mutableListOf<TextPart>()
    var currentIndex = 0

    if (!text.contains("**")) {
        parts.add(TextPart(text, false))
    } else {
        while (currentIndex < text.length) {
            val startIndex = text.indexOf("**", currentIndex)
            if (startIndex == -1) {
                parts.add(TextPart(text.substring(currentIndex), false))
                break
            }

            val endIndex = text.indexOf("**", startIndex + 2)
            if (endIndex == -1) {
                parts.add(TextPart(text.substring(currentIndex), false))
                break
            }

            parts.add(TextPart(text.substring(currentIndex, startIndex), false))
            parts.add(TextPart(text.substring(startIndex + 2, endIndex), true))

            currentIndex = endIndex + 2
        }
    }


    /* if (currentIndex < text.length) {
         parts.add(TextPart(text.substring(currentIndex), false))
     }
 */
    return parts
}

data class TextPart(val text: String, val isBold: Boolean)
