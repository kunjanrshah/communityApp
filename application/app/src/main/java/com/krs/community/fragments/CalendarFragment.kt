package com.krs.community.fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.copyViewImage
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.header_calendar.*
import ru.slybeaver.slycalendarview.SlyCalendarDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarFragment : Fragment(), SlyCalendarDialog.Callback {
    override fun onDataSelected(firstDate: Calendar?, secondDate: Calendar?, hours: Int, minutes: Int) {

        if (firstDate != null) {
            var str: String
            if (secondDate == null) {

                str = SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.getTime())
                Log.d(TAG, str)

            } else {
                str = getString(
                        R.string.period,
                        SimpleDateFormat(getString(R.string.dateFormat)).format(firstDate.getTime()),
                        SimpleDateFormat(getString(R.string.dateFormat)).format(secondDate.getTime())
                )
                Log.d(TAG, str)
            }
            txtdate.setText(str)
        }
    }

    override fun onCancelled() {
        //Nothing
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    lateinit var recyclerView: RecyclerView
    lateinit var ll_root: LinearLayout
    var tithi: Boolean = true
    var panchag: Boolean = true
    var anniversay: Boolean = true
    var birthday: Boolean = true
    var reminder: Boolean = true
    var typeface: Typeface = AppController.getInstance().typeface
    var typeface_bold: Typeface = AppController.getInstance().typeface_bold

    var TAG: String = "CalendarFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(com.krs.community.R.layout.fragment_calendar, container, false)

        (activity as AppCompatActivity).supportActionBar!!.title = "Search by Calendar"

        ll_root = root.findViewById<LinearLayout>(com.krs.community.R.id.ll_root)
        recyclerView = root.findViewById<RecyclerView>(com.krs.community.R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        createCardAdapter(recyclerView)
        return root
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(com.krs.community.R.dimen.details_toolbar_container_height) - view.height / 2f

        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + activity!!.myAppBar.height
        positions[2] = toY

        val adapterPosition = recyclerView.getChildAdapterPosition(view)
        val detailsFragment = SearchbyDetailFragment.newInstance(positions, adapterPosition)
        val transaction = fragmentManager?.beginTransaction()
                ?.replace(com.krs.community.R.id.container_body, detailsFragment, SearchDetailFragment.TAG)
                ?.addToBackStack(null)

         return transaction
    }

    private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.commitAllowingStateLoss()
    }

    private fun createCardAdapter(recyclerView: RecyclerView) {
        val content = ArrayList<String>()
        for (i in 0..49) {
            content.add("item $i")
        }

        val adapter = object : ParallaxRecyclerAdapter<String>(content) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<String>, i: Int) {
                (viewHolder as CalendarViewHolder).tv_name.setText("Kunjan Shah")
                (viewHolder as CalendarViewHolder).txt_area.setText("Maninagar, Ahmedabad")
                (viewHolder as CalendarViewHolder).txt_email.setText("kunjanrshah@gmail.com")
                (viewHolder as CalendarViewHolder).txt_mobile.setText("9427051418")
                (viewHolder as CalendarViewHolder).family_role.setText("Family Head")
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<String>, i: Int): RecyclerView.ViewHolder {
                return CalendarViewHolder(layoutInflater.inflate(com.krs.community.R.layout.row_list_new, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<String>): Int {
                return content.size
            }
        }

        adapter.setOnClickEvent { v, position ->
            val fragmentTransaction = initFragmentTransaction(v)
            val copy = view!!.copyViewImage()
            copy.y += activity!!.myAppBar.height
            ll_root.addView(copy)
            view!!.visibility = View.INVISIBLE
            startAnimation(copy, fragmentTransaction)
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        recyclerView.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(com.krs.community.R.layout.header_calendar, recyclerView, false)

        val fab: FloatingActionButton
        val txtdate: TextView
        val imgCalendar: ImageView
        val lstCalFliter: RecyclerView

        fab = header.run { findViewById(com.krs.community.R.id.fab) }
        txtdate = header.findViewById(com.krs.community.R.id.txtdate)
        imgCalendar = header.findViewById(com.krs.community.R.id.imgCalendar)
        lstCalFliter = header.findViewById(R.id.lstCalFliter)
        lstCalFliter.setHasFixedSize(true)
        val MyLayoutManager = LinearLayoutManager(activity)
        MyLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        if (lstCalFliter != null) {
            var list: ArrayList<String>
            list = ArrayList();
            list.add("Tithi")
            list.add("Panchang")
            list.add("Birthday")
            list.add("Anniversary")
            list.add("Reminder")

            lstCalFliter.setAdapter(FilterAdapter(list))
        }
        lstCalFliter.setLayoutManager(MyLayoutManager)


        imgCalendar.setOnClickListener {
            SlyCalendarDialog()
                    .setSingle(false)
                    .setCallback(this)
                    .setHeaderColor(resources.getColor(R.color.colorPrimary))
                    .setBackgroundColor(Color.parseColor("#ffffff"))
                    .setSelectedColor(Color.parseColor("#c48395"))
                    .show(activity?.getSupportFragmentManager(), "TAG_SLYCALENDAR")
        }
        fab.setOnClickListener {
            Toast.makeText(activity, "search", Toast.LENGTH_LONG).show()
        }

        val str = SimpleDateFormat(getString(R.string.dateFormat)).format(Date())
        txtdate.setText(str)

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, recyclerView)
        adapter.data = content
        recyclerView.adapter = adapter
    }


    internal class CalendarViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_name: TextView
        var txt_area: TextView
        var txt_event: TextView
        var txt_email: TextView
        var txt_mobile: TextView
        var family_role: TextView
        init {
            var typeface: Typeface = AppController.getInstance().typeface
            var typeface_bold: Typeface = AppController.getInstance().typeface_bold
            tv_name = v.findViewById<View>(com.krs.community.R.id.tv_name) as TextView
            tv_name.setTypeface(typeface_bold)

            txt_area = v.findViewById(R.id.txt_area)
            txt_area.setTypeface(typeface)
            txt_event = v.findViewById(R.id.txt_event)
            txt_event.setTypeface(typeface)
            txt_email = v.findViewById(R.id.txt_email)
            txt_email.setTypeface(typeface)
            txt_mobile = v.findViewById(R.id.txt_mobile)
            txt_mobile.setTypeface(typeface)
            family_role = v.findViewById(R.id.family_role)
            family_role.setTypeface(typeface_bold)
        }
    }

    internal inner class FilterViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var txt_name: TextView

        init {
            txt_name = v.findViewById(R.id.txt_name)
            txt_name.setTypeface(typeface_bold)

        }
    }

    internal inner class FilterAdapter(arrayList: ArrayList<String>) : RecyclerView.Adapter<FilterViewHolder>() {
        private var list: ArrayList<String>? = null

        init {
            list = arrayList
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_calendar, parent, false)
            return FilterViewHolder(view)
        }

        override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {

            holder.txt_name.text = list!![position]
            if (holder.txt_name.text.equals("Tithi")) {
                holder.txt_name.setBackgroundResource(R.drawable.filter_tithi)
                holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
            } else if (holder.txt_name.text.equals("Panchang")) {
                holder.txt_name.setBackgroundResource(R.drawable.filter_panchag)
                holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
            } else if (holder.txt_name.text.equals("Birthday")) {
                holder.txt_name.setBackgroundResource(R.drawable.filter_birthday)
                holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
            } else if (holder.txt_name.text.equals("Anniversary")) {
                holder.txt_name.setBackgroundResource(R.drawable.filter_ann)
                holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
            } else if (holder.txt_name.text.equals("Reminder")) {
                holder.txt_name.setBackgroundResource(R.drawable.filter_reminder)
                holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
            }

            holder.txt_name.setOnClickListener {
                if (holder.txt_name.text.equals("Tithi")) {
                    if (tithi) {
                        tithi = false
                        holder.txt_name.setBackgroundResource(R.drawable.filter_fill_tithi)
                        holder.txt_name.setTextColor(resources.getColor(R.color.white))
                    } else {
                        tithi = true
                        holder.txt_name.setBackgroundResource(R.drawable.filter_tithi)
                        holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                } else if (holder.txt_name.text.equals("Panchang")) {
                    if (panchag) {
                        panchag = false
                        holder.txt_name.setBackgroundResource(R.drawable.filter_fill_panchag)
                        holder.txt_name.setTextColor(resources.getColor(R.color.white))
                    } else {
                        panchag = true
                        holder.txt_name.setBackgroundResource(R.drawable.filter_panchag)
                        holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }

                } else if (holder.txt_name.text.equals("Birthday")) {
                    if (birthday) {
                        birthday = false
                        holder.txt_name.setBackgroundResource(R.drawable.filter_fill_birthday)
                        holder.txt_name.setTextColor(resources.getColor(R.color.white))
                    } else {
                        birthday = true
                        holder.txt_name.setBackgroundResource(R.drawable.filter_birthday)
                        holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }

                } else if (holder.txt_name.text.equals("Anniversary")) {
                    if (anniversay) {
                        anniversay = false
                        holder.txt_name.setBackgroundResource(R.drawable.filter_fill_ann)
                        holder.txt_name.setTextColor(resources.getColor(R.color.white))
                    } else {
                        anniversay = true
                        holder.txt_name.setBackgroundResource(R.drawable.filter_ann)
                        holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }

                } else if (holder.txt_name.text.equals("Reminder")) {
                    if (reminder) {
                        reminder = false
                        holder.txt_name.setBackgroundResource(R.drawable.filter_fill_reminder)
                        holder.txt_name.setTextColor(resources.getColor(R.color.white))
                    } else {
                        reminder = true
                        holder.txt_name.setBackgroundResource(R.drawable.filter_reminder)
                        holder.txt_name.setTextColor(resources.getColor(R.color.mdtp_transparent_black))
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return list!!.size
        }
    }

}
