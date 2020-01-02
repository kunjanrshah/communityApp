package com.krs.community.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.print.PrintAttributes
import android.text.TextUtils
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.FamilyTreeListActivity
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.activity.ProfileDetailActivity.Companion.binding
import com.krs.community.adapter.AtoZBottomAdapter
import com.krs.community.interfaces.ByKeywordListener
import com.krs.community.model.Member
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.utils.FlipAnimator
import com.krs.community.utils.Utility
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.ProfileDetailViewModel
import com.krs.community.viewmodel.SmartSearchViewModel
import com.krs.community.viewmodel.SmartSearchViewModelFactory
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import com.orhanobut.dialogplus.DialogPlus
import com.uttampanchasara.pdfgenerator.CreatePdf
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchListFragment : Fragment(), KodeinAware, ByKeywordListener, ParallaxRecyclerAdapter.OnLoadMore{


    private lateinit var rv_search: RecyclerView
    private lateinit var llRoot: LinearLayout
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var multiSearchView: MultiSearchView
    private lateinit var actionModeCallback: ActionModeCallback
    private var actionMode: ActionMode? = null
    private lateinit var rvAdapter: ParallaxRecyclerAdapter<Member>
    private var selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var animationItemsIndex: SparseBooleanArray = SparseBooleanArray()
    private var reverseAllAnimations = false
    private var currentSelectedIndex = -1
    private var TAG: String? = SearchListFragment::class.qualifiedName
    private lateinit var smartSearchviewModel: SmartSearchViewModel
    private lateinit var profileDetailViewModel: ProfileDetailViewModel

    private val factory: SmartSearchViewModelFactory by instance()
    override val kodein by kodein()
    private val lstMembers = ArrayList<Member>()
    private lateinit var tvRecords: TextView
    private lateinit var searchWord: String
    private var selectedPosition = 0
    private var start: Int = 0
    private val length: Int = 30
    private lateinit var membera: Member
    private var strRole:String  = ""

    private val lstKeyword = ArrayList<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_list, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.white, false)
        }

        smartSearchviewModel = ViewModelProviders.of(this, factory).get(SmartSearchViewModel::class.java)
        smartSearchviewModel.mByKeywordListener = this
        llRoot = rootView.findViewById(R.id.ll_root)
        rv_search = rootView.findViewById(R.id.rv_search)
        rv_search.layoutManager = LinearLayoutManager(activity)
        rv_search.setHasFixedSize(true)
        val header = LayoutInflater.from(activity).inflate(R.layout.header_smart_search, container, false)
        multiSearchView = header.findViewById(R.id.multiSearchView)
        tvRecords = header.findViewById(R.id.tvRecords)

        //multiSearchView?.binding!!.searchViewContainer.get(0).editTextSearch.text.insert(0,"Kunjan")

        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
        actionModeCallback = ActionModeCallback()
        (activity as AppCompatActivity).supportActionBar!!.title = "Smart Search"

        rvAdapter = object : ParallaxRecyclerAdapter<Member>(lstMembers) {
            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<Member>?): Int {
                return lstMembers.size
            }

            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder?, adapter: ParallaxRecyclerAdapter<Member>?, position: Int) {
                val viewHolder: MyViewHolder = viewHolder as MyViewHolder

                var member = lstMembers[position]

                viewHolder.tvName.text = member.firstName
                smartSearchviewModel.getLastName(member.subCastId.toInt()).observeForever {
                    viewHolder.tvName.text = member.firstName + " " + it
                }

                if (!member.cityId.isNullOrEmpty()) {
                    smartSearchviewModel.getCityNamebyId(member.cityId).observeForever {
                        viewHolder.tvArea.text = member.area + " " + it
                    }
                }

                viewHolder.tvEmail.text = member.emailAddress
                viewHolder.tvMobile.text = member.mobile

                if (member.headId.equals("0")) {
                    viewHolder.tvRole.text = "Family Head"
                } else {
                    viewHolder.tvRole.text = "Member"
                }

                if (member.updatedDt.isNotEmpty()) {

                }

                viewHolder.tvUpdate.text = "Updated " + Utility.changeDateFormat(member.updatedDt, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                viewHolder.boomMenuButton.clearBuilders()

                var subCastId = member.subCastId

                for (i in 0 until viewHolder.boomMenuButton.piecePlaceEnum.pieceNumber()) {
                    val builder: TextInsideCircleButton.Builder? = Utility.getTextInsideCircleButtonBuilder()
                    builder?.listener {
                        if (it == 0) {

                            ////////////// Main Detail/////

                            val date= Utility.changeDateFormat(member.birthDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                            val Exdate= Utility.changeDateFormat(member.expireDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)
                            val marriageDate= Utility.changeDateFormat(member.marriageDate,Utility.yyyy_MM_dd,Utility.dd_MM_yyyy)

                            val df = SimpleDateFormat("dd.MM.yyyy 'at' h:mm a")
                            val currentdate = df.format(Calendar.getInstance().time)


                            var Fname = viewHolder.tvName.text.toString()
                            var FatherName = member.fatherName
                            var MotherName = member.motherName
                            var Mobile = member.mobile
                            var Relation = member.relation
                            var State = member.stateId
                            var City = member.city
                            var Area = member.area
                            var Pincode = member.pincode
                            var Address = member.address
                            var Email = member.emailAddress
                            var Gender = member.gender

                           // var header  = "<center><u><b>  Community App&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</b></u></center>"+currentdate
                            var header  = "<center>  Community App </center> <object align=right>"+currentdate+"</object>"
                            var MainDetail = "<b>Main Detail  </b> "
                            var Name = "Name : "
                            var FName = "FatherName : "
                            var MName = "MotherName : "
                            var MNumber = "Mobile : "
                            var email = "Email : "
                            var gender = "Gender : "
                            var Rla = "Relation : "
                            var state = "State : "
                            var city = "City : "
                            var area = "Area : "
                            var pincode = "Pincode : "
                            var address = "Address : "

                            var Test =header+"<br> <br>"+ MainDetail+"<br> <br>"+ Name + Fname +"<br>"+FName+FatherName +"<br>" +MName+ MotherName +"<br>" + MNumber +Mobile + "<br>" +email+Email + "<br>"+gender +Gender +"<br>"+ Rla +Relation + "<br>" + state +State +"<br>" +city +City + "<br>" +area+ Area +"<br>" +pincode+Pincode + "<br>"+address+Address

                            /////////////////////Personal Detail

                            if(member.role.equals("LOCAL_ADMIN")){
                                strRole = "Local Admin"

                            }else if(member.role.equals("SUB_ADMIN")) {
                                strRole = "Sub Admin"

                            }else{
                                strRole = "User"
                            }

                            var Role = "Role : "
                            var BirthDate = "BirthDate : "
                            var Native = "Native : "
                            var ExpireDate = "ExpireDate : "
                            var BloodGroup = "Blood Group : "
                            var Gotra = "Gotra : "
                            var Eduction = "Eduction : "
                            var CurrentActivity = "Current Activity : "
                            var MaritalStatus = "Marital Status : "
                            var MarriageDate = "MarriageDate : "
                            var LocalAddress = "Local Address : "

                            var Personal = "<b> Personal </b>"

                            var nativeid = member.nativePlaceId
                            var bloodGroup = member.bloodGroup
                            var gotraId = member.gotraId
                            var educationId = member.educationId
                            var currentActivityId = member.currentActivityId
                            var maritalStatus = member.maritalStatus
                            var localAddress = member.localAddress


                            var StrPersonal = "<br> <br>"+Personal+"<br> <br>"+ Role + strRole+"<br>"+BirthDate+date +"<br>"+Native+nativeid+"<br>"+ExpireDate+Exdate+"<br>"+BloodGroup+bloodGroup+"<br>"+Gotra+gotraId+"<br>"+Eduction+educationId+"<br>"+CurrentActivity+currentActivityId+"<br>"+MaritalStatus+maritalStatus+"<br>"+MarriageDate+marriageDate+"<br>"+LocalAddress+localAddress

                            ///// Professional

                            var Logo = "Logo : "
                            var ComanyName = "Comany Name : "
                            var WorkCategory = "Work Category : "
                            var WorkSubCategory = "Work Sub Category : "
                            var Occupation = "Occupation : "
                            var WebSiteURl = "WebSite URL : "
                            var WorkDetail = "Work Detail : "
                            var WorkAddress = "Work Address : "

                            var Professional = "<b> Professional </b>"

                            var logo = member.businessLogo
                            var companyName = member.companyName
                            var workcategory = ""
                            var worksubCategory = ""
                            var occupationId = member.occupationId
                            var website = member.website
                            var workDetails = member.workDetails
                            var businessAddress = member.businessAddress

                            var StrProfessional = "<br> <br>"+Professional+"<br> <br>"+ Logo + logo+"<br>"+ComanyName+companyName +"<br>"+WorkCategory+workcategory+"<br>"+WorkSubCategory+worksubCategory+"<br>"+Occupation+occupationId+"<br>"+WebSiteURl+website+"<br>"+WorkDetail+workDetails+"<br>"+WorkAddress+businessAddress


                            ///// Matrimony


                            var AboutMe = "About Me : "
                            var FacebookUrl = "Facebook Profile URL : "
                            var BirthTime = "Birth Time : "
                            var BirthPlace = "Birth Place : "
                            var Hobby = "Hobby : "
                            var Expection = "Expection : "
                            var Weight = "Weight : "
                            var Height = "Height : "

                            var Matrimony = "<b> Matrimony </b> "

                            var about = member.aboutMe
                            var facebookProfile = member.facebookProfile
                            var birthTime = member.birthTime
                            var birthPlace = member.birthPlace
                            var hobby = member.hobby
                            var expectation = member.expectation
                            var weight = member.weight
                            var height = member.height

                            var StrMatrimony ="<br> <br>"+ Matrimony+"<br> <br>"+ AboutMe + about+"<br>"+FacebookUrl+facebookProfile +"<br>"+BirthTime+birthTime+"<br>"+BirthPlace+birthPlace+"<br>"+Hobby+hobby+"<br>"+Expection+expectation+"<br>"+Weight+weight+"<br>"+Height+height


                            var MailString = Test + StrPersonal + StrProfessional +StrMatrimony


                                createPdf(activity,Fname,MailString);


                        }else if(it == 1) {

                            val intent: Intent = Intent(activity, FamilyTreeListActivity::class.java)
                            startActivity(intent)

                        } else if (it == 2) {

                            val text = "Install your Community App\n" + "https://play.google.com/store/apps/details?id=com.krs.community"

                            if (member.mobile != null && member.mobile.length != 0) {
                                val toNumber = "+91" + member.mobile

                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$toNumber&text=$text")
                                startActivity(intent)
                            }

                        } else if (it == 3) {

                            val fragment = ByQRCodeFragment()
                            val mBundle = Bundle()
                            mBundle.putString("Mobile", member.mobile)
                            mBundle.putString("Id", member.id)
                            mBundle.putString("Fname", member.firstName)
                            mBundle.putString("Flag", "1")
                            fragment.setArguments(mBundle)
                            Utility.movetoFragment(activity, fragment)

                        } else if (it == 4) {

                            val text = "Install your Community App\n" + "https://play.google.com/store/apps/details?id=com.krs.community \n \n" + "Name : "+ viewHolder.tvName.text.toString() + "\n" + "Mobile : " + member.mobile + "\n" + "Area : " + viewHolder.tvArea.text.toString() + "\n" + "Address : " + member.address

                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, text)
                            startActivity(Intent.createChooser(intent, "Choose one"))

                        } else {

                            Toast.makeText(activity, "Clicked $it", Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewHolder.boomMenuButton.addBuilder(builder)
                }
                viewHolder.boomMenuButton.setOnClickListener { v -> viewHolder.boomMenuButton.boom() }

                viewHolder.iconText.text = viewHolder.tvName.text.substring(0, 1)
                viewHolder.itemView.isActivated = selectedItems.get(position, false)

                viewHolder.lstFound.setHasFixedSize(true)
                val linearLayoutManager = LinearLayoutManager(activity)
                linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

                viewHolder.lstFound.adapter = FoundListAdapter(activity as AppCompatActivity, member.matches)
                viewHolder.lstFound.layoutManager = linearLayoutManager

                applyIconAnimation(viewHolder, position)
                applyProfilePicture(viewHolder, member)
                applyClickEvents(viewHolder, position)

            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<Member>?, i: Int): RecyclerView.ViewHolder {
                return MyViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_list_search, viewGroup, false))
            }
        }

        val ivAtoz = header.findViewById(R.id.iv_atoz) as ImageView
        ivAtoz.setOnClickListener {

            val adapter: AtoZBottomAdapter = AtoZBottomAdapter(context)
            val dialog = DialogPlus.newDialog(context)
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true, 1200)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create()
            dialog.show()
        }
        multiSearchView.setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
            override fun onTextChanged(index: Int, s: CharSequence) {
                //   Toast.makeText(getActivity(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                lstKeyword.add(s.toString())
                searchWord = s.toString()
                DashboardActivity.stop = false
                getMembersByKeyword()
            }

            override fun onSearchItemRemoved(index: Int) {

                searchWord = ""
                lstKeyword.removeAt(index)

                if (lstKeyword.size > 0) {
                    if (lstKeyword.size == 1) {
                        searchWord = lstKeyword[0]
                    } else if (lstKeyword.size == index) {
                        searchWord = lstKeyword[index - 1]
                    } else {
                        searchWord = lstKeyword[index]
                    }
                }
                if (searchWord.isNotEmpty()) {
                    DashboardActivity.stop = false
                    getMembersByKeyword()
                } else {
                    lstMembers.clear()
                    tvRecords.visibility = View.GONE
                    rvAdapter.notifyDataSetChanged()
                }
            }

            override fun onItemSelected(index: Int, s: CharSequence) {
                searchWord = s.toString()
                DashboardActivity.stop = false
                getMembersByKeyword()
            }
        })

        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener {
            Utility.movetoFragment(activity, DashboardFragment())
        }

        rvAdapter.setParallaxHeader(header, rv_search)
        rv_search.adapter = rvAdapter
        DashboardActivity.stop = false
        return rootView
    }

    private fun createPdf(mContext: FragmentActivity?, fname: String, test: String) {

        var filename = fname
        val pdfDirectory = File(Environment.getExternalStorageDirectory(),"/Community")
        // have the object build the directory structure, if needed.
        pdfDirectory.mkdirs()
        // create a File object for the output file
        val outputFile = File(pdfDirectory, filename)

        if (mContext != null) {
            CreatePdf(mContext)
                    .setPdfName(fname)
                    .openPrintDialog(true)
                    .setContentBaseUrl(null)
                    .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                    .setContent(test)
                    .setFilePath(outputFile.absolutePath)
                    .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                        override fun onFailure(errorMsg: String) {
                            Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(filePath: String) {
                            Toast.makeText(activity, "Pdf Saved at: $filePath", Toast.LENGTH_SHORT).show()
                        }
                    })
                    .create()
        }
    }

    class FoundListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var txtName: TextView = v.findViewById(R.id.txt_name)
    }

    class FoundListAdapter(val context: Context, val list: List<String>) : RecyclerView.Adapter<FoundListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundListViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.found_search, parent, false)
            return FoundListViewHolder(view)
        }

        override fun onBindViewHolder(holder: FoundListViewHolder, position: Int) {
            holder.txtName.text = list.get(position).replace("_", " ")
            holder.txtName.setBackgroundResource(R.drawable.filter_found_search)
            holder.txtName.setTextColor(getColor(context, R.color.black1))
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    private fun getMembersByKeyword() {
        if (!DashboardActivity.stop) {
            lstMembers.clear()
            tvRecords.visibility = View.GONE
            rvAdapter.notifyDataSetChanged()
            DashboardActivity.stop = true
            val mJSONObject = JSONObject()
            mJSONObject.put("start", start)
            mJSONObject.put("length", length)
            mJSONObject.put("filter_by", searchWord)
            val updated = JsonParser().parse(mJSONObject.toString()) as JsonObject
            mShimmerViewContainer.startShimmerAnimation()
            mShimmerViewContainer.visibility = View.VISIBLE
            smartSearchviewModel.getMemberByKeywords(updated)
        }
    }

    override fun loadApi() {
        start = (lstMembers.size + 1)
        getMembersByKeyword()
    }

    override fun getMembers(response: searchByKeywordsResponse) {

        DashboardActivity.stop = false
        mShimmerViewContainer.stopShimmerAnimation()
        mShimmerViewContainer.visibility = View.GONE

        //llRoot.snackbar(response.message,Snackbar.LENGTH_LONG)
        Utility.hideKeyboard(activity)

        if (response.success) {
            lstMembers.clear()
            rv_search.visibility = View.VISIBLE
            start = 0
            for (item in response.member) {
                lstMembers.add(item)
            }
            rvAdapter.notifyDataSetChanged()
            rv_search.layoutManager?.scrollToPosition(selectedPosition)
            selectedPosition = lstMembers.size - 1
            if (Integer.parseInt(response.totalRecords) <= length) {
                DashboardActivity.stop = true
                Snackbar.make(llRoot, "End of the Records", Snackbar.LENGTH_LONG).show()
            }
            if (lstMembers.size > 0) {
                tvRecords.text = "Records found: " + response.totalRecords
                tvRecords.visibility = View.VISIBLE
            } else {
                tvRecords.visibility = View.GONE
                DashboardActivity.stop = true
            }
        } else {
            rv_search.visibility = View.GONE
            tvRecords.visibility = View.GONE
        }
    }

    override fun getFailure(message: String) {
        Log.d(TAG, "getFailure: $message")
        activity?.runOnUiThread {
            if (mShimmerViewContainer.isAnimationStarted) {
                mShimmerViewContainer.stopShimmerAnimation()
            }
            mShimmerViewContainer.visibility = View.GONE
            tvRecords.visibility = View.GONE
            rv_search.visibility = View.GONE
            DashboardActivity.stop = false
        }

        llRoot.snackbar(message, Snackbar.LENGTH_LONG)
        Utility.hideKeyboard(activity)
    }

    private fun getSelectedItemCount(): Int {
        return selectedItems.size()
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

        rvAdapter.notifyItemChanged(pos + 1)
    }

    private fun toggleSelection(position: Int) {
        toggleSelected(position)
        val count = getSelectedItemCount()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    fun resetAnimationIndex() {
        reverseAllAnimations = false
        if (animationItemsIndex != null) {
            animationItemsIndex.clear()
        }
    }

    private fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    private fun removeData(position: Int) {
        lstMembers.removeAt(position)
        resetCurrentIndex()
    }

    private fun deleteMessages() {
        resetAnimationIndex()
        val selectedItemPositions = getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            removeData(selectedItemPositions.get(i))
        }
        rvAdapter.notifyDataSetChanged()
    }


    private fun onIconClicked(position: Int) {
        if (actionMode == null) {
            actionMode = activity?.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }

    private fun onMessageRowClicked(position: Int, v: View) {

        if (getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra(getString(R.string.member), lstMembers.get(position))
            startActivity(intent)
            Utility.fade(activity)
            //  Toast.makeText(activity,""+position,Toast.LENGTH_SHORT).show()
            /*val fragmentTransaction = initFragmentTransaction(v)
            fragmentTransaction.commitAllowingStateLoss()*/
        }
    }

    private fun applyClickEvents(holder: MyViewHolder, position: Int) {
        holder.iconContainer.setOnClickListener { view -> onIconClicked(position) }

        holder.messageContainer.setOnClickListener { view -> onMessageRowClicked(position, holder.itemView) }

        /*holder.messageContainer.setOnLongClickListener { view ->

            enableActionMode(position)

            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }*/
    }

    private fun applyProfilePicture(holder: MyViewHolder, member: Member) {
        if (!TextUtils.isEmpty(member.profilePic)) {
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

    private fun applyIconAnimation(holder: MyViewHolder, position: Int) {
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

    fun clearSelections() {
        reverseAllAnimations = true
        selectedItems.clear()
        rvAdapter.notifyDataSetChanged()
    }

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }


    inner class MyViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {
        var iconText: TextView = view.findViewById(R.id.icon_text1)
        var tvName: TextView = view.findViewById(R.id.tv_name1)
        var tvArea: TextView = view.findViewById(R.id.tv_area)
        var tvEmail: TextView = view.findViewById(R.id.tv_email)
        var tvMobile: TextView = view.findViewById(R.id.tv_mobile)
        var tvRole: TextView = view.findViewById(R.id.tv_role)
        var tvUpdate: TextView = view.findViewById(R.id.tv_update)
        var imgProfile: ImageView = view.findViewById(R.id.icon_profile1)
        var messageContainer: LinearLayout = view.findViewById(R.id.message_container1)
        var iconContainer: RelativeLayout = view.findViewById(R.id.icon_container1)
        var iconBack: RelativeLayout = view.findViewById(R.id.icon_back1)
        var iconFront: RelativeLayout = view.findViewById(R.id.icon_front1)
        var boomMenuButton: BoomMenuButton = view.findViewById(R.id.boomMenuButton1)
        var lstFound: RecyclerView = view.findViewById(R.id.lst_found)


        init {
            view.setOnLongClickListener(this)
        }

        override fun onLongClick(view: View): Boolean {
            enableActionMode(adapterPosition)
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            return true
        }
    }

    /* private fun setupList() {

         rv_search.layoutManager = LinearLayoutManager(activity)
         rv_search.adapter = rvAdapter
         rv_search.setHasFixedSize(true)

         Handler().postDelayed({
             // stop animating Shimmer and hideOverlay the layout
             mShimmerViewContainer.stopShimmerAnimation()
             mShimmerViewContainer.visibility = View.GONE
         }, 3000)
     }*/

    /* private fun getInbox() {
         swipeRefreshLayout.isRefreshing = true
         messages.clear()

         for (i in 0..19) {
             val message = Message()
             message.id = 1
             message.isImportant = false
             message.message = "Now android supports multiple voice recogonization"
             message.picture = "https://api.androidhive.info/json/google.png"
             message.isRead = false
             message.timestamp = "10:30 AM"
             message.from = "Google Alerts"
             message.subject = "Google Alert - android"
             message.color = Utility.getRandomMaterialColor(activity!!, "400")
             messages.add(message)
         }

         rvAdapter.notifyDataSetChanged()
         swipeRefreshLayout.isRefreshing = false
     }*/

    override fun onStart() {
        super.onStart()
        mShimmerViewContainer.startShimmerAnimation()
        /*Handler().postDelayed({
            multiSearchView!!.binding.imageViewSearch.performClick()
        }, 100)*/
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).supportActionBar!!.hide()
        /*Handler().postDelayed({
            Utility.hideKeyboard(activity)
        }, 1000)*/
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar!!.show()
        mShimmerViewContainer.stopShimmerAnimation()
        super.onStop()
        Handler().postDelayed({
            Utility.hideKeyboard(activity)
        }, 500)
    }

    /* private fun initFragmentTransaction(view: View): FragmentTransaction? {

         val adapterPosition = rv_search!!.getChildAdapterPosition(view)
         val detailsFragment = FamilyDetailActivity.newInstance(adapterPosition)
          val transaction = fragmentManager?.beginTransaction()
                 ?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                 ?.replace(R.id.container_body, detailsFragment, FamilyDetailActivity.TAG)
                 ?.addToBackStack(null)

         return transaction
     }*/


    private inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_action_mode, menu)

            // disable swipe refresh if action mode is enabled
            // swipeRefreshLayout.isEnabled = false
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            /* ViewGroup   decorView = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(R.id.action_mode_bar);
            decorView.setBackgroundColor(getResources().getColor(R.color.colorBG));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,true);
            }*/

            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.action_delete -> {
                        // delete all the selected messages
                        deleteMessages()
                        mode.finish()
                        true
                    }

                    else -> false
                }

        override fun onDestroyActionMode(mode: ActionMode) {
            clearSelections()
            //  swipeRefreshLayout.isEnabled = true
            //actionMode = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(activity, R.color.colorBG, false)
            }
            rv_search.post {
                resetAnimationIndex()
                // mAdapter.notifyDataSetChanged();
            }
        }
    }

    private fun enableActionMode(position: Int) {

        if (actionMode == null) {
            actionMode = activity!!.startActionMode(actionModeCallback)!!
        }
        toggleSelection(position)
    }


}
