package com.krs.community.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.app.AppController
import com.krs.community.databinding.FragmentShareEventBinding
import com.krs.community.listeners.CreateEventListener
import com.krs.community.utils.MovableFloatingActionButton
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.ShareEventViewModel
import com.krs.community.viewmodelfactory.ShareEventViewModelFactory
import com.orhanobut.dialogplus.DialogPlus
import com.zfdang.multiple_images_selector.ImagesSelectorActivity
import com.zfdang.multiple_images_selector.SelectorSettings
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ShareEventFragment : Fragment(), KodeinAware, CreateEventListener {
    override val kodein by kodein()
    private lateinit var shareEventViewModel: ShareEventViewModel
    private val shareEventFactory: ShareEventViewModelFactory by instance<ShareEventViewModelFactory>()

    // class variables
    private val REQUEST_CODE = 123
    private var adapter: ImagesAdapter? = null
    private var mResults: ArrayList<String> = ArrayList()
    private var yURLs = ArrayList<String>()
    lateinit var txtStart: TextView
    lateinit var edtTitle: TextView
    lateinit var edtAddress: TextView
    lateinit var edtDescription: TextView

    //lateinit var edtEndDate: TextView
    //  private lateinit var txtEndTime: TextView
    private lateinit var txtStartTime: TextView
    var isStart = false
    private lateinit var userId: String
    lateinit var linearLayout: LinearLayout
    lateinit var adapter1: URLAdapter
    lateinit var fab: MovableFloatingActionButton
    lateinit var rvParent: RecyclerView
    private lateinit var binding: FragmentShareEventBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_share_event, container, false)
        binding = FragmentShareEventBinding.inflate(inflater)
        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, ShareEventFragment::class.simpleName)
        mApp.facebookAnalytics(context, ShareEventFragment::class.simpleName)

        shareEventViewModel =
            ViewModelProvider(this, shareEventFactory).get(ShareEventViewModel::class.java)

        shareEventViewModel.mCreateEventListener = this
        userId = Guru.getString(getString(R.string.user_id), "")!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.color_mid_light_gray, false)
        }

        val ivCancel = root.findViewById<ImageView>(R.id.iv_cancel)
        linearLayout = root.findViewById(R.id.main_content)
        ivCancel.setOnClickListener { v: View? ->
            Utility.movetoFragment(
                activity,
                DashboardFragment()
            )
        }
        val rvImages: RecyclerView = root.findViewById(R.id.rv_images)
        rvImages.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        adapter = ImagesAdapter()
        rvImages.adapter = adapter
        rvImages.layoutManager = linearLayoutManager
        rvParent = root.findViewById(R.id.rv_parent)
        edtTitle = root.findViewById<EditText>(R.id.edt_title)
        edtAddress = root.findViewById<EditText>(R.id.edt_address)
        edtDescription = root.findViewById<EditText>(R.id.edt_description)
        yUrlAdapterInit()
        val ivAddUrl = root.findViewById<ImageView>(R.id.iv_add_url)
        ivAddUrl.setOnClickListener { v: View? ->
            yURLs.add("")
            adapter1.notifyDataSetChanged()
        }
        val ivUpload = root.findViewById<ImageView>(R.id.iv_upload)
        ivUpload.setOnClickListener { v: View? ->
            val intent = Intent(activity, ImagesSelectorActivity::class.java)
            intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 15)
            intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000)
            intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true)
            intent.putStringArrayListExtra(
                SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST,
                mResults
            )
            startActivityForResult(intent, REQUEST_CODE)
        }
        txtStart = root.findViewById(R.id.edt_start)


        txtStart.setOnClickListener { view: View? ->
            showCalendar()
        }


        // val btnShare: Button
        // val btnCreate: Button
        // btnShare = root.findViewById(R.id.btnShare)
        /*btnCreate = root.findViewById(R.id.btnCreate)*/
        fab = root.findViewById(R.id.fab)
        //  edtEndDate = root.findViewById(R.id.edt_end_date)
        txtStartTime = root.findViewById(R.id.txt_start_time)
        // txtEndTime = root.findViewById(R.id.txt_end_time)
//        txtStart.setOnClickListener { view: View? ->
//            isStart = true
//            // showCalendar()
//        }
        /* edtEndDate.setOnClickListener({ view: View? ->
             isStart = false
             showCalendar()
         })*/
        txtStartTime.setOnClickListener { view: View? ->
            showTimerSelection()
        }
        /*txtEndTime.setOnClickListener({ view: View? ->
            isStart = false
            showTimerSelection()
        })*/
        fab.setOnClickListener { v: View? ->
            var isValidated = true
            if (edtTitle.text.toString().isEmpty()) {
                edtTitle.error = "Event title is required"
                isValidated = false
            }
            if (edtAddress.text.toString().isEmpty()) {
                edtAddress.error = "Event address is required"
                isValidated = false
            }
            if (edtDescription.text.toString().isEmpty()) {
                edtDescription.error = "Event description is required"
                isValidated = false
            }
            if (txtStart.text.toString().isEmpty()) {
                txtStart.error = "Start date is required"
                isValidated = false
            } else {
                txtStart.error = null
            }
            /* if (edtEndDate.getText().toString().length == 0) {
                 edtEndDate.setError("End date is required")
                 isValidated = false
             } else {
                 edtEndDate.setError(null)
             }*/
            if (txtStartTime.text.toString().length == 0) {
                txtStartTime.error = "Start time is required"
                isValidated = false
            } else {
                txtStartTime.error = null
            }
            /*            if (txtEndTime.getText().toString().length == 0) {
                            txtEndTime.setError("End time is required")
                            isValidated = false
                        } else {
                            txtEndTime.setError(null)
                        }

                        if (isValidated) {
                            yURLs.removeAll(Arrays.asList(""))
                            val json = JSONObject()
                            json.put("id", userId)
                            json.put("event_date", binding.edtStart.text.toString())
                            json.put("title", edtTitle.text.toString())
                            json.put("description", edtDescription.text.toString())
                            json.put("location", edtAddress.text.toString())
                            json.put("lat", "23.7546")
                            json.put("lng", "72.2308")
                            json.put("youtube", yURLs)


                          val data = "{\"id\":\"1\",\"event_date\":\"2020-01-01\",\"title\":\"DemoTitile\",\"description\":\"DemoDescription\",\"location\":\"DemoLocation\",\"lat\":\"23.7546\",\"lng\":\"72.2308\",\"youtube\":[\"https:\\/\\/youtube.com\",\"https:\\/\\/youtube.com\"]}";
                            Utility.startSweetProgress(activity, "Creating an event", "Please wait...")
                            shareEventViewModel.createEvent(mResults, userId, userId, Guru.getString(getString(R.string.access_token), "").toString(), json.toString(), yURLs)
            //                Log.d("okhttp","${shareEventViewModel.createEvent(mResults, userId, userId, Guru.getString(getString(R.string.access_token), "").toString(), json.toString(), yURLs)}")
                        }*/

            Log.e("isValidated", isValidated.toString())
            if (isValidated) {
                val json = JSONObject()
                json.put("id", userId)
                json.put(
                    "event_date",
                    txtStart.text.toString() + " " + txtStartTime.text.toString()
                )
                json.put("title", edtTitle.text.toString())
                json.put("description", edtDescription.text.toString())
                json.put("location", edtAddress.text.toString())
                json.put("lat", "23.7546")
                json.put("lng", "72.2308")
                json.put("youtube", yURLs)
                json.put("images", mResults) // Adding images URL to the JSON object

                Utility.startSweetProgress(activity, "Creating an event", "Please wait...")
                // Call the ViewModel function to create the event
                shareEventViewModel.createEvent(
                    images = mResults,
                    id = userId,
                    title = edtTitle.text.toString(),
                    description = edtDescription.text.toString(),
                    location = edtAddress.text.toString(),
                    lat = "23.7546",
                    lng = "72.2308",
                    youtube = yURLs,
                    eventDate = binding.edtStart.text.toString()
                )

                Log.d("TAG", "Creating event with JSON data: $json")

            } else {
                Log.d("TAG", "event creation failed, event not created")
            }
        }
        binding.ivShare.setOnClickListener { v: View? ->
            val adapter = ShareEventAdapter()
            val dialog =
                DialogPlus.newDialog(context).setAdapter(adapter).setGravity(Gravity.BOTTOM)
                    .setCancelable(true).setExpanded(true, 900)
                    .setContentBackgroundResource(R.drawable.popup_top_corner).create()
            dialog.show()
        }
        return root
    }

    private fun yUrlAdapterInit() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = RecyclerView.VERTICAL
        yURLs.add("")
        rvParent.setHasFixedSize(true)
        adapter1 = URLAdapter()
        rvParent.adapter = adapter1
        rvParent.layoutManager = layoutManager
    }

    private fun showCalendar() {
        SlyCalendarDialog()
            .setSingle(false)
            .setCallback(object : SlyCalendarDialog.Callback {
                override fun onDataSelected(
                    firstDate: Calendar?,
                    secondDate: Calendar?,
                    hours: Int,
                    minutes: Int
                ) {
                    try {
                        val str = firstDate?.time?.let {
                            SimpleDateFormat(getString(R.string.dateFormat_dd_mm_yyyy)).format(
                                it
                            )
                        }
                        txtStart.error = null
                        txtStart.text = str

                    } catch (ignore: java.lang.Exception) {

                    }

                }

                override fun onCancelled() {

                }

            })
            .setHeaderColor(resources.getColor(R.color.colorPrimary))
            .setBackgroundColor(Color.parseColor("#ffffff"))
            .setSelectedColor(Color.parseColor("#c48395"))
            .show(requireActivity().supportFragmentManager, "TAG_SLYCALENDAR")
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { // get selected images from selector
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS)!!
                }
                assert(mResults != null)
                // show results in textview
                val sb = StringBuffer()
                sb.append(String.format("Totally %d images selected:", mResults.size)).append("\n")
                for (result in mResults) {
                    sb.append(result).append("\n")
                }
                //   tvResults.setText(sb.toString());
                adapter!!.notifyDataSetChanged()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun showTimerSelection() {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(
            activity,
            { timePicker, selectedHour, selectedMinute ->
                txtStartTime.error = null
                txtStartTime.text =
                    (if (selectedHour < 10) "0$selectedHour" else selectedHour.toString()).plus(":")
                        .plus(if (selectedMinute < 10) "0$selectedMinute" else selectedMinute)
            },
            hour,
            minute,
            true
        ) //Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    inner class URLAdapter : RecyclerView.Adapter<URLViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): URLViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_youtube_url, parent, false)
            return URLViewHolder(view)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: URLViewHolder, position: Int) {
            holder.edt_yurl.setOnTouchListener { v: View?, event: MotionEvent ->
                val DRAWABLE_RIGHT = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    val drawableRight = holder.edt_yurl.compoundDrawables[DRAWABLE_RIGHT]
                    if (drawableRight != null) {  // Check if drawable exists
                        if (event.rawX >= holder.edt_yurl.right - drawableRight.bounds.width()) {
                            val adapterPosition = holder.bindingAdapterPosition
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                yURLs.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)  // Use notifyItemRemoved instead of notifyDataSetChanged for efficiency
                            }
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }


            holder.edt_yurl.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val adapterPosition = holder.bindingAdapterPosition
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        yURLs[adapterPosition] = s.toString()
                    }
                }
            })
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getItemCount(): Int {
            return yURLs.size
        }
    }

    inner class URLViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var edt_yurl: EditText

        init {
            edt_yurl = view.findViewById(R.id.edt_yurl)
        }
    }

    private inner class ShareEventAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return 1
        }

        override fun getItem(position: Int): Any {
            return 0
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            var convertView = convertView
            val viewHolder: ShareEventHolder
            val mInflater =
                activity!!.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.bottom_sheet_share_event, null)
                viewHolder = ShareEventHolder(convertView)
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ShareEventHolder
            }
            return convertView
        }
    }

    private inner class ShareEventHolder internal constructor(view: View) {
        var textView: TextView

        init {
            textView = view.findViewById(R.id.tv_d)
        }
    }

    private inner class ImagesAdapter : RecyclerView.Adapter<ImageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_event_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            var filepath = ""
            try {
                filepath = mResults[position]
                val uri = Uri.fromFile(File(filepath))
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                    if (bitmap != null) {
                        val bmp = Utility.getRoundedCornerBitmap(bitmap, 100)
                        Glide.with(context!!).load(bmp).thumbnail(0.5f)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into(holder.iv_event)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                holder.iv_cancel.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.iv_cancel.visibility = View.GONE
                Glide.with(context!!).load(R.drawable.photo).thumbnail(0.5f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.iv_event)
                e.printStackTrace()
            }
            holder.iv_cancel.setOnClickListener { v: View? ->
                mResults.removeAt(position)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return if (mResults.size < 3) {
                3
            } else {
                mResults.size
            }
        }
    }

    internal inner class ImageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var iv_event: ImageView
        var iv_cancel: ImageView

        init {
            iv_event = v.findViewById(R.id.iv_event)
            iv_cancel = v.findViewById(R.id.iv_cancel)
        }
    }

    override fun getResult(profile: String) {
        Utility.hideSweetProgress()
        linearLayout.snackbar(profile, Snackbar.LENGTH_SHORT)
        edtTitle.setText("")
        edtDescription.setText("")
        edtAddress.setText("")
        txtStart.text = ""
        txtStartTime.text = ""
        mResults.clear()
        yURLs.clear()
        adapter?.notifyDataSetChanged()
        yUrlAdapterInit()
    }

    override suspend fun onFailure(message: String) {
        Utility.hideSweetProgress()
        linearLayout.snackbar(getString(R.string.went_wrong), Snackbar.LENGTH_LONG)
        Log.d(ShareEventFragment::class.java.simpleName, "getFailure: " + message)
    }
}