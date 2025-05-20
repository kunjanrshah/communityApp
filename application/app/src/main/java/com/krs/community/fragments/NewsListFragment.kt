package com.krs.community.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.squti.guru.Guru
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.listeners.NewsListener
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter
import com.krs.community.responses.News
import com.krs.community.responses.NewsResponse
import com.krs.community.utils.Utility
import com.krs.community.utils.Utility.watchYoutubeVideo
import com.krs.community.viewmodel.NewsViewModel
import com.krs.community.viewmodelfactory.NewsModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NewsListFragment : Fragment(), KodeinAware, NewsListener {

    private lateinit var listView: RecyclerView
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var adapter: ParallaxRecyclerAdapter<News>
    private var lstNews = ArrayList<News>()
    override val kodein by kodein()

    private lateinit var newsViewModel: NewsViewModel
    private val factory: NewsModelFactory by instance<NewsModelFactory>()

//    private val staticJsonData = """
//        {
//  "success": true,
//  "message": "Events retried successfully.",
//  "data": [
//    {
//      "id": "1",
//      "title": "Discussion with Bhavesh",
//      "description": "Discussion with Bhavesh for community app",
//      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
//      "profile_pic": null,
//      "event_date": "1970-01-01",
//      "lat": "22.976056",
//      "lng": "72.60176249999995",
//      "created_dt": "1574616197",
//      "youtube_url": [
//        "https://www.youtube.com/watch?v=EExSSotojVI"
//      ],
//      "images": [
//        "https://hips.hearstapps.com/hmg-prod/images/wisteria-in-bloom-royalty-free-image-1653423554.jpg?crop=0.685xw:1.00xh;0.112xw,0&resize=980:*"
//      ]
//    },
//    {
//      "id": "2",
//      "title": "Test",
//      "description": "Test",
//      "location": "Ahmedabad, Gujarat, India",
//      "profile_pic": null,
//      "event_date": "2019-10-12",
//      "lat": "23.022505",
//      "lng": "72.57136209999999",
//      "created_dt": "1574616870",
//      "youtube_url": [
//        "https://www.youtube.com/watch?v=EExSSotojVI"
//      ],
//      "images": [
//        "https://hips.hearstapps.com/hmg-prod/images/vibrant-pink-and-white-summer-flowering-cosmos-royalty-free-image-1653499726.jpg?crop=0.66541xw:1xh;center,top&resize=980:*"
//      ]
//    },
//    {
//      "id": "3",
//      "title": "Discussion with Kunjan",
//      "description": "Discussion with kunjan for community app",
//      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
//      "profile_pic": null,
//      "event_date": "1970-01-01",
//      "lat": "22.976056",
//      "lng": "72.60176249999995",
//      "created_dt": "1574616197",
//      "youtube_url": [
//        "https://www.youtube.com/watch?v=EExSSotojVI"
//      ],
//      "images": [
//        "https://hips.hearstapps.com/hmg-prod/images/door-shaded-by-bougainvillea-porquerolles-france-royalty-free-image-1653423252.jpg?crop=0.668xw:1.00xh;0.165xw,0&resize=980:*"
//      ]
//    },
//    {
//      "id": "4",
//      "title": "testing news",
//      "description": "testing news event for community app",
//      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
//      "profile_pic": null,
//      "event_date": "1970-01-01",
//      "lat": "22.976056",
//      "lng": "72.60176249999995",
//      "created_dt": "1574616197",
//      "youtube_url": [
//        "https://www.youtube.com/watch?v=EExSSotojVI"
//      ],
//      "images": [
//        "https://hips.hearstapps.com/hmg-prod/images/gardenia-royalty-free-image-1580854928.jpg?crop=1.00xw:0.796xh;0,0.0851xh&resize=980:*"
//      ]
//    },
//    {
//      "id": "5",
//      "title": "JSON format",
//      "description": "testing news event for community app",
//      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
//      "profile_pic": null,
//      "event_date": "1970-01-01",
//      "lat": "22.976056",
//      "lng": "72.60176249999995",
//      "created_dt": "1574616197",
//      "youtube_url": [
//        "https://www.youtube.com/watch?v=EExSSotojVI"
//      ],
//      "images": [
//        "https://hips.hearstapps.com/hmg-prod/images/gardenia-royalty-free-image-1580854928.jpg?crop=1.00xw:0.796xh;0,0.0851xh&resize=980:*"
//      ]
//    }
//  ],
//  "totalRecords": 5
//}
//        """

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_news, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.bg_gray, false)
        }

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.firebaseAnalytics(context, NewsListFragment::class.simpleName)
        mApp.facebookAnalytics(context, NewsListFragment::class.simpleName)

        newsViewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
        newsViewModel.mNewsListener = this

        listView = rootView.findViewById(R.id.list)
        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
        listView.setHasFixedSize(true)
        listView.itemAnimator = DefaultItemAnimator()
        listView.layoutManager = mLayoutManager
        val header = LayoutInflater.from(activity).inflate(R.layout.header_news, container, false)

        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }

        adapter = object : ParallaxRecyclerAdapter<News>(lstNews) {

            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<News>, position: Int) {

                val item: News = lstNews.get(position)
                val holder = viewHolder as FeedListViewHolder
                holder.name.text = item.title
                val eDate = Utility.changeDateFormat(item.eventDate, Utility.yyyy_MM_dd, Utility.ddMMyyyy)
                holder.txtExpire.text = eDate
                val times = 1578140965000
                // Converting timestamp into x ago format
                val timeAgo = DateUtils.getRelativeTimeSpanString(times, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
                holder.timestamp.text = timeAgo
                // Check for empty status message
                if (!TextUtils.isEmpty(item.description)) {
                    holder.statusMsg.text = item.description
                    holder.statusMsg.visibility = View.VISIBLE
                } else {
                    // status is empty, remove from view
                    holder.statusMsg.visibility = View.GONE
                }

                // Checking for null feed url
                val url_ = "http://bit.ly/kunjan1"
                // item.youtubeUrl[0]
                //  if (item.youtubeUrl != null) {
                holder.url.text = Html.fromHtml("<a href=\"" + url_ + "\">" + url_ + "</a> ")
                // Making url clickable
                holder.url.movementMethod = LinkMovementMethod.getInstance()
                holder.url.visibility = View.VISIBLE
                /*} else { // url is null, remove from the view
                    holder.url.visibility = View.GONE
                }*/

                // user profile pic
                val imageUrl = "https://d12m9erqbesehq.cloudfront.net/wp-content/uploads/sites/2/2024/02/27231254/fitness-event-at-community-park-1.jpg"
                Glide.with(activity!!).load(item.profilePic).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.profilePic)
                //   holder.profilePic.setImageUrl(item.profilePic, imageLoader)
                // Feed image
                // if (item.images != null) {
                Glide.with(activity!!)
                    .load(if (item.images.isNullOrEmpty()) "" else item.images[0])
                    .thumbnail(0.5f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).centerCrop() )
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.feedImageView)
                //holder.feedImageView.setImageUrl(item.images[0], imageLoader)
                holder.feedImageView.visibility = View.VISIBLE

                /*holder.feedImageView.setResponseObserver(object : FeedImageView.ResponseObserver {
                    override fun onError() {}
                    override fun onSuccess() {}
                })*/
                /*} else {
                    holder.feedImageView.visibility = View.GONE
                }*/
            }

            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<News>, i: Int): RecyclerView.ViewHolder {
                return FeedListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.feed_item, viewGroup, false))
            }

            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<News>): Int {
                return lstNews.size
            }
        }
        adapter.setParallaxHeader(header, listView)
        listView.adapter = adapter

        val jsonObject = JSONObject()
        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
        jsonObject.put("page", "1")
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
        newsViewModel.getNewsSearch(updated)
        Handler().postDelayed({
            mShimmerViewContainer.stopShimmerAnimation()
            mShimmerViewContainer.visibility = View.GONE
        }, 4000)
        mShimmerViewContainer.startShimmerAnimation()
        mShimmerViewContainer.visibility = View.VISIBLE
        return rootView
    }

    internal class FeedListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var timestamp: TextView = itemView.findViewById(R.id.timestamp)
        var statusMsg: TextView = itemView.findViewById(R.id.txtStatusMsg)
        var txtExpire: TextView = itemView.findViewById(R.id.txt_expire)
        var url: TextView = itemView.findViewById(R.id.txtUrl)
        var profilePic: ImageView = itemView.findViewById(R.id.profilePic)
        var feedImageView: ImageView = itemView.findViewById(R.id.feedImage1)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        mShimmerViewContainer.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
        mShimmerViewContainer.stopShimmerAnimation()
    }

    override fun getNewsList(response: NewsResponse) {
        mShimmerViewContainer.stopShimmerAnimation()
        mShimmerViewContainer.visibility = View.GONE

        if (response.success) {
            if (response.data.size > 0) {
                lstNews.clear()
                lstNews.addAll(response.data)
                adapter.notifyDataSetChanged()
            } else {
                Utility.displaySnackBarWithBottomMargin(listView, response.message)
            }
        }
    }

    override fun getFailure(message: String) {
    }
}