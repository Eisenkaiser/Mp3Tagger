package com.example.admin.mp3tagger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.mp3tagger.mp3agic.ID3v1Genres;
import com.example.admin.mp3tagger.mp3agic.ID3v2;
import com.example.admin.mp3tagger.mp3agic.InvalidDataException;
import com.example.admin.mp3tagger.mp3agic.Mp3File;
import com.example.admin.mp3tagger.mp3agic.NotSupportedException;
import com.example.admin.mp3tagger.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConvertActivity extends Activity {

    // momentarly prevents the conversion of multiple files due to difficulties with the concept
    private final boolean ONLY_SINGLE_CONVERSION = true;
    
    private List<Mp3File> files;
    
    // button instances
    private Button track;
    private Button title;
    private Button album;
    private Button artist;
    private Button genre;
    private Button year;
    private TextView path;
    
    // variables for new tags
    private String extractedTrack = "";
    private String extractedTitle = "";
    private String extractedAlbum = "";
    private String extractedArtist = "";
    private String extractedGenre = "";
    private String extractedYear = "";
    
    private Context curContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        curContext = this;

        // loads all selected files from the previous activity
        Bundle extras = LoadMp3List();

        // sets the first path as a template
        path = (TextView) this.findViewById(R.id.convert_path);
        // initializes path textview with the first mp3-file title from the list 
        path.setText((new File(extras.getStringArrayList("mp3filePaths").get(0)).getName()).split(".mp3")[0]);
        path.setTextIsSelectable(true);
        initializeButtons();
    }

    @Nullable
    private Bundle LoadMp3List() {
       
        files = new ArrayList<>();
        Bundle extras = getIntent().getExtras();

        // get the mp3 filepaths from the previous activity
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

                // initializes variable with the currently selected text from the path textview
                extractedTrack = (path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd())).trim();

                if (extractedTrack.length() == 0) {
                    // sets buttons text to default
                    track.setText(getResources().getString(R.string.track));
                } else {
                    // check if text is numeric
                    if (extractedTrack.matches("^-?\\d+$")) {
                        track.setText(extractedTrack);
                    } else {
                        // alert dialog for invalid value
                        new AlertDialog.Builder(curContext)
                                .setTitle(R.string.invalid_value)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                 // sets buttons text to default
                                                track.setText(getResources().getString(R.string.track));
                                            }
                                        }).show();
                    }
                }

            }
        });

        title = (Button) findViewById(R.id.convert_title);
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // initializes variable with the currently selected text from the path textview
                extractedTitle = (path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd())).trim();

                if (extractedTitle.length() > 0) {
                    title.setText(extractedTitle);
                } else {
                    // sets button text to default
                    title.setText(getResources().getString(R.string.title));
                }

            }
        });

        album = (Button) findViewById(R.id.convert_album);
        album.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // initializes variable with the currently selected text from the path textview
                extractedAlbum = (path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd())).trim();

                if (extractedAlbum.length() > 0) {
                    album.setText(extractedAlbum);
                } else {
                    // sets button text to default
                    album.setText(getResources().getString(R.string.album));
                }

            }
        });

        artist = (Button) findViewById(R.id.convert_artist);
        artist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // initializes variable with the currently selected text from the path textview
                extractedArtist = (path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd())).trim();

                if (extractedArtist.length() > 0) {
                    artist.setText(extractedArtist);
                } else {
                    // sets button text to default
                    artist.setText(getResources().getString(R.string.artist));
                }

            }
        });

        year = (Button) findViewById(R.id.convert_year);
        year.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // initializes variable with the currently selected text from the path textview
                extractedYear = (path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd())).trim();

                if (extractedYear.length() == 0) {
                    // sets button text tp default
                    year.setText(getResources().getString(R.string.year));
                } else {
                    // check if text is numeric
                    if (extractedYear.matches("^-?\\d+$")) {
                        year.setText(extractedYear);
                    } else {
                        new AlertDialog.Builder(curContext)
                                .setTitle(R.string.invalid_value)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // sets button text tp default
                                                year.setText(getResources().getString(R.string.year));
                                            }
                                        }).show();
                    }
                }

            }
        });

        genre = (Button) findViewById(R.id.convert_genre);
        genre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // initializes variable with the currently selected text from the path textview
                extractedGenre = (path.getText().toString().substring(path.getSelectionStart(), path.getSelectionEnd())).trim();

                if (extractedGenre.length() == 0) {
                    // sets button text tp default
                    genre.setText(getResources().getString(R.string.genre));
                } else {
                    if (ID3v1Genres.matchGenreDescription(extractedGenre) != -1) {
                        genre.setText(extractedGenre);
                    } else {
                        new AlertDialog.Builder(curContext)
                                .setTitle(R.string.invalid_value)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // sets button text to default
                                                genre.setText(getResources().getString(R.string.genre));
                                            }
                                        }).show();
                    }
                }

            }
        });

        Button cancel = (Button) findViewById(R.id.convert_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // close activity
                finish();
            }
        });

        Button convert = (Button) findViewById(R.id.convert_convert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    saveExtractedTags();
                    finish();
                } catch (IOException | NotSupportedException e) {
                    e.printStackTrace();
                }
                
                // toast for 'converted'
                Toast.makeText(getApplicationContext(), R.string.converted, Toast.LENGTH_LONG).show();
            }
        });

        Button clear = (Button) findViewById(R.id.convert_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearSelection();
            }
        });
    }

    /**
    * sets variables amd button texts to default
    **/
    private void clearSelection() {
      
        extractedAlbum = "";
        extractedTitle = "";
        extractedArtist = "";
        extractedTrack = "";
        extractedYear = "";
        extractedGenre = "";
        
        album.setText(getResources().getString(R.string.album));
        title.setText(getResources().getString(R.string.title));
        artist.setText(getResources().getString(R.string.artist));
        track.setText(getResources().getString(R.string.track));
        genre.setText(getResources().getString(R.string.genre));
        year.setText(getResources().getString(R.string.year));
        
    }
     
    /**
    * iterates through the files list and saves the extracted tags 
    **/
    private void saveExtractedTags() throws IOException, NotSupportedException {

        ID3v2 id3v2;
        boolean isChanged = false;

        for (Mp3File mp3File : files) {

            id3v2 = mp3File.getId3v2Tag();

            if (!extractedTrack.equals("")) {
                id3v2.setTrack(extractedTrack);
                isChanged = true;
            }

            if (!extractedArtist.equals("")) {
                id3v2.setArtist(extractedArtist);
                isChanged = true;
            }

            if (!extractedTitle.equals("")) {
                id3v2.setTitle(extractedTitle);
                isChanged = true;
            }

            if (!extractedAlbum.equals("")) {
                id3v2.setAlbum(extractedAlbum);
                isChanged = true;
            }

            if (!extractedYear.equals("")) {
                id3v2.setYear(extractedYear);
                isChanged = true;
            }

            if (!extractedGenre.equals("")) {
                id3v2.setGenreDescription(extractedGenre);
                isChanged = true;
            }

            if (isChanged) {
                // overwrite
                mp3File.save(mp3File.getFilename());
            } else {
                return;
            }

            if (ONLY_SINGLE_CONVERSION) return;
        }
    }
}
