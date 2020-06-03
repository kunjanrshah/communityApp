package com.krs.community.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.facebook.FacebookSdk
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.QRCodeActivity
import com.krs.community.adapter.LocationAdapter
import com.krs.community.app.AppController
import com.krs.community.app.NotificationBadge
import com.krs.community.listeners.ByFilterListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.SmartFilterResponse
import com.krs.community.utils.*
import com.krs.community.viewmodel.ContactListViewModel
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodelfactory.ContactListViewModelFactory
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import kotlinx.coroutines.Runnable
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class MyContactListFragment : Fragment(), KodeinAware, ByFilterListener, LocationAdapter.SetLocationListner {
    override val kodein by kodein()

    private var rvSearch: RecyclerView? = null
    private var ivNotFound: ImageView? = null
    var StoreContacts = ArrayList<String?>()
    var cursor: Cursor? = null
    var name: String? = null
    var phonenumber: String? = null
    private val RequestPermissionCode = 5
    private val lstMembers: MutableList<Member> = ArrayList()
    private lateinit var adapter: ParallaxRecyclerAdapter<Member>
    private lateinit var tvCount: TextView
    private lateinit var contactListViewModel: ContactListViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel
    private val contactListViewModelFactory: ContactListViewModelFactory by instance<ContactListViewModelFactory>()
    private val profileDetailFactory: ProfileDetailViewModelFactory by instance<ProfileDetailViewModelFactory>()
    private var setLocationDialog: DialogPlus? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_mycontactlist, container, false)
        val mApp = FacebookSdk.getApplicationContext() as AppController
        mApp.firebaseAnalytics(context, MyContactListFragment::class.java.simpleName)
        mApp.facebookAnalytics(context, MyContactListFragment::class.java.simpleName)

        profileDetailViewModel = ViewModelProvider(this, profileDetailFactory).get(ProfileDetailViewModel::class.java)
        contactListViewModel = ViewModelProvider(this, contactListViewModelFactory).get(ContactListViewModel::class.java)
        contactListViewModel.filterListener = this
        rvSearch = root.findViewById<View>(R.id.rv_search) as RecyclerView
        ivNotFound = root.findViewById(R.id.iv_not_found) as ImageView
        AppController.mApplication.start = 0

        adapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<Member>, position: Int) {

                val member = lstMembers[position]
                val holder = viewHolder as MyViewHolder

                if (lstMembers.size > 0) {
                    tvCount.visibility = View.VISIBLE
                    tvCount.text = getString(R.string.members) + " ${lstMembers.size} " + getString(R.string.found)
                } else {
                    tvCount.visibility = View.GONE
                }

                holder.tvName.text = member.firstName
                holder.tvArea.text = member.area
                if (member.gender.equals("Male")) {
                    holder.ivGender.setBackgroundResource(R.drawable.male)
                } else {
                    holder.ivGender.setBackgroundResource(R.drawable.female)
                }
                if (member.status == "2") {
                    holder.ivVerify.visibility = View.VISIBLE
                } else {
                    holder.ivVerify.visibility = View.GONE
                }

                var count = member.membersCount
                if (count != 0) {
                    count += 1
                }
                holder.badge.setNumber(count)

                var code: String? = null
                code = if (!member.memberCode.isNullOrEmpty() && member.memberCode.length > 5) {
                    member.memberCode.substring(0, 5)
                } else {
                    member.memberCode
                }
                if (BuildConfig.FLAVOR == "yadav") {
                    holder.tvCode.text = getString(R.string.yss) + code + "/" + member.id
                } else {
                    holder.tvCode.text = getMemberCode(code)
                }

                if (BuildConfig.FLAVOR == "ghanchi") {
                    holder.llData.visibility = View.VISIBLE
                } else {
                    holder.llData.visibility = View.GONE
                }

                Coroutines.io {
                    if (!member.subCastId.isNullOrEmpty()) {
                        val name = member.firstName + " " + member.fatherName + " " + contactListViewModel.getLastNameById(member.subCastId.trim().toInt())
                        Coroutines.main {
                            viewHolder.tvName.text = name
                        }
                    }

                    if (!member.cityId.isNullOrEmpty()) {
                        val area = member.area + " " + contactListViewModel.getCityNamebyId(member.cityId.trim())
                        Coroutines.main {
                            holder.tvArea.text = area
                        }
                    }

                    if (!member.nativePlaceId.isNullOrEmpty()) {
                        val native = contactListViewModel.getNativeById(Integer.parseInt(member.nativePlaceId.trim()))
                        Coroutines.main {
                            holder.tvNative.text = "Native: $native"
                        }
                    }

                    if (!member.localCommunityId.isNullOrEmpty()) {
                        val name = contactListViewModel.getLocalCommunity(member.localCommunityId)
                        Coroutines.main {
                            viewHolder.tvRegion.text = name
                        }
                    }
                }

                if (member.mobile.isEmpty()) {
                    viewHolder.tvMobile.text = getString(R.string.mobile_not_available)
                    viewHolder.ivMobile.visibility = View.GONE
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                } else {
                    viewHolder.ivMobile.visibility = View.VISIBLE
                    viewHolder.tvMobile.text = member.mobile
                    viewHolder.tvMobile.setTextColor(resources.getColor(R.color.com_facebook_blue))
                }

                if (member.emailAddress.isNullOrEmpty()) {
                    viewHolder.ivEmail.visibility = View.GONE
                    viewHolder.tvEmail.text = getString(R.string.email_not_available)
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.gray_btn_bg_color))
                } else {
                    viewHolder.tvEmail.setTextColor(resources.getColor(R.color.red_btn_bg_color))
                    viewHolder.ivEmail.visibility = View.VISIBLE
                    viewHolder.tvEmail.text = member.emailAddress
                }

                if (member.headId == "0") {
                    holder.tvRole.text = resources.getString(R.string.Family_Head)
                } else {
                    holder.tvRole.text = resources.getString(R.string.Member)
                }



                if (member.updatedDt.isNotEmpty()) {
                    if (member.updatedDt.contains(getString(R.string.zero_date))) {
                        viewHolder.tvUpdate.text = getString(R.string.not_updated)
                    } else {
                        viewHolder.tvUpdate.text = getString(R.string.updated) + " " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                    }
                }

                holder.boomMenuButton.clearBuilders()
                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {
                            createMemberPDF(activity as AppCompatActivity, member, profileDetailViewModel)
                            Handler().post(Runnable {
                                Utility.startSweetProgress(activity, "Exporting ${member.firstName}'s Details", getString(R.string.please_wait))
                            })
                            Handler().postDelayed({
                                Utility.hideSweetProgress()
                            }, 5000)
                        } else if (it == 1) {
                            Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                            return@listener
                            val intent: Intent = Intent(activity, FamilyTreeListActivity::class.java)
                            startActivity(intent)
                        } else if (it == 2) {
                            if (!member.mobile.isNullOrEmpty()) {
                                Utility.sendWhatsAppMessage(activity as AppCompatActivity, member.mobile, getString(R.string.install_app) + BuildConfig.APPLICATION_ID)
                            } else {
                                Toast.makeText(activity, getString(R.string.mobile_not_found), Toast.LENGTH_SHORT).show()
                            }
                        } else if (it == 3) {
                            val mBundle = Bundle()
                            mBundle.putSerializable(getString(R.string.member), member)
                            val intent: Intent = Intent(activity, QRCodeActivity::class.java)
                            intent.putExtras(mBundle)
                            startActivity(intent)
                            // Utility.fade(activity)
                        } else if (it == 4) {
                            shareDetails(activity, viewHolder.tvName.text.toString(), member.mobile, member.emailAddress, viewHolder.tvArea.text.toString(), member.address)
                        } else if (it == 5) {
                            val adapter: LocationAdapter = LocationAdapter(context as AppCompatActivity, member)
                            adapter.setLocationListner(this@MyContactListFragment)
                            setLocationDialog = DialogPlus.newDialog(context)
                                    .setAdapter(adapter)
                                    .setGravity(Gravity.BOTTOM)
                                    .setCancelable(true)
                                    .setExpanded(false, 600)
                                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                                    .create()
                            setLocationDialog?.show()
                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }
                holder.boomMenuButton.setOnClickListener { v: View? -> holder.boomMenuButton.boom() }
                holder.swipe.setLockDrag(true)
                holder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                applyProfilePicture(holder, member)
                applyClickEvents(holder, position, member)
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.filter_result_list, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>): Int {
                return lstMembers.size
            }
        }

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity!!.applicationContext)
        rvSearch?.layoutManager = mLayoutManager
        rvSearch?.itemAnimator = DefaultItemAnimator()

        val header = LayoutInflater.from(activity).inflate(R.layout.header_nonactives, container, false)
        tvCount = header.findViewById(R.id.tv_count)
        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
        val tvTitle = header.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = getString(R.string.mycontacts)
        adapter.setParallaxHeader(header, rvSearch)
        rvSearch?.adapter = adapter
        enableRuntimePermission()

        return root
    }

    private fun userContactList() {
        Coroutines.io {
            Coroutines.main {
                Utility.startSweetProgress(context!!, getString(R.string.fetchingcontacts), getString(R.string.please_wait_contacts))
            }
            getContactsIntoArrayList()
            val jsonObject = JSONObject()
            jsonObject.put(context!!.getString(R.string.user_id), Guru.getString(context!!.getString(R.string.user_id), ""))
            jsonObject.put(context!!.getString(R.string.id), Guru.getString(context!!.getString(R.string.member_id), ""))
            jsonObject.put(context!!.getString(R.string.access_token), Guru.getString(context!!.getString(R.string.access_token), ""))
            val jsonArray = JSONArray(StoreContacts)
            jsonObject.put("mobiles", jsonArray)
            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

            Log.e("updated----", "" + updated)
            contactListViewModel.getContactList(updated)
        }
    }

    private fun getContactsIntoArrayList() {
        cursor = activity!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        StoreContacts.clear()
        while (cursor!!.moveToNext()) {
            name = cursor!!.getString(cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            phonenumber = cursor!!.getString(cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val strNumber = phonenumber.toString().replace(" ", "").replace("+91", "")
            if (strNumber.length == 10) {
                if (!StoreContacts.contains(strNumber)) {
                    StoreContacts.add(strNumber)
                }
            }
        }
        cursor!!.close()
    }

    override fun getMembers(response: SmartFilterResponse) {
        Utility.hideSweetProgress()
        if (response.success) {
            if (response.members.size > 0) {
                lstMembers.clear()
                lstMembers.addAll(response.members)
                adapter.notifyDataSetChanged()
                ivNotFound?.visibility = View.GONE
            } else {
                ivNotFound?.visibility = View.VISIBLE
                rvSearch?.snackbar(getString(R.string.noFoundNonActives), Snackbar.LENGTH_SHORT)
            }
        } else {
            ivNotFound?.visibility = View.VISIBLE
            rvSearch?.snackbar(getString(R.string.noFoundNonActives), Snackbar.LENGTH_SHORT)
        }
    }

    override suspend fun getFailure(message: String) {
        Utility.hideSweetProgress()
        rvSearch?.snackbar(message, Snackbar.LENGTH_SHORT)
    }

    private fun enableRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED || (activity as AppCompatActivity).checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CONTACTS)
                requestPermissions(permissions, RequestPermissionCode)
            } else {
                userContactList()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RequestPermissionCode -> {
                if (grantResults.isNotEmpty()) {
                    val Accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (Accepted) {
                        userContactList()
                    } else {
                        Snackbar.make(view!!, getString(R.string.permissioncontact), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val boomMenuButton: BoomMenuButton = itemView.findViewById(R.id.bmb1)
        val tvArea: TextView = itemView.findViewById(R.id.tv_area)
        val tvRole: TextView = itemView.findViewById(R.id.tv_role)
        val tvMobile: TextView = itemView.findViewById(R.id.tv_mobile)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        var iconBack: RelativeLayout = itemView.findViewById(R.id.icon_back)
        var iconImp: ImageView = itemView.findViewById(R.id.icon_star)
        var iconFront: RelativeLayout = itemView.findViewById(R.id.icon_front)
        var iconText: TextView = itemView.findViewById(R.id.icon_text)
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var imgProfile: ImageView = itemView.findViewById(R.id.icon_profile)
        var tvUpdate: TextView = itemView.findViewById(R.id.tv_update)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        var ivMobile: ImageView = itemView.findViewById(R.id.iv_mobile)
        var ivEmail: ImageView = itemView.findViewById(R.id.iv_email)
        var ivGender: ImageView = itemView.findViewById(R.id.iv_gender)
        var ivVerify: ImageView = itemView.findViewById(R.id.iv_verify)
        var badge: NotificationBadge = itemView.findViewById(R.id.badge)
        var tvCode: TextView = itemView.findViewById(R.id.tv_code)
        var tvNative: TextView = itemView.findViewById(R.id.tv_native)
        var swipe: SwipeRevealLayout = itemView.findViewById(R.id.swipe)
        var tvRegion: TextView = itemView.findViewById(R.id.tv_region)
        var llData: LinearLayout = itemView.findViewById(R.id.ll_data)
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int, member: Member) {

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
                    openImageDialog(activity as AppCompatActivity, path)
                } catch (e: Exception) {
                    e.message
                }
            } else {
                holder.imgProfile.isClickable = false
            }
        }

        holder.messageContainer.setOnClickListener { view -> onMessageRowClicked(position, holder.itemView) }
    }

    private fun onMessageRowClicked(position: Int, v: View) {

        val intent = Intent(activity, ProfileDetailActivity::class.java)
        intent.putExtra(getString(R.string.member), lstMembers.get(position))
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == 102) {
            DashboardActivity.stop = false
            userContactList()
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

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun cancelDialog() {
        setLocationDialog?.dismiss()
    }


}