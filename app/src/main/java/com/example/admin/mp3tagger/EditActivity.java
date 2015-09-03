package com.example.admin.mp3tagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends Activity {

    private List<MP3File> files;
    private TextView artist;
    private TextView album;
    private TextView title;
    private TextView track;
    private TextView genre;
    private TextView year;
    private TextView comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        files = new ArrayList<>();

        artist = (TextView) findViewById(R.id.edit_text_artist);
        album = (TextView) findViewById(R.id.edit_text_album);
        title = (TextView) findViewById(R.id.edit_text_title);
        track = (TextView) findViewById(R.id.edit_text_track);
        genre = (TextView) findViewById(R.id.edit_text_genre);
        year = (TextView) findViewById(R.id.edit_text_year);
        comment = (TextView) findViewById(R.id.edit_text_comment);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            for (String path : extras.getStringArrayList("mp3filePaths")) {
                try {
                    files.add(new MP3File(new File(path)));
                } catch (IOException | TagException e) {
                    e.printStackTrace();
                }
            }
        }

        InitializeButtons();
        try {
            LoadData(this.files);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void LoadData(List<MP3File> mp3FileList) throws UnsupportedEncodingException {

        String maintain = "< " + getString(R.string.maintain) + " >";

        AbstractID3v2 tag;
        tag = mp3FileList.get(0).getID3v2Tag();

        String currentArtist = new String(tag.getLeadArtist().getBytes("UTF-8"),"UTF-8");
        String currentAlbum = new String(tag.getAlbumTitle().getBytes("UTF-8"),"UTF-8");
        String currentTitle = new String(tag.getSongTitle().getBytes("UTF-8"),"UTF-8");
        String currentTrack = new String(tag.getTrackNumberOnAlbum().getBytes("UTF-8"),"UTF-8");
        String currentGenre = new String(tag.getSongGenre().getBytes("UTF-8"),"UTF-8");
        String currentYear = new String(tag.getYearReleased().getBytes("UTF-8"),"UTF-8");
        String currentComment = new String(tag.getSongComment().getBytes("UTF-8"),"UTF-8");

        artist.setText(currentArtist);
        album.setText(currentAlbum);
        title.setText(currentTitle);
        track.setText(currentTrack);
        genre.setText(currentGenre);
        year.setText(currentYear);
        comment.setText(currentComment);

        for (MP3File file : mp3FileList) {

            tag = file.getID3v2Tag();

            if (!currentArtist.equals(tag.getLeadArtist())) {
                artist.setText(maintain);
            }

            if (!currentAlbum.equals(tag.getAlbumTitle())) {
                album.setText(maintain);
            }

            if (!currentTitle.equals(tag.getSongTitle())) {
                title.setText(maintain);
            }
            if (!currentTrack.equals(tag.getTrackNumberOnAlbum())) {
                track.setText(maintain);
            }
            if (!currentGenre.equals(tag.getSongGenre())) {
                genre.setText(maintain);
            }

            if (!currentYear.equals(tag.getYearReleased())) {
                year.setText(maintain);
            }

            if (!currentComment.equals(tag.getSongComment())) {
                comment.setText(maintain);
            }
        }
    }

    private void SaveData(List<MP3File> mp3FileList) throws IOException, TagException {

        AbstractID3v2 tag;

        for (MP3File file : mp3FileList) {
            tag = file.getID3v2Tag();
            tag.setLeadArtist(artist.getText().toString());
            tag.setAlbumTitle(album.getText().toString());
            tag.setSongTitle(title.getText().toString());
            tag.setTrackNumberOnAlbum(track.getText().toString());
            tag.setSongGenre(genre.getText().toString());
            tag.setYearReleased(year.getText().toString());
            tag.setSongComment(comment.getText().toString());
            file.save();
            this.finish();
        }
    }

    private void InitializeButtons() {
        Button cancel = (Button) findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button save = (Button) findViewById(R.id.edit_save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    SaveData(files);
                } catch (IOException | TagException e) {
                    e.printStackTrace();
                }
            }
        });

        Button convert = (Button) findViewById(R.id.edit_convert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(EditActivity.this, ConvertActivity.class));
            }
        });
    }
}