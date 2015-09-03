package com.example.admin.mp3tagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

/**
 * Created by deutschth on 26.08.2015.
 */
public class MP3Tags {

    private List<File> fileList;
    private File folder;

    public MP3Tags(File folder, List<File> fileList) {
        this.fileList = fileList;
        this.folder = folder;
    }

    public MP3Tags(File folder) {
        this.folder = folder;
    }

    public MP3Tags(){

    }

    public void loadFiles(){
        fileList = getFiles(folder);
    }

     //lists all files from a given directory, recursively
    public static List<File> getFiles(final File folder) {

        List<File> list = new ArrayList<>();

        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                list.addAll(getFiles(file));
            } else {
                list.add(file);
            }
        }

        return list;
    }

    private void saveChanges(List<File> files){

        MP3File mp3file;
        AbstractID3v2 id3v2tag;

        for (File file: files) {
            try {
                mp3file = new MP3File(file);
                id3v2tag = mp3file.getID3v2Tag();

                id3v2tag.setLeadArtist("");
                id3v2tag.setAlbumTitle("");
                id3v2tag.setSongTitle("");
                id3v2tag.setSongGenre("");
                id3v2tag.setAuthorComposer("");
                id3v2tag.setYearReleased("");
                id3v2tag.setTrackNumberOnAlbum("");
                id3v2tag.setSongLyric("");
                id3v2tag.setSongComment("");
                id3v2tag.setAlbumTitle("");
                id3v2tag.setFrame(null);

                mp3file.save();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            }
        }
    }
}
