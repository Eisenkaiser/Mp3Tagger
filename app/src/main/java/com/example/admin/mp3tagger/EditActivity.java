package com.example.admin.mp3tagger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.mp3tagger.mp3agic.InvalidDataException;
import com.example.admin.mp3tagger.mp3agic.Mp3File;
import com.example.admin.mp3tagger.mp3agic.ID3v2;
import com.example.admin.mp3tagger.mp3agic.NotSupportedException;
import com.example.admin.mp3tagger.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends Activity implements TextWatcher {

    private String maintain;
    private List<Mp3File> files;
    private TextView artist;
    private TextView album;
    private TextView title;
    private TextView track;
    private TextView genre;
    private TextView year;
    private TextView comment;
    private Button save;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        maintain = "< " + getString(R.string.maintain) + " >";

        initializeTextViews();
        InitializeButtons();
        initializeListeners();

        LoadMp3List();

        try {
            LoadData(this.files);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        this.save.setEnabled(false);
    }

    private void initializeListeners() {
        this.artist.addTextChangedListener(this);
        this.album.addTextChangedListener(this);
        this.track.addTextChangedListener(this);
        this.year.addTextChangedListener(this);
        this.comment.addTextChangedListener(this);
        this.title.addTextChangedListener(this);
        this.genre.addTextChangedListener(this);
    }

    private void initializeTextViews() {
        artist = (TextView) findViewById(R.id.edit_text_artist);
        album = (TextView) findViewById(R.id.edit_text_album);
        title = (TextView) findViewById(R.id.edit_text_title);
        track = (TextView) findViewById(R.id.edit_text_track);
        genre = (TextView) findViewById(R.id.edit_text_genre);
        year = (TextView) findViewById(R.id.edit_text_year);
        comment = (TextView) findViewById(R.id.edit_text_comment);
    }

    private void LoadMp3List() {

        files = new ArrayList<>();
        extras = getIntent().getExtras();

        if (extras != null) {
            for (String path : extras.getStringArrayList("mp3filePaths")) {
                try {
                    files.add(new Mp3File(new File(path)));
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void LoadData(List<Mp3File> mp3FileList) throws UnsupportedEncodingException {

        if (mp3FileList == null | mp3FileList.size() == 0) return;

        ID3v2 id3v2 = mp3FileList.get(0).getId3v2Tag();

        String currentArtist = (id3v2.getArtist() == null) ? "" : id3v2.getArtist();
        String currentAlbum = (id3v2.getAlbum() == null) ? "" : id3v2.getAlbum();
        String currentTitle = (id3v2.getTitle() == null) ? "" : id3v2.getTitle();
        String currentTrack = (id3v2.getTrack() == null) ? "" : id3v2.getTrack();
        String currentGenre = (id3v2.getGenreDescription() == null) ? "" : id3v2.getGenreDescription();
        String currentYear = (id3v2.getYear() == null) ? "" : id3v2.getYear();
        String currentComment = (id3v2.getComment() == null) ? "" : id3v2.getComment();

        artist.setText(currentArtist);
        album.setText(currentAlbum);
        title.setText(currentTitle);
        track.setText(currentTrack);
        genre.setText(currentGenre);
        year.setText(currentYear);
        comment.setText(currentComment);

        for (Mp3File mp3File : mp3FileList) {

            id3v2 = mp3File.getId3v2Tag();

            if (!currentArtist.equals((id3v2.getArtist() == null) ? "" : id3v2.getArtist())) {
                artist.setText(maintain);
            }

            if (!currentAlbum.equals((id3v2.getAlbum() == null) ? "" : id3v2.getAlbum())) {
                album.setText(maintain);
            }

            if (!currentTitle.equals((id3v2.getTitle() == null) ? "" : id3v2.getTitle())) {
                title.setText(maintain);
            }

            if (!currentTrack.equals((id3v2.getTrack() == null) ? "" : id3v2.getTrack())) {
                track.setText(maintain);
            }

            if (!currentGenre.equals((id3v2.getGenreDescription() == null) ? "" : id3v2.getGenreDescription())) {
                genre.setText(maintain);
            }

            if (!currentYear.equals((id3v2.getYear() == null) ? "" : id3v2.getYear())) {
                year.setText(maintain);
            }

            if (!currentComment.equals((id3v2.getComment() == null) ? "" : id3v2.getComment())) {
                comment.setText(maintain);
            }
        }
    }

    private void SaveData(List<Mp3File> mp3FileList) throws IOException, NotSupportedException {

        ID3v2 id3v2;

        for (Mp3File mp3File : mp3FileList) {
            id3v2 = mp3File.getId3v2Tag();
            id3v2.setArtist((artist.getText().toString().equals(maintain) ? id3v2.getArtist() : artist.getText().toString()));
            id3v2.setAlbum((album.getText().toString().equals(maintain) ? id3v2.getAlbum() : album.getText().toString()));
            id3v2.setTitle((title.getText().toString().equals(maintain) ? id3v2.getTitle() : title.getText().toString()));
            id3v2.setTrack((track.getText().toString().equals(maintain) ? id3v2.getTrack() : track.getText().toString()));
            id3v2.setGenreDescription((genre.getText().toString().equals(maintain) ? id3v2.getGenreDescription() : genre.getText().toString()));
            id3v2.setYear((year.getText().toString().equals(maintain) ? id3v2.getYear() : year.getText().toString()));
            id3v2.setComment((comment.getText().toString().equals(maintain) ? id3v2.getComment() : comment.getText().toString()));
            mp3File.save(mp3File.getFilename());
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

        save = (Button) findViewById(R.id.convert_convert);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    SaveData(files);
                } catch (IOException | NotSupportedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();
            }
        });

        Button convert = (Button) findViewById(R.id.edit_convert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ConvertActivity.class);
                intent.putStringArrayListExtra("mp3filePaths", extras.getStringArrayList("mp3filePaths"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        this.save.setEnabled(true);
    }
}