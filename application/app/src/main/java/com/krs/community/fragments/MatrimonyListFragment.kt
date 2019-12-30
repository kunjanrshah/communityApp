package com.krs.community.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krs.community.R
import com.krs.community.databinding.FragmentMatrimonylistBinding
import com.krs.community.interfaces.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.Utility
import com.krs.community.utils.openFilter
import com.krs.community.viewmodel.MatrimonySearchViewModel
import com.krs.community.viewmodel.MatrimonySearchViewModelFactory
import com.nightonke.boommenu.BoomMenuButton
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class MatrimonyListFragment : Fragment(), KodeinAware, ByFilterListener {

    private lateinit var lstMatrimony: List<Member>
    override val kodein by kodein()
    private val factory: MatrimonySearchViewModelFactory by instance()
    private lateinit var matrimonySearchViewModel: MatrimonySearchViewModel
    private lateinit var binding:FragmentMatrimonylistBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding=DataBindingUtil.inflate(inflater, R.layout.fragment_matrimonylist, container, false)

        matrimonySearchViewModel = ViewModelProviders.of(this,factory).get(MatrimonySearchViewModel::class.java)
        matrimonySearchViewModel.mByFilterListener =this

        val adapter: ParallaxRecyclerAdapter<Member> = object : ParallaxRecyclerAdapter<Member>(lstMatrimony) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {
                val holder = viewHolder as ListViewHolder
                holder.tvName.text = "Kunjan Shah"
                holder.boomMenuButton.clearBuilders()
                for (i in 0 until holder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }
                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.matrimony_profile, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return 10
            }
        }

        val header = LayoutInflater.from(activity).inflate(R.layout.header_matrimony, container, false)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        val edtSearch = header.findViewById<EditText>(R.id.edtSearch)
        edtSearch.setOnTouchListener { v: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if ((event.rawX+35) >= edtSearch.right - edtSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    activity?.let { openFilter(it,matrimonySearchViewModel) }
                    return@setOnTouchListener true
                }
            }
            false
        }
        adapter.setParallaxHeader(header, binding.listMatrimony)
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.listMatrimony.layoutManager = linearLayoutManager
        binding.listMatrimony.itemAnimator = DefaultItemAnimator()
        binding.listMatrimony.setHasFixedSize(true)



        binding.listMatrimony.adapter = adapter
        return binding.root
    }

    inner class ListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvName: TextView = v.findViewById(R.id.tv_name)
        var boomMenuButton: BoomMenuButton = v.findViewById(R.id.boomMenuButton)
    }

    override fun getMembers(response: SmartFilterResponse) {
        if(response.success){

        }
    }

    override fun getFailure(message: String) {

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}