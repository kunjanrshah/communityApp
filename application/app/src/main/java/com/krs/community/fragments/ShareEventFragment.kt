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
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
import com.krs.community.listeners.CreateEventListener
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.ShareEventViewModel
import com.krs.community.viewmodelfactory.ShareEventViewModelFactory
import com.orhanobut.dialogplus.DialogPlus
import com.zfdang.multiple_images_selector.ImagesSelectorActivity
import com.zfdang.multiple_images_selector.SelectorSettings
import kotlinx.android.synthetic.main.fragment_share_event.*
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShareEventFragment : Fragment(), KodeinAware,CreateEventListener {
    override val kodein by kodein()
    private lateinit var shareEventViewModel: ShareEventViewModel
    private val shareEventFactory: ShareEventViewModelFactory by instance()
    // class variables
    private val REQUEST_CODE = 123
    private var adapter: ImagesAdapter? = null
    private var mResults: ArrayList<String> = ArrayList()
    private var yURLs = ArrayList<String>()
    lateinit var txt_start: TextView
    lateinit var edt_end_date: TextView
    lateinit var txt_end_time: TextView
    lateinit var txt_start_time: TextView
    var isStart = false
    private lateinit var userId:String
    lateinit var linearLayout:LinearLayout
    lateinit  var adapter1:URLAdapter;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_share_event, container, false)
        shareEventViewModel = ViewModelProviders.of(this,shareEventFactory).get(ShareEventViewModel::class.java)


        shareEventViewModel.mCreateEventListener = this
        userId= Guru.getString(getString(R.string.user_id), "")!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.color_mid_light_gray, false)
        }
        val iv_cancel = root.findViewById<ImageView>(R.id.iv_cancel)
        linearLayout = root.findViewById(R.id.main_content)
        iv_cancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        val rv_images: RecyclerView = root.findViewById(R.id.rv_images)
        rv_images.setHasFixedSize(true)
        val MyLayoutManager = LinearLayoutManager(activity)
        MyLayoutManager.orientation = RecyclerView.HORIZONTAL
        adapter = ImagesAdapter()
        rv_images.adapter = adapter
        rv_images.layoutManager = MyLayoutManager
        val rv_parent: RecyclerView = root.findViewById(R.id.rv_parent)
        val edt_title = root.findViewById<EditText>(R.id.edt_title)
        val edt_address = root.findViewById<EditText>(R.id.edt_address)
        val edt_description = root.findViewById<EditText>(R.id.edt_description)
        rv_parent.setHasFixedSize(true)
        val MyLayoutManager1 = LinearLayoutManager(activity)
        MyLayoutManager1.orientation = RecyclerView.VERTICAL
        yURLs.add("")
        yURLs.add("")
        yURLs.add("")
        adapter1 = URLAdapter()
        rv_parent.adapter = adapter1
        rv_parent.layoutManager = MyLayoutManager1
        val iv_add_url = root.findViewById<ImageView>(R.id.iv_add_url)
        iv_add_url.setOnClickListener { v: View? ->
            yURLs.add("")
            adapter1.notifyDataSetChanged()
        }
        val iv_upload = root.findViewById<ImageView>(R.id.iv_upload)
        iv_upload.setOnClickListener { v: View? ->
            val intent = Intent(activity, ImagesSelectorActivity::class.java)
            intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 15)
            intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000)
            intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true)
            intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults)
            startActivityForResult(intent, REQUEST_CODE)
        }
        val btnShare: Button
        val btnCreate: Button
        btnShare = root.findViewById(R.id.btnShare)
        btnCreate = root.findViewById(R.id.btnCreate)
        txt_start = root.findViewById(R.id.edt_start)
        edt_end_date = root.findViewById(R.id.edt_end_date)
        txt_start_time = root.findViewById(R.id.txt_start_time)
        txt_end_time = root.findViewById(R.id.txt_end_time)
        txt_start.setOnClickListener(View.OnClickListener { view: View? ->
            isStart = true
            showCalendar()
        })
        edt_end_date.setOnClickListener({ view: View? ->
            isStart = false
            showCalendar()
        })
        txt_start_time.setOnClickListener({ view: View? ->
            isStart = true
            showTimerSelection()
        })
        txt_end_time.setOnClickListener({ view: View? ->
            isStart = false
            showTimerSelection()
        })
        btnCreate.setOnClickListener { v: View? ->
            var isValidated = true
            if (edt_title.text.toString().length == 0) {
                edt_title.error = "Event title is required"
                isValidated = false
            }
            if (edt_address.text.toString().length == 0) {
                edt_address.error = "Event address is required"
                isValidated = false
            }
            if (edt_description.text.toString().length == 0) {
                edt_description.error = "Event description is required"
                isValidated = false
            }
            if (txt_start.getText().toString().length == 0) {
                txt_start.setError("Start date is required")
                isValidated = false
            } else {
                txt_start.setError(null)
            }
            if (edt_end_date.getText().toString().length == 0) {
                edt_end_date.setError("End date is required")
                isValidated = false
            } else {
                edt_end_date.setError(null)
            }
            if (txt_start_time.getText().toString().length == 0) {
                txt_start_time.setError("Start time is required")
                isValidated = false
            } else {
                txt_start_time.setError(null)
            }
            if (txt_end_time.getText().toString().length == 0) {
                txt_end_time.setError("End time is required")
                isValidated = false
            } else {
                txt_end_time.setError(null)
            }
            if (isValidated) {
                yURLs.removeAll(Arrays.asList(""));
                val json = JSONObject()
                json.put("id",userId);
                json.put("event_date",edt_start.text.toString())
                json.put("title",edt_title.text.toString())
                json.put("description",edt_description.text.toString())
                json.put("location",edt_address.text.toString())
                json.put("lat","23.7546")
                json.put("lng","72.2308")
                json.put("youtube",yURLs);


//              val data = "{\"id\":\"1\",\"event_date\":\"2020-01-01\",\"title\":\"DemoTitile\",\"description\":\"DemoDescription\",\"location\":\"DemoLocation\",\"lat\":\"23.7546\",\"lng\":\"72.2308\",\"youtube\":[\"https:\\/\\/youtube.com\",\"https:\\/\\/youtube.com\"]}";
                Utility.startSweetProgress(activity, "Creating an event", "Please wait...")
                shareEventViewModel.createEvent(mResults,userId,userId,Guru.getString(getString(R.string.access_token), "").toString(),json.toString(),yURLs)
            }
        }
        btnShare.setOnClickListener { v: View? ->
            val adapter = ShareEventAdapter()
            val dialog = DialogPlus.newDialog(context).setAdapter(adapter).setGravity(Gravity.BOTTOM).setCancelable(true).setExpanded(true, 900).setContentBackgroundResource(R.drawable.popup_top_corner).create()
            dialog.show()
        }
        return root
    }

    private fun showCalendar() {
        SlyCalendarDialog()
                .setSingle(false)
                .setCallback(object:SlyCalendarDialog.Callback{
                    override fun onDataSelected(firstDate: Calendar?, secondDate: Calendar?, hours: Int, minutes: Int) {
                       try {
                           val str = SimpleDateFormat(getString(R.string.dateFormat_first)).format(firstDate?.time)
                           if (isStart) {
                               txt_start.error = null
                               txt_start.text = str
                           } else {
                               edt_end_date.error = null
                               edt_end_date.text = str
                           }
                       }catch (ignore:java.lang.Exception){

                       }

                    }

                    override fun onCancelled() {

                    }

                })
                .setHeaderColor(resources.getColor(R.color.colorPrimary))
                .setBackgroundColor(Color.parseColor("#ffffff"))
                .setSelectedColor(Color.parseColor("#c48395"))
                .show(activity!!.supportFragmentManager, "TAG_SLYCALENDAR")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // get selected images from selector
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mResults = data!!.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS)
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
        mTimePicker = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
            if (isStart) {
                txt_start_time.error = null
                txt_start_time.text = (if (selectedHour < 10) "0$selectedHour" else selectedHour.toString() ).plus( ":") .plus( if (selectedMinute < 10) "0$selectedMinute" else selectedMinute)
            } else {
                txt_end_time.text = (if (selectedHour < 10) "0$selectedHour" else selectedHour.toString() ).plus( ":") .plus( if (selectedMinute < 10) "0$selectedMinute" else selectedMinute)
                txt_end_time.error = null
            }
        }, hour, minute, true) //Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }


     inner class URLAdapter : RecyclerView.Adapter<URLViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): URLViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_youtube_url, parent, false)
            return URLViewHolder(view)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: URLViewHolder, position: Int) {
            holder.edt_yurl.setOnTouchListener { v: View?, event: MotionEvent ->
                val DRAWABLE_RIGHT = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= holder.edt_yurl.right - holder.edt_yurl.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        Log.d("YoutubeURL", "position: $position")
                        yURLs.removeAt(position)
                        notifyDataSetChanged()
                        return@setOnTouchListener true
                    }
                }
                false
            }

            holder.edt_yurl.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {

                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    yURLs[position] = s.toString();
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
            val mInflater = activity!!.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_event_image, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            var filepath = ""
            try {
                filepath = mResults!![position]
                val uri = Uri.fromFile(File(filepath))
                var bitmap: Bitmap? = null
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
                    if (bitmap != null) {
                        val bmp = Utility.getRoundedCornerBitmap(bitmap, 100)
                        Glide.with(context!!).load(bmp).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.iv_event)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                holder.iv_cancel.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.iv_cancel.visibility = View.GONE
                Glide.with(context!!).load(R.drawable.photo).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.iv_event)
                e.printStackTrace()
            }
            holder.iv_cancel.setOnClickListener { v: View? ->
                mResults!!.removeAt(position)
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return if (mResults!!.size < 3) {
                3
            } else {
                mResults!!.size
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
        linearLayout.snackbar( profile,Snackbar.LENGTH_SHORT)
        edt_title.setText("")
        edt_description.setText("")
        edt_address.setText("")
        edt_start.setText("")
        edt_end_date.setText("")
        txt_start_time.setText("")
        txt_end_time.setText("")
        txt_end_time.setText("")
        mResults = ArrayList()
        yURLs = ArrayList()
        adapter?.notifyDataSetChanged()
        adapter1.notifyDataSetChanged()
    }

    override suspend fun onFailure(message: String) {
        Utility.hideSweetProgress()
        linearLayout.snackbar("Something went wrong!", Snackbar.LENGTH_LONG)
        Log.d(ShareEventFragment::class.java.simpleName, "getFailure: " + message)
    }
}