package com.example.admin.mp3tagger;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.mp3tagger.mp3agic.ID3v2;
import com.example.admin.mp3tagger.mp3agic.InvalidDataException;
import com.example.admin.mp3tagger.mp3agic.Mp3File;
import com.example.admin.mp3tagger.mp3agic.NotSupportedException;
import com.example.admin.mp3tagger.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ConvertActivity extends Activity {

    private Button track;
    private Button title;
    private Button album;
    private Button artist;
    private Button genre;
    private Button comment;
    private Button year;

    private Button convert;
    private TextView path;
    List<Mp3File> files;

    private String extractedTrack = "";
    private String extractedTitle = "";
    private String extractedAlbum = "";
    private String extractedArtist = "";
    private String extractedGenre = "";
    private String extractedComment = "";
    private String extractedYear = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        // loads all selected files from the previous activity
        Bundle extras = LoadMp3List();

        // sets the first path as a template
        path = (TextView) this.findViewById(R.id.convert_path);
        path.setText(new File(extras.getStringArrayList("mp3filePaths").get(0)).getName());
        path.setTextIsSelectable(true);

        initializeButtons();

        convert.setEnabled(false);
    }

    @Nullable
    private Bundle LoadMp3List() {
        files = new ArrayList<>();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            for (String path : extras.getStringArrayList("mp3filePaths")) {
                try {
                    files.add(new Mp3File(new File(path)));
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    e.printStackTrace();
                }
            }
        }

        return extras;
    }

    private void initializeButtons() {
        track = (Button) findViewById(R.id.convert_track);
        track.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedTrack = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                track.setText(getResources().getString(R.string.track) + " " + extractedTrack);
            }
        });

        title = (Button) findViewById(R.id.convert_title);
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedTitle = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                title.setText(getResources().getString(R.string.title) + " " + extractedTitle);
                if (extractedTitle.length() > 0) convert.setEnabled(true);
            }
        });

        album = (Button) findViewById(R.id.convert_album);
        album.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedAlbum = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                album.setText(getResources().getString(R.string.album) + " " + extractedAlbum);
                if (extractedAlbum.length() > 0) convert.setEnabled(true);
            }
        });

        artist = (Button) findViewById(R.id.convert_artist);
        artist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedArtist = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                artist.setText(getResources().getString(R.string.artist) + " " + extractedArtist);
                if (extractedArtist.length() > 0) convert.setEnabled(true);
            }
        });

        year = (Button) findViewById(R.id.convert_year);
        year.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedYear = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                year.setText(getResources().getString(R.string.year) + " " + extractedYear);
                if (extractedYear.length() > 0) convert.setEnabled(true);
            }
        });

        genre = (Button) findViewById(R.id.convert_genre);
        genre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedGenre = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                genre.setText(getResources().getString(R.string.genre) + " " + extractedGenre);
                if (extractedGenre.length() > 0) convert.setEnabled(true);
            }
        });

        comment = (Button) findViewById(R.id.convert_comment);
        comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                extractedComment = path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd());
                comment.setText(getResources().getString(R.string.comment) + " " + extractedComment);
                if (extractedComment.length() > 0) convert.setEnabled(true);
            }
        });

        Button cancel = (Button) findViewById(R.id.convert_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        convert = (Button) findViewById(R.id.convert_convert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    ConvertFilenameToTags();
                    finish();
                } catch (IOException | NotSupportedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), R.string.converted, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ConvertFilenameToTags() throws IOException, NotSupportedException {

        ID3v2 id3v2;

        for(Mp3File mp3File:files){

            id3v2 = mp3File.getId3v2Tag();

            if (!extractedTrack.equals("")){
                id3v2.setTrack(extractedTrack);
            }

            if (!extractedArtist.equals("")){
                id3v2.setArtist(extractedArtist);
            }

            if (!extractedTitle.equals("")){
                id3v2.setTitle(extractedTitle);
            }

            if (!extractedAlbum.equals("")){
                id3v2.setAlbum(extractedAlbum);
            }

            if (!extractedYear.equals("")){
                id3v2.setYear(extractedYear);
            }

            if (!extractedGenre.equals("")){
                id3v2.setGenreDescription(extractedGenre);
            }

            if (!extractedComment.equals("")){
                id3v2.setComment(extractedComment);
            }

            mp3File.save(mp3File.getFilename());
        }
    }
}