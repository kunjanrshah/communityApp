package com.krs.community.fragments


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk
import com.krs.community.R
import com.krs.community.activity.ProfileDetailActivity
import com.krs.community.adapter.RecyclerAdapter.ItemClickListener
import com.krs.community.app.AppController
import com.krs.community.interfaces.OnBackPressedListener
import com.krs.community.model.User
import com.krs.community.parallaxrecyclerview.HeaderLayoutManagerFixed
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.utils.AppConstants.EXTRA_POSITION
import com.krs.community.utils.Utility
import com.nightonke.boommenu.BoomMenuButton
import kotlinx.android.synthetic.main.header_detail.view.*
import java.io.Serializable


class FamilyDetailFragment : Fragment(), OnBackPressedListener, ItemClickListener {

    override fun itemClick(id: Int) {
        val intent = Intent(activity, ProfileDetailActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
        Utility.fade(context)
    }

    lateinit var rv_detail: RecyclerView
    lateinit var ll_root: LinearLayout
    lateinit var  user:User;

    companion object {

        const val TAG = "FamilyDetailFragment"
        fun newInstance(adapterPosition: Int): FamilyDetailFragment {
            val bundle = Bundle().apply {
                putInt(EXTRA_POSITION, adapterPosition)
            }

            return FamilyDetailFragment().apply { arguments = bundle }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(com.krs.community.R.layout.fragment_header_detail, container, false)
        (activity as AppCompatActivity).supportActionBar!!.title = "Header Detail"

        ll_root = root.findViewById<LinearLayout>(com.krs.community.R.id.ll_root)
        rv_detail = root.findViewById<RecyclerView>(com.krs.community.R.id.rv_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorPrimary, true)
        }

       Toast.makeText(context,""+user.firstName,Toast.LENGTH_SHORT).show()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = if (arguments != null) (arguments as Bundle).getInt(EXTRA_POSITION)
        else 0

        user = arguments?.getSerializable("user") as User

        rv_detail.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        rv_detail.layoutManager = mLayoutManager
        rv_detail.itemAnimator = DefaultItemAnimator()
        createCardAdapter()
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar!!.hide()
        Handler().postDelayed({
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }, 1000)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar!!.show()
    }

    private fun createCardAdapter() {
        val content = ArrayList<String>()
        for (i in 0..4) {
            content.add("item $i")
        }

        val adapter = object : ParallaxRecyclerAdapter<String>(content) {
            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<String>, i: Int) {
                (viewHolder as HeaderViewHolder).tv_name.text = "Kunjan Shah"
                viewHolder.tv_subtext.text = "Son"
                viewHolder.tv_email.text = "kunjanrshah@gmail.com"
                viewHolder.tv_mobile.text = "9427051418"

                viewHolder.bmb1.clearBuilders()
                for (i in 0 until viewHolder.bmb1.piecePlaceEnum.pieceNumber()) {
                    viewHolder.bmb1.addBuilder(Utility.getTextInsideCircleButtonBuilder())
                }

                viewHolder.bmb1.setOnClickListener {
                    viewHolder.bmb1.boom()
                }
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<String>, i: Int): RecyclerView.ViewHolder {
                return HeaderViewHolder(layoutInflater.inflate(R.layout.row_list_family_detail, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<String>): Int {
                return content.size
            }
        }

        adapter.setOnClickEvent { v, position ->
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra("id",id)
            startActivity(intent)
            Utility.fade(context)
        }

        val layoutManagerFixed = HeaderLayoutManagerFixed(activity)
        rv_detail.layoutManager = layoutManagerFixed
        val header = layoutInflater.inflate(com.krs.community.R.layout.header_detail, rv_detail, false)
        val ll_family_head: LinearLayout

        ll_family_head = header.findViewById(R.id.ll_family_head)
        ll_family_head.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            Utility.fade(context)
        }

        val tv_add: TextView
        tv_add = header.findViewById(R.id.tv_add)
        tv_add.setOnClickListener {
            val intent = Intent(activity, ProfileDetailActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            Utility.fade(context)
        }

        header.img_cancel.setOnClickListener {
            Utility.movetoFragment(activity, SearchListFragment())
        }

        header.bmb.clearBuilders()
        for (i in 0 until header.bmb.piecePlaceEnum.pieceNumber()) {
            header.bmb.addBuilder(Utility.getTextInsideCircleButtonBuilder())
        }

        header.bmb.setOnClickListener {
            header.bmb.boom()
        }

        layoutManagerFixed.setHeaderIncrementFixer(header)
        adapter.isShouldClipView = false
        adapter.setParallaxHeader(header, rv_detail)
        adapter.data = content
        rv_detail.adapter = adapter
    }

    internal class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv_name: TextView
        var tv_subtext: TextView
        var tv_email: TextView
        var tv_mobile: TextView
        var bmb1: BoomMenuButton

        init {
            tv_name = v.findViewById<View>(com.krs.community.R.id.tv_name) as TextView
            tv_subtext = v.findViewById(R.id.tv_subtext)
            tv_subtext.typeface = AppController.mApplication.typeface_bold
            tv_email = v.findViewById(R.id.tv_email)
            tv_mobile = v.findViewById(R.id.tv_mobile)
            bmb1 = v.findViewById(R.id.bmb1)
        }
    }

    override fun onBackPressed() {
        //  animateViewsOut()
    }

}
