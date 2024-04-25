package com.sanket.ingredicheck.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sanket.ingredicheck.R
import com.sanket.ingredicheck.adapters.DietaryAdapter
import com.sanket.ingredicheck.adapters.ItemAdapter
import com.sanket.ingredicheck.databinding.FragmentHomeBinding
import com.sanket.ingredicheck.model.Dietary
import com.sanket.ingredicheck.networking.RetrofitClint
import com.sanket.ingredicheck.response.ErrorResponse
import com.sanket.ingredicheck.utilities.MyApp
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.util.UUID

class HomeFragment : Fragment() {

    private var dialog: AlertDialog? = null
    private lateinit var dietaryAdapter: DietaryAdapter
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var dietaryItemList: MutableList<Dietary>
    private lateinit var followingItemList: MutableList<String>
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var activity:Activity
    private val handler = Handler(Looper.getMainLooper())
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setListeners()
        loadData()
        getData()
    }

    fun setListeners(){
        binding.clear.setOnClickListener {
            binding.addDietary.text.clear()
            if (binding.warning.visibility == View.VISIBLE){
                hideKeyboard()
            }
        }

        dietaryAdapter.clickListener = object : DietaryAdapter.OnItemClickListener {
            override fun onClick() {
                hideKeyboard()
            }
        }

        binding.main.setOnClickListener {
            hideKeyboard()
        }

        binding.addDietary.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.addDietaryLayout.background = ResourcesCompat.getDrawable(resources,R.drawable.et_active_bg,null)
                binding.warning.visibility = View.GONE
                if (s?.length != 0){
                    binding.clear.visibility = View.VISIBLE
                }else{
                    binding.clear.visibility = View.INVISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.addDietary.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.addDietaryLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.et_active_bg,null)
            }
        }

        binding.addDietary.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text.toString()
                binding.main.isFocusable = false
                hideKeyboard()
                if (!text.isEmpty()){
                    addPreference(text)
                }
                true // Return true to indicate the event was handled
            } else {
                false // Return false to allow other handlers to process the event
            }
        }

        binding.recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager:LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findFirstVisibleItemPosition()
                if (position == 0){
                    binding.indicator.setCurrentPage(0)
                }else if (position == followingItemList.size-1){
                    binding.indicator.setCurrentPage(2)
                }else{
                    binding.indicator.setCurrentPage(1)
                }
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as Activity;
    }

    private fun initialize() {
        initLoadingDialogue()
        binding.addDietary.imeOptions = EditorInfo.IME_ACTION_DONE;
        binding.addDietary.setRawInputType(InputType.TYPE_CLASS_TEXT);
        binding.isEmpty = true
        dietaryItemList = ArrayList()
        followingItemList = ArrayList()
        dietaryAdapter = DietaryAdapter(activity,dietaryItemList)
        binding.dietaryRecycler.adapter = dietaryAdapter
        binding.main.isClickable = true

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recycler)
        itemAdapter = ItemAdapter(activity,followingItemList)
        binding.recycler.adapter = itemAdapter
    }

    private fun loadData(){
        followingItemList.add("I follow a vegetarian diet, but I\'m okay with eating fish")
        followingItemList.add("I follow a vegetarian diet, but I\'m okay with eating fish")
        followingItemList.add("I follow a vegetarian diet, but I\'m okay with eating fish")
        followingItemList.add("I follow a vegetarian diet, but I\'m okay with eating fish")
        itemAdapter.notifyDataSetChanged()
        binding.indicator.setPageIndicators(3)
    }

    fun addPreference(text: String){
        showDialogue()
        val uuid = UUID.randomUUID()
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("preference", text)
            .addFormDataPart("clientActivityId", uuid.toString())
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            val supabaseClient = (activity.applicationContext as MyApp).supabaseClient
            val session: UserSession? = supabaseClient.auth.sessionManager.loadSession()
            if (session != null) {
                val accessToken = session.accessToken
                Log.d(TAG, "addPreference: $accessToken")
                RetrofitClint.setAuthorizationToken(accessToken)
                val response = RetrofitClint.getApi().addPreference(requestBody)
                launch(Dispatchers.Main) {
                    hideDialogue()
                    if (response.isSuccessful) {
                        binding.addDietary.text.clear()
                        response.body()?.let {
                            val dietary = Dietary(it.text,it.annotatedText,it.id)
                            dietaryItemList.add(0,dietary)
                            dietaryAdapter.notifyItemInserted(0)
                            binding.isEmpty = false
                        }
                        binding.addDietaryLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.et_bg,null)
                    }else{
                        val errorBody = response.errorBody()?.string()
                        val errorResponse: ErrorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                        binding.addDietaryLayout.background = ResourcesCompat.getDrawable(resources,R.drawable.et_red_bg,null)
                        binding.warning.text = errorResponse.explanation
                        binding.warning.visibility = View.VISIBLE
                    }
                }
            }else{
                Log.d(TAG, "addPreference: session null")
            }
        }
    }


    fun getData(){
        CoroutineScope(Dispatchers.IO).launch {
            val supabaseClient = (activity.applicationContext as MyApp).supabaseClient
            val session: UserSession? = supabaseClient.auth.sessionManager.loadSession()
            if (session != null) {
                val accessToken = session.accessToken
                Log.d(TAG, "getData: $accessToken")
                RetrofitClint.setAuthorizationToken(accessToken)
                val response = RetrofitClint.getApi().getPreferenceLists();
                if (response.isSuccessful) {
                    launch(Dispatchers.Main) {
                        Log.d(TAG, "getData: ${GsonBuilder().setPrettyPrinting().create().toJson(response.body())}")
                        if (!response.body().isNullOrEmpty()) {
                            binding.isEmpty = false
                            response.body()?.let {
                                dietaryItemList.addAll(it)
                            }
                        }else{
                            binding.isEmpty = true
                        }
                    }
                }else{
                    Log.d(TAG, "getData: response fail: "+response.message())
                }
            }else{
                Log.d(TAG, "getData: session null")
            }
        }
    }

    fun initLoadingDialogue() {
        val view = LayoutInflater.from(activity).inflate(R.layout.custom_loading, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    
    fun showDialogue(){
        dialog?.show()
    }

    fun hideDialogue() {
        if (dialog != null && dialog?.isShowing == true)
            dialog?.dismiss()
    }

    fun hideKeyboard() {
        binding.addDietary.clearFocus()
        binding.addDietaryLayout.background = ResourcesCompat.getDrawable(resources, R.drawable.et_bg,null)
        binding.warning.visibility = View.GONE
        val view: View? = activity.currentFocus
        view?.let {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}