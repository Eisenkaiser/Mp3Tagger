package com.example.admin.mp3tagger;

/**
 * Created by deutschth on 27.08.2015.
 */
public class ListItem {

    private String path;
    private String name;
    private String songtitle;
    private String artist;

    public ListItem(String path, String name){
        super();
        this.path = path;
        this.name = name;
    }

    public String getSongtitle() {
        return songtitle;
    }

    public void setSongtitle(String songtitle) {
        this.songtitle = songtitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
