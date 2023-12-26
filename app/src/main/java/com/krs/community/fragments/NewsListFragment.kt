package com.krs.community.fragments

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
import com.krs.community.viewmodel.NewsViewModel
import com.krs.community.viewmodelfactory.NewsModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.regex.Pattern
class NewsListFragment : Fragment(), KodeinAware, NewsListener {

    private lateinit var listView: RecyclerView
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var adapter: ParallaxRecyclerAdapter<News>
    private var lstNews = ArrayList<News>()
    override val kodein by kodein()

    private lateinit var newsViewModel: NewsViewModel
    private val factory: NewsModelFactory by instance<NewsModelFactory>()

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

                // Check if there is a YouTube video URL
                if (item.youtubeUrl?.isNotEmpty() == true) {
                    // Show YouTubePlayerView and load the first video
                    holder.youTubePlayerContainer.visibility = View.VISIBLE

                    // Extract the video ID from the URL
                    val videoUrl = item.youtubeUrl[0]
                    val videoId = extractVideoId(videoUrl)
                    holder.youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            // Load the first video using the video ID
                            youTubePlayer.cueVideo(videoId, 0f)
                        }
                    })
                } else {
                    // Hide YouTubePlayerView if there is no video URL
                    holder.youTubePlayerContainer.visibility = View.GONE
                }

                //news feed Image
                if (item.images?.isNotEmpty() == true) {
                    val imageUrl = item.images[0]
                    Glide.with(activity!!).load(imageUrl)
                        .thumbnail(0.5f)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(holder.feedImageView)
                    holder.feedImageView.visibility = View.VISIBLE
                } else {
                    holder.feedImageView.visibility = View.GONE
                }

                // Set profile pic using Glide
                if (item.profilePic != null && item.profilePic.isNotEmpty()) {
                    Glide.with(activity!!)
                        .load(item.profilePic)
                        .thumbnail(0.5f)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .listener(object : RequestListener<Drawable?> {
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                dataSource: com.bumptech.glide.load.DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // Image loaded successfully, make the profile pic visible
                                holder.profilePic.visibility = View.VISIBLE
                                return false
                            }
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // Load default profile pic from drawable if loading fails
                                holder.profilePic.setImageResource(R.drawable.user_profile)
                                holder.profilePic.visibility = View.VISIBLE
                                return true // indicate that the error is handled
                            }
                        })
                        .into(holder.profilePic)
                } else {
                    // No profile pic URL provided, load default profile pic from drawable
                    holder.profilePic.setImageResource(R.drawable.user_profile)
                    holder.profilePic.visibility = View.VISIBLE
                }
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

    // Function to extract video ID from YouTube URL
    private fun extractVideoId(youtubeUrl: Any): String {
        val pattern = "(?:watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2Fwatch%3Fv%3D|youtu.be%2F|^youtu\\.be\\/|watch\\?v=|\\?v=|\\&v=|youtube.com\\/user\\/[^\\/]*\\/|\\.be\\/|youtube.com\\/[^\\/]*\\/)([^\"&'<>?\\s]*)"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youtubeUrl.toString())

        return if (matcher.find()) {
            matcher.group(1)
        } else {
            // Handle invalid URL or return a default video ID
            "defaultVideoId"
        }
    }

    internal class FeedListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var timestamp: TextView = itemView.findViewById(R.id.timestamp)
        var statusMsg: TextView = itemView.findViewById(R.id.txtStatusMsg)
        var txtExpire: TextView = itemView.findViewById(R.id.txt_expire)
        var youTubePlayer :YouTubePlayerView = itemView.findViewById(R.id.youtube_player_view)
        var youTubePlayerContainer: FrameLayout = itemView.findViewById(R.id.youtube_player_container)
        var profilePic: CircleImageView = itemView.findViewById(R.id.profilePic)
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
//                Log.d("NewsListFragment", "Response Data: ${Gson().toJson(response.data)}")
            } else {
                Utility.displaySnackBarWithBottomMargin(listView, response.message)
            }
        }
    }
    override fun getFailure(message: String) {
    }
}