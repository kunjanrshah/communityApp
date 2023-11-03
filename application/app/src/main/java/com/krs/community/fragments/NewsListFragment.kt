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

    private val staticJsonData = """
        {
  "success": true,
  "message": "Events retried successfully.",
  "data": [
    {
      "id": "1",
      "title": "Discussion with Bhavesh",
      "description": "Discussion with Bhavesh for community app",
      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
      "profile_pic": null,
      "event_date": "1970-01-01",
      "lat": "22.976056",
      "lng": "72.60176249999995",
      "created_dt": "1574616197",
      "youtube_url": [
        "https://www.youtube.com/watch?v=EExSSotojVI"
      ],
      "images": [
        "https://hips.hearstapps.com/hmg-prod/images/wisteria-in-bloom-royalty-free-image-1653423554.jpg?crop=0.685xw:1.00xh;0.112xw,0&resize=980:*"
      ]
    },
    {
      "id": "2",
      "title": "Test",
      "description": "Test",
      "location": "Ahmedabad, Gujarat, India",
      "profile_pic": null,
      "event_date": "2019-10-12",
      "lat": "23.022505",
      "lng": "72.57136209999999",
      "created_dt": "1574616870",
      "youtube_url": [
        "https://www.youtube.com/watch?v=EExSSotojVI"
      ],
      "images": [
        "https://hips.hearstapps.com/hmg-prod/images/vibrant-pink-and-white-summer-flowering-cosmos-royalty-free-image-1653499726.jpg?crop=0.66541xw:1xh;center,top&resize=980:*"
      ]
    },
    {
      "id": "3",
      "title": "Discussion with Kunjan",
      "description": "Discussion with kunjan for community app",
      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
      "profile_pic": null,
      "event_date": "1970-01-01",
      "lat": "22.976056",
      "lng": "72.60176249999995",
      "created_dt": "1574616197",
      "youtube_url": [
        "https://www.youtube.com/watch?v=EExSSotojVI"
      ],
      "images": [
        "https://hips.hearstapps.com/hmg-prod/images/door-shaded-by-bougainvillea-porquerolles-france-royalty-free-image-1653423252.jpg?crop=0.668xw:1.00xh;0.165xw,0&resize=980:*"
      ]
    },
    {
      "id": "4",
      "title": "testing news",
      "description": "testing news event for community app",
      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
      "profile_pic": null,
      "event_date": "1970-01-01",
      "lat": "22.976056",
      "lng": "72.60176249999995",
      "created_dt": "1574616197",
      "youtube_url": [
        "https://www.youtube.com/watch?v=EExSSotojVI"
      ],
      "images": [
        "https://hips.hearstapps.com/hmg-prod/images/gardenia-royalty-free-image-1580854928.jpg?crop=1.00xw:0.796xh;0,0.0851xh&resize=980:*"
      ]
    },
    {
      "id": "5",
      "title": "JSON format",
      "description": "testing news event for community app",
      "location": "Isanpur, Ahmedabad, Gujarat 380043, India",
      "profile_pic": null,
      "event_date": "1970-01-01",
      "lat": "22.976056",
      "lng": "72.60176249999995",
      "created_dt": "1574616197",
      "youtube_url": [
        "https://www.youtube.com/watch?v=EExSSotojVI"
      ],
      "images": [
        "https://hips.hearstapps.com/hmg-prod/images/gardenia-royalty-free-image-1580854928.jpg?crop=1.00xw:0.796xh;0,0.0851xh&resize=980:*"
      ]
    }
  ],
  "totalRecords": 5
}
        """
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
                val profilePic = "https://api.androidhive.info/feed/img/time.png"

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
                val imageUrl = "https://api.androidhive.info/feed/img/cosmos.jpg"
                Glide.with(activity!!).load(profilePic).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.profilePic)
                //   holder.profilePic.setImageUrl(item.profilePic, imageLoader)
                // Feed image
                // if (item.images != null) {
                Glide.with(activity!!).load(imageUrl).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.feedImageView)
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

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//
//        val rootView = inflater.inflate(R.layout.fragment_news, container, false)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Utility.changeStatusbarColor(activity, R.color.bg_gray, false)
//        }
//
//        val mApp = (activity as AppCompatActivity).applicationContext as AppController
//        mApp.firebaseAnalytics(context, NewsListFragment::class.simpleName)
//        mApp.facebookAnalytics(context, NewsListFragment::class.simpleName)
//
//        newsViewModel = ViewModelProvider(this, factory).get(NewsViewModel::class.java)
//        newsViewModel.mNewsListener = this
//
//        listView = rootView.findViewById(R.id.list)
//        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container)
//
//        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(FacebookSdk.getApplicationContext())
//        listView.setHasFixedSize(true)
//        listView.itemAnimator = DefaultItemAnimator()
//        listView.layoutManager = mLayoutManager
//        val header = LayoutInflater.from(activity).inflate(R.layout.header_news, container, false)
//
//        val ivCancel = header.findViewById<ImageView>(R.id.iv_cancel)
//        ivCancel.setOnClickListener { v: View? -> Utility.backNavigation(activity) }
//
//        // Parse static JSON data into NewsResponse object
//        val staticNewsData = JsonParser().parse(staticJsonData).asJsonObject
//        val newsResponse = Gson().fromJson(staticNewsData, NewsResponse::class.java)
//
//        if (newsResponse.success) {
//            if (newsResponse.data.isNotEmpty()) {
//                lstNews.clear()
//                lstNews.addAll(newsResponse.data)
//            }
//        }
//
//        adapter = object : ParallaxRecyclerAdapter<News>(lstNews) {
//
//            override fun onBindViewHolderImpl(viewHolder: RecyclerView.ViewHolder, adapter: ParallaxRecyclerAdapter<News>, position: Int) {
//
//                val item: News = lstNews.get(position)
//                val holder = viewHolder as FeedListViewHolder
//                holder.name.text = item.title
//                val eDate = Utility.changeDateFormat(item.eventDate, Utility.yyyy_MM_dd, Utility.ddMMyyyy)
//                holder.txtExpire.text = eDate
//                val times = 1578140965000
//                // Converting timestamp into x ago format
//                val timeAgo = DateUtils.getRelativeTimeSpanString(times, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS)
//                holder.timestamp.text = timeAgo
//                // Check for empty status message
//                if (!TextUtils.isEmpty(item.description)) {
//                    holder.statusMsg.text = item.description
//                    holder.statusMsg.visibility = View.VISIBLE
//                } else {
//                    // status is empty, remove from view
//                    holder.statusMsg.visibility = View.GONE
//                }
//                val profilePic = "https://api.androidhive.info/feed/img/time.png"
////                val profilePic = "data:image/png;base64,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"
//
//                // Checking for null feed url
//                val url_ = "http://bit.ly/kunjan1"
////                val url_ = "https://www.youtube.com/watch?v=EExSSotojVI"
//
////                if (!TextUtils.isEmpty(item.youtubeUrl.toString())) {
////                    holder.url.text = Html.fromHtml("<a href=\"" + item.youtubeUrl + "\">" + item.youtubeUrl + "</a> ")
////                    // Making URL clickable and opening YouTube video when clicked
////                    holder.url.movementMethod = LinkMovementMethod.getInstance()
////                    holder.url.setOnClickListener {
////                        Utility.watchYoutubeVideo(context, item.youtubeUrl.toString())
////                    }
////                    holder.url.visibility = View.VISIBLE
////                } else {
////                    holder.url.visibility = View.GONE
////                }
//
////                 item.youtubeUrl[0]
////                  if (item.youtubeUrl != null) {
////                holder.url.text = Html.fromHtml("<a href=\"" + url_ + "\">" + url_ + "</a> ")
//                //Making url clickable
////                holder.url.movementMethod = LinkMovementMethod.getInstance()
////                      holder.url.setOnClickListener {
////                        Utility.watchYoutubeVideo(context, item.youtubeUrl.toString())
////                    }
////                holder.url.visibility = View.VISIBLE
////                } else {
////                    // url is null, remove from the view
////                    holder.url.visibility = View.GONE
////                }
//
//                // user profile pic
//                val imageUrl = "https://api.androidhive.info/feed/img/cosmos.jpg"
////                val imageUrl = "https://hips.hearstapps.com/hmg-prod/images/gardenia-royalty-free-image-1580854928.jpg?crop=1.00xw:0.796xh;0,0.0851xh&resize=980:*"
//                Glide.with(activity!!).load(profilePic).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.profilePic)
//                //   holder.profilePic.setImageUrl(item.profilePic, imageLoader)
//                // Feed image
//                // if (item.images != null) {
//                Glide.with(activity!!).load(imageUrl).thumbnail(0.5f).transition(DrawableTransitionOptions.withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.feedImageView)
//                //holder.feedImageView.setImageUrl(item.images[0], imageLoader)
//                holder.feedImageView.visibility = View.VISIBLE
//                /*holder.feedImageView.setResponseObserver(object : FeedImageView.ResponseObserver {
//                    override fun onError() {}
//                    override fun onSuccess() {}
//                })*/
//                /*} else {
//                    holder.feedImageView.visibility = View.GONE
//                }*/
//            }
//
//            override fun onCreateViewHolderImpl(viewGroup: ViewGroup, adapter: ParallaxRecyclerAdapter<News>, i: Int): RecyclerView.ViewHolder {
//                return FeedListViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.feed_item, viewGroup, false))
//            }
//
//            override fun getItemCountImpl(adapter: ParallaxRecyclerAdapter<News>): Int {
//                return lstNews.size
//            }
//        }
//        adapter.setParallaxHeader(header, listView)
//        listView.adapter = adapter
//
//        val jsonObject = JSONObject()
//        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token), ""))
//        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id), ""))
//        jsonObject.put("page", "1")
//        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
//        newsViewModel.getNewsSearch(updated)
//        Handler().postDelayed({
//            mShimmerViewContainer.stopShimmerAnimation()
//            mShimmerViewContainer.visibility = View.GONE
//        }, 4000)
//        mShimmerViewContainer.startShimmerAnimation()
//        mShimmerViewContainer.visibility = View.VISIBLE
//        return rootView
//    }

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