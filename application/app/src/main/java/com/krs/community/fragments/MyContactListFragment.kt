package com.krs.community.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.repositories.ContactListRepository
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.*
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class MyContactListFragment : Fragment(), KodeinAware, ParallaxRecyclerAdapter.OnLoadMore, RoomMemberListener {
    override val kodein by kodein()

    var rv_search: RecyclerView? = null
    var StoreContacts = ArrayList<String?>()
    var arrayAdapter: ArrayAdapter<String?>? = null
    var cursor: Cursor? = null
    var name: String? = null
    var phonenumber: String? = null
    var completableJob: CompletableJob? = null
    var contactListRepository = ContactListRepository(ApiServices())
    private val RequestPermissionCode = 5
    private val lstMembers: MutableList<Member> = ArrayList()
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var tvCount: TextView
    private lateinit var selectedItems: SparseBooleanArray
    private var currentSelectedIndex = -1
    private var reverseAllAnimations = false
    private var actionMode: ActionMode? = null
    private lateinit var actionModeCallback: ActionModeCallback
    private lateinit var roomMemberViewModel: RoomMemberViewModel

    private lateinit var animationItemsIndex: SparseBooleanArray
    private val roomMemberFactory: RoomMemberViewModelFactory by instance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_mycontactlist, container, false)
        val mApp = FacebookSdk.getApplicationContext() as AppController
        mApp.FirebaseAnalytics(context, MyContactListFragment::class.java.simpleName)

        roomMemberViewModel = ViewModelProvider(this, roomMemberFactory).get(RoomMemberViewModel::class.java)
        roomMemberViewModel.mRoomMemberListener= this

        selectedItems = SparseBooleanArray()
        animationItemsIndex = SparseBooleanArray()
        actionModeCallback = ActionModeCallback()


        rv_search = root.findViewById<View>(R.id.rv_search) as RecyclerView


        adapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val member = lstMembers[position]
                val holder = viewHolder as MyViewHolder

                holder.tvName.text = member.firstName
                holder.tvArea.text = member.area
                holder.tvAddr.text = member.address



                if (member.mobile.isEmpty()){
                    viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                    viewHolder.ivMobile.visibility = View.GONE
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                }else{
                    viewHolder.ivMobile.visibility = View.VISIBLE
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.com_facebook_blue))
                }

                if (member.emailAddress.isEmpty()){
                    viewHolder.ivEmail.visibility = View.GONE
                    viewHolder.tvEmail.text = getString(R.string.email_not_available)
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                }else{
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.red_btn_bg_color))
                    viewHolder.ivEmail.visibility = View.VISIBLE
                    viewHolder.tvEmail.text = member.emailAddress
                }


                holder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                viewHolder.itemView.isActivated = selectedItems.get(position, false)

                applyIconAnimation(holder, position)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position,member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.list_row_nonactives, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity!!.applicationContext)
        rv_search?.layoutManager = mLayoutManager
        rv_search?.itemAnimator = DefaultItemAnimator()



        val header = LayoutInflater.from(activity).inflate(R.layout.header_nonactives, container, false)
        tvCount = header.findViewById(R.id.tv_count)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }


        adapter.setParallaxHeader(header, rv_search)
        adapter.setContext(this)
        rv_search?.adapter = adapter
        EnableRuntimePermission()

        return root
    }

    private fun GetUserContactList() {

        Utility.startSweetProgress(context!!,getString(R.string.app_name),"Fetching Your Contacts")

        GetContactsIntoArrayList()
     /*   arrayAdapter = ArrayAdapter(
                context!!, R.layout.contact_items_listview, R.id.textView, StoreContacts
        )
        listView!!.adapter = arrayAdapter*/

        val jsonObject = JSONObject()
        jsonObject.put(context!!.getString(R.string.user_id), Guru.getString(context!!.getString(R.string.user_id), ""))
        jsonObject.put(context!!.getString(R.string.id), Guru.getString(context!!.getString(R.string.member_id), ""))
        jsonObject.put(context!!.getString(R.string.access_token), Guru.getString(context!!.getString(R.string.access_token), ""))
        val jsonArray = JSONArray(StoreContacts)
        jsonObject.put("mobiles", jsonArray)
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

        Log.e("updated----",""+updated);

        getContactList(context!!,updated)
    }

    fun getContactList(context: Context, jsonObject: JsonObject) {

        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = contactListRepository.getContactList(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {

                            Utility.hideSweetProgress()

                            Log.e("Frist Time", " success ")
                            if (response.success) {
                                if (response.members.size > 0) {
                                    lstMembers.clear()

                                    lstMembers.addAll(response.members)
                                    adapter.notifyDataSetChanged()
                                }

                                Log.e("Frist Time", " success ")
                                Log.e("members", ""+ response.members)

                            }
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        Utility.hideSweetProgress()

                    }
                } catch (e: NoInternetException) {

                    e.message?.let {
                        Utility.hideSweetProgress()
                    }
                } catch (e: Exception) {

                    e.message?.let {
                        Utility.hideSweetProgress()

                        Log.e("Exception--", "" + e.toString());
                    }
                }
                thejob.complete()
            }
        }
    }
    fun GetContactsIntoArrayList() {
        cursor = activity!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        while (cursor!!.moveToNext()) {
            name = cursor!!.getString(cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

            phonenumber = cursor!!.getString(cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            var strNumber = phonenumber.toString().replace(" ","").replace("+91","");

            if (strNumber.length ==10){
                StoreContacts.add(strNumber)
            }
        }
        cursor!!.close()
    }

    fun EnableRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED || (activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CONTACTS)
                requestPermissions(permissions, RequestPermissionCode)
            }else{

                GetUserContactList()

            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {RequestPermissionCode ->
        {
            if (grantResults.size > 0) {
                val ContactAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (ContactAccepted){

                    GetUserContactList()


                }else {
                    Snackbar.make(view!!, "Permission Canceled, Now your application cannot access CONTACTS.", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        }
    }

    override fun loadApi() {

    }
    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var iconText: TextView = view.findViewById(R.id.icon_text)
        var tvName: TextView = view.findViewById(R.id.tv_name)
        var imgProfile: ImageView = view.findViewById(R.id.icon_profile)
        var messageContainer: LinearLayout = view.findViewById(R.id.message_container)
        var iconBack: RelativeLayout = view.findViewById(R.id.icon_back)
        var iconFront: RelativeLayout = view.findViewById(R.id.icon_front)
        var tvArea: TextView = view.findViewById(R.id.tv_area)
        var tvMobile: TextView = view.findViewById(R.id.tv_mobile)
        var tvEmail: TextView = view.findViewById(R.id.tv_email)
        var tvAddr: TextView = view.findViewById(R.id.tv_addr)
        var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
        var ll_email: LinearLayout = itemView.findViewById(R.id.ll_email)
        var ivMobile: ImageView = itemView.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = itemView.findViewById(R.id.iv_email)
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int, member: Member) {

        holder.messageContainer.setOnClickListener { view: View? -> onMessageRowClicked(position) }
        holder.messageContainer.setOnLongClickListener { view: View ->
            onRowLongClicked(position)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            true
        }
        holder.tvMobile.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val str = "tel:" + holder.tvMobile.text
            intent.data = Uri.parse(str)
            startActivity(intent)
        }
        holder.imgProfile.setOnClickListener {
            if (member.profilePic.isNotEmpty()) {
                holder.imgProfile.isClickable = true
                try {
                    val path = getString(R.string.base_url_original) + "" + member.profilePic
                    Log.d("NonActiveFragment", "path: $path")
                    openImageDialog(activity as AppCompatActivity,path)
                } catch (e: Exception) {
                    e.message
                }
            }else{
                holder.imgProfile.isClickable = false
            }
        }

    }

    fun applyProfilePicture(holder: MyViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
            if (member.profilePic.isNotEmpty()) {
                holder.imgProfile.isClickable = true
                val path = getString(R.string.base_url_thumb) + "" + member.profilePic
                Log.d("NonActives", "path: $path")
                try {
                    Glide.with(AppController.mApplication).load(path).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(holder.imgProfile)
                } catch (e: Exception) {
                    e.message
                }
            }
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(activity!!, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
        if (selectedItems[position, false]) {
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
            if (reverseAllAnimations && animationItemsIndex[position, false] || currentSelectedIndex == position) {
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

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        actionMode?.finish()
        selectedItems.clear()
    }

    private fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex.clear()
        }
    }

    private fun getSelectedItems(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun toggleSelected(pos: Int) {
        currentSelectedIndex = pos
        if (selectedItems[pos, false]) {
            selectedItems.delete(pos)
            animationItemsIndex.delete(pos)
        } else {
            selectedItems.put(pos, true)
            animationItemsIndex.put(pos, true)
        }
        adapter.notifyItemChanged(pos+ 1)
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = getSelectedItemCount()
        if (count <= 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    private fun getSelectedItemCount(): Int {
        return selectedItems.size()
    }

    private fun onMessageRowClicked(position: Int) {
        if (getSelectedItemCount() > 0) {
            enableActionMode(position)
        }
    }

    private fun onRowLongClicked(position: Int) {
        enableActionMode(position)
    }

    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_non_actives, menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.action_activate -> {
                        val selectedItemPositions = getSelectedItems()
                        SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(getString(R.string.you_sure))
                                .setContentText(getString(R.string.Approved) + " ${selectedItemPositions.size}" + " Profiles!")
                                .setConfirmText(getString(R.string.YesApprovenon))
                                .setCancelText(getString(R.string.no))
                                .setConfirmClickListener {
                                    it.dismiss()

                                    val jsonObject = JSONObject()
                                    jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
                                    jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
                                    jsonObject.put("status", "1")

                                    var Ids = ""
                                    for (index in selectedItemPositions) {
                                        Ids += lstMembers[index].id + ","
                                    }

                                    Ids = Ids.substring(0, Ids.length - 1)
                                    jsonObject.put(getString(R.string.idList), Ids)
                                    val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

                                    Utility.startSweetProgress(activity,getString(R.string.Restricted),getString(R.string.loading))
                                    roomMemberViewModel.changeStatus(updated)
                                }
                                .setCancelClickListener {
                                    it.dismiss()
                                }
                                .show()

                        true
                    }
                    R.id.action_select_all -> {
                        clearSelections()
                        for (i in lstMembers.indices) {
                            enableActionMode(i)
                        }
                        true
                    }
                    else -> false
                }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rv_search?.post {
                resetAnimationIndex()
            }
        }

        fun clearSelections() {
            reverseAllAnimations = true
            selectedItems.clear()
            adapter.notifyDataSetChanged()
        }
    }

    override fun refreshList() {
        adapter.notifyDataSetChanged()

    }

    override fun getRoomMembers(response: List<RoomMember>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getFailure(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}