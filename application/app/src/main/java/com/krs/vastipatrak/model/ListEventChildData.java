package com.krs.vastipatrak.model;

import java.util.List;

public class ListEventChildData {

    public List<String> getYoutubeUrls() {
        return YoutubeUrls;
    }

    public void setYoutubeUrls(List<String> youtubeUrls) {
        YoutubeUrls = youtubeUrls;
    }

    public List<String> getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        ImageUrls = imageUrls;
    }

    List<String> YoutubeUrls;
    List<String> ImageUrls;

}
