package com.krs.community.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.krs.community.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.regex.Pattern

class VideoSliderAdapter(private val context: Context, private val videoUrls: MutableList<Any>) :
    SliderViewAdapter<VideoSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item1, null)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        val videoUrl = videoUrls[position]

        // Load YouTube video using YouTubePlayerView
        viewHolder.youTubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                // Load the YouTube video using the video URL
                youTubePlayer.cueVideo(getVideoId(videoUrl), 0f)

                Log.d("video URL", "$videoUrl")

            }
        })
    }

    override fun getCount(): Int {
        return videoUrls.size
    }

    class SliderViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val youTubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)
    }

    // Function to extract video ID from YouTube URL
    private fun getVideoId(youtubeUrl: Any): String {
        val pattern =
            "(?:watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2Fwatch%3Fv%3D|youtu.be%2F|^youtu\\.be\\/|watch\\?v=|\\?v=|\\&v=|youtube.com\\/user\\/[^\\/]*\\/|\\.be\\/|youtube.com\\/[^\\/]*\\/)([^\"&'<>?\\s]*)"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(youtubeUrl.toString())

        return if (matcher.find()) {
            matcher.group(1)
        } else {
            // Handle invalid URL or return a default video ID
            "defaultVideoId"
        }
    }
}
