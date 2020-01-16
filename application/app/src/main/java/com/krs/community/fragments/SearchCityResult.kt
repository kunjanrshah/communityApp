package com.krs.community.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.databinding.FragmentFilterResultBinding
import com.krs.community.interfaces.IbrowseCityRecordsListener
import com.krs.community.model.FilterBy
import com.krs.community.model.Member
import com.krs.community.model.SearchByCityData
import com.krs.community.model.SearchByCityModel
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.Coroutines
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.BrowseCityViewModel
import com.krs.community.viewmodel.BrowseCityViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class SearchCityResult : Fragment(), KodeinAware, IbrowseCityRecordsListener, ParallaxRecyclerAdapter.OnLoadMore,AtoZBottomAdapter.ISortingRecords {

    override fun getRecords() {
            members.clear()
            DashboardActivity.stop=false
            start=0
            setupList()
    }

    override fun loadApi() {
        if (!DashboardActivity.stop) {
            start = (members.size+1)
            setupList()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun getSearchRecords(data: SearchByCityModel) {

        if (data.success) {
            if (data.members.size > 0) {
                val count= data.totalHead+ data.totalMem
                tvCount.text="Families: ${data.totalHead}. Members: $count"
                for (user in data.members) {
                    members.add(user)
                }

                adapter.notifyDataSetChanged()
                binding.lstFilter.layoutManager?.scrollToPosition(selectedPosition)
                selectedPosition = members.size - 1
                DashboardActivity.stop = false

                if(data.totalHead<=length){
                    DashboardActivity.stop = true
                    Snackbar.make(binding.llParent, "End of $alpha Records", Snackbar.LENGTH_LONG).show()
                }

            } else {
                DashboardActivity.stop = true
                //rootView!!.lstFilter.layoutManager?.scrollToPosition(selectedPosition)
                Snackbar.make(binding.llParent, "End of $alpha Records", Snackbar.LENGTH_LONG).show()
            }
        } else {
            DashboardActivity.stop = false
        }

        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    override suspend fun getFailure(message: String) {
      try{
          DashboardActivity.stop = false
          binding.shimmerViewContainer.stopShimmerAnimation()
          binding.shimmerViewContainer.visibility = View.GONE
          Snackbar.make(binding.llParent, "Something went wrong!", Snackbar.LENGTH_LONG).show()
      }catch (e:Exception){
          e.printStackTrace()
      }

    }

    companion object {
        var alpha:String=""
        var dialog:DialogPlus?=null
    }

    private var selectedPosition = 0
    private var start: Int = 0
    private val length: Int = 5
    private lateinit var city_id:String
    private lateinit var city_name:String

    private val members = ArrayList<Member>()
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode?=null
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var selectedItems: SparseBooleanArray
    private lateinit var animationItemsIndex: SparseBooleanArray
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private val selectedItemCount: Int get() = selectedItems.size()
    private var TAG: String = SearchCityResult::class.java.simpleName
    private val factory: BrowseCityViewModelFactory by instance()
    internal lateinit var browseCityViewModel: BrowseCityViewModel
    override val kodein by kodein()
    private lateinit var tvCount:TextView
    lateinit var binding: FragmentFilterResultBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_result, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()

        actionModeCallback = ActionModeCallback()
        browseCityViewModel = ViewModelProviders.of(this, factory).get(BrowseCityViewModel::class.java)
        browseCityViewModel.ibrowseCityRecordsListener = this

        if (this.arguments != null){
            city_name = this.arguments!!.getString("city_name").toString()
            city_id =  this.arguments!!.getString("city_id").toString()
            Guru.putString("user_city",city_name)
            Guru.putString("user_city_id",city_id)
        }

        members.clear()
        adapter = object : ParallaxRecyclerAdapter<Member>(members) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val member = members[position]
                val holder = viewHolder as SearchCityResult.ViewHolder
                val name=member.firstName

                Coroutines.main {
                 val lastname= browseCityViewModel.getLastName(Integer.parseInt(member.subCastId.toString()))
                    holder.tvName.text= "$name $lastname"
                }

                holder.tvArea.text = member.area
                holder.tvEmail.text = member.emailAddress
                holder.tvMobile.text = member.mobile
                if (member.headId.equals("0")) {
                    holder.tvRole.text = "Family Head"
                } else {
                    holder.tvRole.text = "Member"
                }

                holder.tvUpdate.text="updated "+Utility.changeDateFormat(member.updatedDt,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)

                holder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 1) {
                            val intent: Intent = Intent(activity, FamilyTreeListActivity::class.java)
                            startActivity(intent)
                        } else if (it == 2) {
                            Utility.sendWhatsappMessage(activity as FragmentActivity,member.mobile,"")
                        }else{
                            Toast.makeText(activity, "Clicked $it", Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }
                holder.boomMenuButton.setOnClickListener({ v -> holder.boomMenuButton.boom() })

                holder.iconText.text = name.substring(0, 1)
                holder.itemView.isActivated = selectedItems.get(position, false)
                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return members.size
            }
        }

        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_filter, container, false)
        Log.d(TAG, "City Name: " + city_name)
        val tvTitle = header.findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = city_name

        val edtFilterName = header.findViewById<EditText>(R.id.edt_filter_name)
        edtFilterName.visibility = View.GONE

        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v -> Utility.movetoFragment(activity, BrowseByCityFragment()) }

        val ivExport = header.findViewById<ImageView>(R.id.iv_export)
        ivExport.setOnClickListener {

        }
        tvCount= header.findViewById(R.id.tv_count)

        val ivAtoz = header.findViewById<ImageView>(R.id.iv_atoz)
        ivAtoz.setOnClickListener { v ->
            val adapter = AtoZBottomAdapter(context)
            adapter.setmISortingRecords(this)
            dialog = DialogPlus.newDialog(context!!)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true,900)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog?.show()
        }

        adapter.setParallaxHeader(header, binding.lstFilter)
        binding.lstFilter.layoutManager = LinearLayoutManager(activity)
        binding.lstFilter.adapter = adapter
        adapter.setContext(this)
        DashboardActivity.stop=false
        alpha=""
        setupList()
        return binding.root
    }

    private fun setupList() {
        if (!DashboardActivity.stop) {
            DashboardActivity.stop = true
            val data = SearchByCityData()
            data.start = start.toString()
            data.length = length.toString()
            data.alpha= alpha
            val filterBy = FilterBy()
            filterBy.cityId = city_id
            data.filterBy = filterBy
            if(start==0){
                binding.shimmerViewContainer.startShimmerAnimation()
                binding.shimmerViewContainer.visibility = View.VISIBLE
            }
            browseCityViewModel.fetchRecordsByCity(data)
        }
    }

    private fun applyClickEvents(holder: ViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { onIconClicked(position) }

        holder.llMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + holder.tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }

        holder.messageContainer.setOnClickListener { onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
    }

    private fun applyProfilePicture(holder: ViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic) && member.profilePic.contains("http://")) {
            Glide.with(activity!!).load(member.profilePic)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: ViewHolder, position: Int) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.visibility = View.GONE
            resetIconYAxis(holder.iconBack)
            holder.iconBack.visibility = View.VISIBLE
            holder.iconBack.alpha = 1f
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, true)
                resetCurrentIndex()
            }
        } else {
            holder.iconBack.visibility = View.GONE
            resetIconYAxis(holder.iconFront)
            holder.iconFront.visibility = View.VISIBLE
            holder.iconFront.alpha = 1f
            if (reverseAllAnimations && animationItemsIndex.get(position, false) || currentSelectedIndex == position) {
                FlipAnimator.flipView(activity, holder.iconBack, holder.iconFront, false)
                resetCurrentIndex()
            }
        }
    }

    private fun resetIconYAxis(view: View) {
        if (view.rotationY != 0f) {
            view.rotationY = 0f
        }
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        animationItemsIndex.clear()
    }

    private fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
            animationItemsIndex.delete(pos)
        } else {
            selectedItems.put(pos, true)
            animationItemsIndex.put(pos, true)
        }
        adapter.notifyItemChanged(pos + 1)
    }

    private fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

    private fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun removeData(position: Int) {
        members.removeAt(position)
        resetCurrentIndex()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    private inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        val boomMenuButton: BoomMenuButton = itemView.findViewById(R.id.bmb1)
        var imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvArea: TextView = itemView.findViewById(R.id.tv_area)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var iconContainer: RelativeLayout = itemView.findViewById(R.id.icon_container)
        var iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back)
        var iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front)
        var iconText: TextView = itemView.findViewById(R.id.icon_text)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
        //var llEmail: LinearLayout = itemView.findViewById(R.id.ll_email)
        var tvUpdate:TextView=  itemView.findViewById(R.id.tv_update)
        init {
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View): Boolean {
            onRowLongClicked(adapterPosition)
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility = View.GONE
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions[i])
        }
        adapter.notifyDataSetChanged()
    }


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            // rootView!!.swipe_refresh_layout.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {


            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_delete -> {
                    deleteMessages()
                    mode.finish()
                    return true
                }

                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }

            binding.lstFilter.post { resetAnimationIndex() }
        }
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = selectedItemCount

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private fun enableActionMode(position: Int) {
        toggleSelection(position)
    }


    private fun onIconClicked(position: Int) {
        toggleSelection(position)
    }

    /*private fun onIconImportantClicked(position: Int) {
        val user = users[position]
        message.isImportant = !message.isImportant
        users[position] = message
        adapter!!.notifyDataSetChanged()
    }*/

    private fun onMessageRowClicked(position: Int) {
        if (selectedItemCount > 0) {
            enableActionMode(position)
        } else {
            /*val user = users[position]
            user.isRead = true
            users[position] = user
            adapter!!.notifyDataSetChanged()*/
            val intent=Intent(activity,ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member),members.get(position))
            //intent.putExtra("id",members.get(position)?.id)
            startActivity(intent)
            Utility.fade(activity)
            /*val fragmemnt= FamilyDetailFragment()
            val bundle=Bundle()
            bundle.putString("screen_name",SearchCityResult::class.java.simpleName)
            bundle.putSerializable("user",members.get(position))
            fragmemnt.arguments=bundle
            Utility.movetoFragment(activity,fragmemnt)*/
            //Toast.makeText(activity, "Read: " + position, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }
}
