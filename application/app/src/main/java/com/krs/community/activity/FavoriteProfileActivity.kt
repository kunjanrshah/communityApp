package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.krs.community.R
import com.krs.community.adapter.LocationAdapter
import com.krs.community.app.SearchLiveo
import com.krs.community.databinding.ActivityFavoriteBinding
import com.krs.community.entities.RoomMember
import com.krs.community.listeners.RoomMemberListener
import com.krs.community.utils.*
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.RoomMemberViewModel
import com.krs.community.viewmodelfactory.ProfileDetailViewModelFactory
import com.krs.community.viewmodelfactory.RoomMemberViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.Normalizer
import java.util.*
import kotlin.collections.ArrayList

class FavoriteProfileActivity : AppCompatActivity() , SearchLiveo.OnSearchListener, KodeinAware, RoomMemberListener,LocationAdapter.SetLocationListner {

    private lateinit var roomMemberViewModel: RoomMemberViewModel
    private lateinit var mBinding:ActivityFavoriteBinding
    private val roomMemberViewModelFactory: RoomMemberViewModelFactory by instance()
    private var mAdapter: FavoriteAdapter? = null
    private var setLocationDialog: DialogPlus? = null
    private var lstMember = ArrayList<RoomMember>()
    override val kodein by kodein()

    companion object {
        private fun removeAccent(text: String?): String {
            val result = Normalizer.normalize(text, Normalizer.Form.NFD)
            return result.replace("[^\\p{ASCII}]".toRegex(), "")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        roomMemberViewModel = ViewModelProvider(this, roomMemberViewModelFactory).get(RoomMemberViewModel::class.java)
        roomMemberViewModel.mRoomMemberListener = this
        onInitView()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        roomMemberViewModel.getRoomMembers()
    }

    override fun changedSearch(text: CharSequence?) {
        if (text != null) {
            mAdapter?.searchMembers(text)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_search) {
            mBinding.searchLiveo.show()
            return true
        } else if(id == android.R.id.home){
            finish()
            Utility.fade(this)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
                mBinding.searchLiveo.resultVoice(requestCode, resultCode, data)
            }
        }
    }


    private fun onInitView(){
        mBinding=  DataBindingUtil.setContentView(this,R.layout.activity_favorite)
        this.onInitToolbar(mBinding.toolbar,getString(R.string.Search),R.drawable.ic_arrow_back_white_24dp)

        mBinding.searchLiveo.with(this).removeMinToSearch().removeSearchDelay().build()
        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun onInitToolbar(toolBar: Toolbar?, title: String?, icon: Int=-1, displayHome: Boolean=true) {
        if (toolBar != null) {
            setSupportActionBar(toolBar)
            val actionBar: ActionBar? = supportActionBar
            if (actionBar != null) {
                actionBar.title = title
                actionBar.setDisplayShowHomeEnabled(displayHome)
                actionBar.setDisplayHomeAsUpEnabled(displayHome)
                if (icon != -1 && displayHome) {
                    toolBar.navigationIcon = ContextCompat.getDrawable(this, icon)
                }
            }
        }
    }


    override fun refreshList() {
        mAdapter = FavoriteAdapter(lstMember as MutableList<RoomMember>)
        mBinding.recyclerView.adapter = mAdapter
    }

    override fun getRoomMembers(response: List<RoomMember>) {
        if (response.size > 0) {
            lstMember.addAll(response)

            mAdapter = FavoriteAdapter(lstMember as MutableList<RoomMember>)
            mBinding.recyclerView.adapter = mAdapter
        }else{
            DashboardActivity.stop = true
            Snackbar.make(mBinding.recyclerView, "No Data Found", Snackbar.LENGTH_LONG).show()
        }

    }

    override suspend fun getFailure(message: String) {
        Utility.displaySnackBarWithBottomMargin(mBinding.recyclerView,message)
    }

    private fun applyProfilePicture(holder: FavoriteAdapter.ViewHolder, path: String) {
        if (!TextUtils.isEmpty(path)) {
            holder.imgProfile.isClickable = true
            val url=resources.getString(R.string.base_url_thumb)+path
            Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(holder.imgProfile)
            holder.imgProfile.colorFilter = null
            holder.iconText.visibility = View.GONE

        } else {
            holder.imgProfile.isClickable = false
            holder.imgProfile.setImageResource(R.drawable.bg_circle)
            holder.imgProfile.setColorFilter(Utility.getRandomMaterialColor(this, "400"))
            holder.iconText.visibility = View.VISIBLE
        }
    }


    inner class FavoriteAdapter(private val mMembers: MutableList<RoomMember>) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
        private val mSearchMembers: ArrayList<RoomMember> = ArrayList()
        init {
            mSearchMembers.addAll(mMembers)
        }

       inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var iconText: TextView = view.findViewById(R.id.icon_text1)
            var tvName: TextView = view.findViewById(R.id.tv_name1)
            var tvArea: TextView = view.findViewById(R.id.tv_area)
            var tvEmail: TextView = view.findViewById(R.id.tv_email)
            var tvMobile: TextView = view.findViewById(R.id.tv_mobile)
            var tvRole: TextView = view.findViewById(R.id.tv_role)
            var iconImp: ImageView = view.findViewById(R.id.icon_star)
            var tvUpdate: TextView = view.findViewById(R.id.tv_update)
            var imgProfile: ImageView = view.findViewById(R.id.icon_profile1)
            var messageContainer: LinearLayout = view.findViewById(R.id.message_container1)
            var iconBack: RelativeLayout = view.findViewById(R.id.icon_back1)
            var iconFront: RelativeLayout = view.findViewById(R.id.icon_front1)
            var boomMenuButton: BoomMenuButton = view.findViewById(R.id.boomMenuButton1)
            var lstFound: RecyclerView = view.findViewById(R.id.lst_found)
            var llMobile: LinearLayout = itemView.findViewById(R.id.ll_mobile)
            var ll_email: LinearLayout = itemView.findViewById(R.id.ll_email)
            var ivGender: ImageView = itemView.findViewById(R.id.iv_gender)

       }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_list_search, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            val member = mMembers[viewHolder.adapterPosition]
            viewHolder.lstFound.visibility=View.GONE
            viewHolder.iconFront.visibility=View.VISIBLE
            viewHolder.iconBack.visibility=View.GONE

            viewHolder.tvName.text = member.firstName
            if(!member.subCastId.isNullOrEmpty()){
                roomMemberViewModel.getLastName(member.subCastId.toInt()).observeForever {
                    viewHolder.tvName.text = member.firstName + " " + it
                }
            }

            if (!member.cityId.isNullOrEmpty()) {
                roomMemberViewModel.getCityNamebyId(member.cityId).observeForever {
                    viewHolder.tvArea.text = member.area + " " + it
                }
            }
           /* viewHolder.tvEmail.text = member.emailAddress
            viewHolder.tvMobile.text = member.mobile*/
            if(member.gender.equals("Male")){
                viewHolder.ivGender.setBackgroundResource(R.drawable.male)
            }else{
                viewHolder.ivGender.setBackgroundResource(R.drawable.female)
            }
            if (member.mobile.isNullOrEmpty()){
                viewHolder.llMobile.visibility = View.GONE
            }else{
                viewHolder.tvMobile.text = member.mobile
            }

            if (member.emailAddress.isNullOrEmpty()){
                viewHolder.ll_email.visibility = View.GONE

            }else{
                viewHolder.tvEmail.text = member.emailAddress
            }

            if (member.headId.equals("0")) {
                viewHolder.tvRole.text = resources.getString(R.string.Family_Head)
            } else {
                viewHolder.tvRole.text = resources.getString(R.string.Member)
            }
            if (!member.updatedDt.isNullOrEmpty()) {
                viewHolder.tvUpdate.text = getString(R.string.Updated) + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
            }

            viewHolder.boomMenuButton.clearBuilders()
            for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                builder?.listener {
                    if (it == 0) {
                        val profileDetailFactory: ProfileDetailViewModelFactory by instance()
                        val profileDetailViewModel = ViewModelProvider(this@FavoriteProfileActivity, profileDetailFactory).get(ProfileDetailViewModel::class.java)
                        createMemberPDF(this@FavoriteProfileActivity, getMemberFromRoomMember(member),profileDetailViewModel)

                        Handler().post(Runnable {
                            Utility.startSweetProgress(this@FavoriteProfileActivity, getString(R.string.ExportingList)+" ${member.firstName}" +getString(R.string.DetailList), getString(R.string.please_wait))
                        })
                        Handler().postDelayed({
                            Utility.hideSweetProgress()
                        }, 5000)


                    }else if(it == 1) {
                        Toast.makeText(this@FavoriteProfileActivity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
                        return@listener
                        val intent: Intent = Intent(this@FavoriteProfileActivity, FamilyTreeListActivity::class.java)
                        startActivity(intent)

                    } else if (it == 2) {
                        if(!member.mobile.isNullOrEmpty()){
                            val toNumber = getString(R.string.number) + member.mobile
                            val text = getString(R.string.InstallApp)+"\n" + "https://play.google.com/store/apps/details?id=com.krs.community"
                            Utility.sendWhatsappMessage(this@FavoriteProfileActivity,toNumber,text)
                        }

                    } else if (it == 3) {

                        val mBundle = Bundle()
                        mBundle.putSerializable(getString(R.string.member), getMemberFromRoomMember(member))
                        val intent: Intent = Intent(this@FavoriteProfileActivity, QRCodeActivity::class.java)
                        intent.putExtras(mBundle)
                        startActivity(intent)
                        Utility.fade(this@FavoriteProfileActivity)
                    } else if (it == 4) {
                        shareDetails(this@FavoriteProfileActivity,viewHolder.tvName.text.toString(), member.mobile.toString(),member.emailAddress.toString(),viewHolder.tvArea.text.toString(), member.address.toString())
                    } else if (it == 5) {
                        val adapter: LocationAdapter = LocationAdapter(this@FavoriteProfileActivity,getMemberFromRoomMember(member))
                        adapter.setLocationListner(this@FavoriteProfileActivity)
                        setLocationDialog = DialogPlus.newDialog(this@FavoriteProfileActivity)
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
            viewHolder.boomMenuButton.setOnClickListener { v -> viewHolder.boomMenuButton.boom() }

            viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
            viewHolder.iconImp.setImageDrawable(ContextCompat.getDrawable(this@FavoriteProfileActivity, R.drawable.ic_star_black_24dp))
            viewHolder.iconImp.setColorFilter(ContextCompat.getColor(this@FavoriteProfileActivity, R.color.icon_tint_selected))

            applyProfilePicture(viewHolder, member.profilePic!!)
            viewHolder.messageContainer.setOnClickListener { view ->
                val intent = Intent(this@FavoriteProfileActivity, ProfileDetailActivity::class.java)
                intent.putExtra(getString(R.string.member), getMemberFromRoomMember(member))
                startActivity(intent)
                Utility.fade(this@FavoriteProfileActivity)
            }

            viewHolder.tvMobile.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                val str = getString(R.string.tel) + viewHolder.tvMobile.text
                intent.data = Uri.parse(str)
                startActivity(intent)
            }

            viewHolder.iconImp.setOnClickListener {
                roomMemberViewModel.getRoomMember(member.id).observeForever {
                        if(it!=null){
                            lstMember.removeAt(position)
                            roomMemberViewModel.deleteRoomMember(member.id)
                        }
                }
            }

            viewHolder.imgProfile.setOnClickListener { view ->
                try {
                    val path = getString(R.string.base_url_original) + "" + member.profilePic
                    Log.d("FavoriteProfileActivity", "path: $path")
                    openImageDialog(this@FavoriteProfileActivity,path)
                } catch (e: Exception) {
                    e.message
                }
            }
        }

        override fun getItemCount(): Int {
            return mMembers.size
        }

        fun searchMembers(charText: CharSequence) {
            var charText = charText
            charText = removeAccent(charText as String).toLowerCase(Locale.getDefault())
            mMembers.clear()
            if (charText.isEmpty()) {
                mMembers.addAll(mSearchMembers)
            } else {
                for (Member in mSearchMembers) {
                    val name = removeAccent(Member.firstName)
                    if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                        mMembers.add(Member)
                    }
                }
            }
            notifyDataSetChanged()
        }
    }

    override fun cancelDialog() {
        setLocationDialog?.dismiss()
    }
}