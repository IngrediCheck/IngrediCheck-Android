package com.android.ingredicheck.Activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.ingredicheck.Constant.SharePrefrence
import com.android.ingredicheck.Constant.SupabaseHelp
import com.android.ingredicheck.DataClass.PagerItem
import com.android.ingredicheck.R
import com.android.ingredicheck.ui.Views.Loader_view
import com.android.ingredicheck.ui.Views.NoRippleInteractionSource
import com.android.ingredicheck.ui.Views.showToast
import com.android.ingredicheck.ui.theme.IngrediCheckTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import ir.kaaveh.sdpcompose.sdp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield


class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IngrediCheckTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Login_screen()
                }
            }
        }
    }
}


@Composable
fun Login_screen() {


    var context = LocalContext.current

    var supabase = SupabaseHelp().get_supaclient(context)!!

    var isprogress by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(initialPage = 0)
    val pagerItems = listOf(
        PagerItem(
            R.drawable.img_intro1,
            stringResource(R.string.log_t1), stringResource(R.string.log_dic1)
        ),
        PagerItem(
            R.drawable.img_intro2,
            stringResource(R.string.log_t2), stringResource(R.string.log_dic2)
        ),
        PagerItem(
            R.drawable.img_intro3,
            stringResource(R.string.log_t3), stringResource(R.string.log_dic3)
        ),
    )

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
            .fillMaxSize()
            .padding(horizontal = 15.sdp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            count = pagerItems.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            PagerItemView(pagerItems[page])
        }

        IndicatorDots(
            pageCount = 3, // Change pageCount as per your ViewPager pages
            currentPage = pagerState.currentPage
        )

        //   bt_applelogin()

        Spacer(modifier = Modifier.height(15.sdp))

        bt_googlelogin()

        Spacer(modifier = Modifier.height(10.sdp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clickable(
                    interactionSource = NoRippleInteractionSource(),
                    indication = null
                ) {
                    coroutineScope.launch {
                        isprogress = true
                        try {
                            supabase.auth.signInAnonymously(null, null)
                            val user = supabase.auth.currentUserOrNull()

                            val token = supabase.auth.currentSessionOrNull()?.accessToken ?: ""
                            if (user != null && token.isNotEmpty() && user.id.isNotEmpty()) {
                                SupabaseHelp.userSession = supabase.auth.currentSessionOrNull()
                                SharePrefrence(context).set_token(token)
                                isprogress = false
                                SharePrefrence(context).set_boolvalue("Googlelogin",false)
                                val intent = Intent(context, WelcomeActivity::class.java)
                                context.startActivity(intent)
                                (context as? Activity)?.finish()
                            } else {
                                showToast(context, "Please try agin")
                            }
                        } catch (e: Exception) {
                            showToast(context, "Please try agin")
                            isprogress = false
                        }
                    }
                }

            ,
            text = "Countinue as guest",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.color_appgreen),
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(7.sdp))

        TermsAndPrivacyText(onTermsClick = {
            val intent = Intent(context, WebviewPageActivity::class.java).putExtra(
                "is_link", "https://www.ingredicheck.app/terms-conditions"
            )
            context.startActivity(intent)
        }, onPrivacyClick = {
            val intent = Intent(context, WebviewPageActivity::class.java).putExtra(
                "is_link", "https://www.ingredicheck.app/privacy-policy"
            )
            context.startActivity(intent)
        })
    }

    if (isprogress) {
        Loader_view()
    }


}


@Composable
fun TermsAndPrivacyText(onTermsClick: () -> Unit, onPrivacyClick: () -> Unit) {
    val annotatedText = buildAnnotatedString {

        append("By continuing, you are agreeing to our\n")

        pushStringAnnotation(tag = "Terms", annotation = "Clickable")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Terms of Use")
        }
        pop()

        append(" and ")

        pushStringAnnotation(tag = "Privacy", annotation = "Clickable")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Privacy Policy")
        }
        pop()
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(offset, offset)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        "Terms" -> onTermsClick()
                        "Privacy" -> onPrivacyClick()
                    }
                }
        },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 10.dp)
            .fillMaxWidth(),
        style = TextStyle(
            fontSize = 14.sp,
            color = colorResource(id = R.color.color_lightfont),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    )
}

@Composable
fun bt_googlelogin() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var supabase = createSupabaseClient(
        supabaseUrl = context.getString(R.string.supa_url),
        supabaseKey = context.getString(R.string.supa_token)
    ) {
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = "478832614549-s0ucvjfchkikp57vj5u0bc29jqthme63.apps.googleusercontent.com")
        }
        install(Auth) {
        }
    }

    val authState = supabase.composeAuth.rememberSignInWithGoogle(
        onResult = {
            when (it) { //handle errors
                NativeSignInResult.ClosedByUser -> Log.e(
                    "mydataisloginres",
                    "ClosedByUser  is ===  "
                )

                is NativeSignInResult.Error -> Log.e(
                    "mydataisloginres",
                    "Error is ===  "
                )

                is NativeSignInResult.NetworkError -> Log.e(
                    "mydataisloginres",
                    "Error NetworkError ===  "
                )

                NativeSignInResult.Success -> {

                    val user = supabase.auth.currentUserOrNull()
                    val token = supabase.auth.currentSessionOrNull()?.accessToken ?: ""
                    if (user != null && token.isNotEmpty() && user.id.isNotEmpty()) {
                        SupabaseHelp.userSession = supabase.auth.currentSessionOrNull()
                        SharePrefrence(context).set_token(token)
                        SharePrefrence(context).set_boolvalue("Googlelogin",true)
                        val intent = Intent(context, WelcomeActivity::class.java)
                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    } else {
                        showToast(context, "Please try agin")
                    }
                }
            }
        }, fallback = { // optional: add custom error handling, not required by default

        }
    )
    val backgroundColor = colorResource(id = R.color.color_appgreen)
    val shape: RoundedCornerShape = CircleShape
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(backgroundColor, shape)
            .clickable(
                interactionSource = NoRippleInteractionSource(),
                indication = null
            ) {
                authState.startFlow()
            }
            ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = null,
            modifier = Modifier
                .wrapContentWidth()
                .padding(12.sdp)

        )


        Text(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentHeight(),
            text = "Sign in with Google",
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_semibold)),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.white),
            lineHeight = 18.sp,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                ),
            ),
        )
    }
}


/*fun google_login(context: Context, coroutineScope: CoroutineScope) {


    Log.e("mysininglog", "google_login   is done ---   ")
    val credentialManager = CredentialManager.create(context)
    val rawNonce = UUID.randomUUID()
        .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
    val bytes = rawNonce.toString().toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("759692907580-kfaoh4gt75hop72kfnpev0kurdtpqgav.apps.googleusercontent.com")
        .build()
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            MyApplication().getloginClient().auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
                nonce = rawNonce
            }

            Log.e("mysininglog", "singinglog  is done ---   ")


        } catch (e: GetCredentialException) {
            Log.e("mysininglog", "error   GetCredentialException ---   " + e.message)
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("mysininglog", "error   GoogleIdTokenParsingException ---   " + e.message)
        } catch (e: RestException) {
            Log.e("mysininglog", "error   RestException ---   " + e.message)
        } catch (e: Exception) {
            Log.e("mysininglog", "error   Exception ---   " + e.message)
        }
    }

}*/

@Composable
fun PagerItemView(item: PagerItem) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.sdp),
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(30.sdp))

        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.sdp)
                .weight(1f)

        )

        Spacer(modifier = Modifier.height(15.sdp))
        Text(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentHeight(),
            text = item.title,
            fontSize = 28.sp,
            fontFamily = FontFamily(Font(R.font.fredoka_medium)),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.color_font),
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(15.sdp))

        Text(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentHeight(),
            text = item.description,
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.sfpro_reguler)),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.color_lightfont),
            lineHeight = 18.sp
        )

    }
}

@Composable
fun IndicatorDots(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.sdp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until pageCount) {
                Dot(isSelected = i == currentPage)
        }
    }
}

@Composable
fun Dot(isSelected: Boolean) {
    val color =
        if (isSelected) colorResource(id = R.color.color_appgreen)
        else colorResource(id = R.color.color_grey)
    Box(
        modifier = Modifier
            .size(18.sdp)
            .padding(4.sdp)
            .background(color = color, shape = MaterialTheme.shapes.small),
    )
}


