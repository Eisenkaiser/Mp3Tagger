package com.example.admin.mp3tagger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.mp3tagger.mp3agic.ID3v1Genres;
import com.example.admin.mp3tagger.mp3agic.InvalidDataException;
import com.example.admin.mp3tagger.mp3agic.Mp3File;
import com.example.admin.mp3tagger.mp3agic.ID3v2;
import com.example.admin.mp3tagger.mp3agic.NotSupportedException;
import com.example.admin.mp3tagger.mp3agic.UnsupportedTagException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditActivity extends Activity implements AdapterView.OnItemSelectedListener {

    // string for R.string.multiple_selected
    private String multipleSelected;
    
    private List<Mp3File> files;
    
    private ArrayAdapter<String> genreAdapter;
    
    // index of the selected genre
    private  int currentGenrePosition;
    
    private boolean isConverted = false;
    private boolean genreIsChanged = false;
    
    // gui instances
    private EditText artist;
    private EditText album;
    private EditText title;
    private EditText track;
    private EditText year;
    private TextView genreText;
    private ImageButton imageButton;
    private Spinner spinner;
    
    // variable to hold data from the previous activity
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // initialize the gui
        initializeGenreSpinner();
        initializeEditTexts();
        InitializeButtons();
        multipleSelected = getResources().getString(R.string.multiple_selected);
        
        // converts path list to a list of mp3s
        LoadMp3List();

        try {
            // loads tags into the gui
            LoadData(this.files);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void initializeGenreSpinner() {
        // instanciate the spinner
        spinner = (Spinner) findViewById(R.id.edit_spinner);
        // load genre from the static string array
        List<String> genreList = Arrays.asList(ID3v1Genres.GENRES);
        // sorts the genres alphabetically
        Collections.sort(genreList, String.CASE_INSENSITIVE_ORDER);
        // initialize spinner adapter
        genreAdapter = new ArrayAdapter<>(this,R.layout.edit_genre_item, genreList);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(genreAdapter);
        // set listener
        spinner.setOnItemSelectedListener(this);
    }

    private void initializeEditTexts() {
        artist = (EditText) findViewById(R.id.edit_text_artist);
        album = (EditText) findViewById(R.id.edit_text_album);
        title = (EditText) findViewById(R.id.edit_text_title);
        track = (EditText) findViewById(R.id.edit_text_track);
        year = (EditText) findViewById(R.id.edit_text_year);
    }

    private void LoadMp3List() {

        files = new ArrayList<>();
        // get values from previous activity
        extras = getIntent().getExtras();

        // initialize list of mp3s from the path list
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
        
        // the first mp3 in the list serves as a template
        ID3v2 id3v2 = mp3FileList.get(0).getId3v2Tag();

        // initialize the 'template'
        byte[] currentImage = id3v2.getAlbumImage();
        String currentArtist = id3v2.getArtist();
        String currentAlbum = id3v2.getAlbum();
        String currentTitle = id3v2.getTitle();
        String currentTrack = id3v2.getTrack();
        String currentYear = id3v2.getYear();
        int currentGenre = id3v2.getGenre();
        
        // hints no possible in spinner, so we use the textview for it
        String genreTextIfMultipleSelected = id3v2.getGenreDescription();
        
        // if true -> loads default image from resources
        boolean imgIsDifferent = false;

        genreText = (TextView) findViewById(R.id.text_Genre);

        // check where following tags do not match and change the hint etc.
        for (Mp3File mp3File : mp3FileList) {

            id3v2 = mp3File.getId3v2Tag();

            if (currentAlbum == null | id3v2.getAlbumImage() == null){
                imgIsDifferent = true;
            } else {
                if (!imgIsDifferent & !Arrays.equals(currentImage, id3v2.getAlbumImage())){
                    imgIsDifferent = true;
                }
            }

            if (currentArtist == null & id3v2.getArtist() == null) {
                artist.setHint(R.string.empty);
                currentArtist = "";
            } else if (currentArtist != null ^ id3v2.getArtist() != null) {
                artist.setHint(multipleSelected);
                currentArtist = "";
            } else if (!currentArtist.equals(id3v2.getArtist())) {
                artist.setHint(multipleSelected);
                currentArtist = "";
            } else {
                artist.setHint("");
            }

            if (currentAlbum == null & id3v2.getAlbum() == null) {
                album.setHint(R.string.empty);
                currentAlbum = "";
            } else if (currentAlbum != null ^ id3v2.getAlbum() != null) {
                album.setHint(multipleSelected);
                currentAlbum = "";
            } else if (!currentAlbum.equals(id3v2.getAlbum())) {
                album.setHint(multipleSelected);
                currentAlbum = "";
            } else {
                album.setHint("");
            }

            if (currentTitle == null & id3v2.getTitle() == null) {
                title.setHint(R.string.empty);
                currentTitle = "";
            } else if (currentTitle != null ^ id3v2.getTitle() != null) {
                title.setHint(multipleSelected);
                currentTitle = "";
            } else if (!currentTitle.equals(id3v2.getTitle())) {
                title.setHint(multipleSelected);
                currentTitle = "";
            } else {
                title.setHint("");
            }

            if (currentTrack == null & id3v2.getTrack() == null) {
                track.setHint(R.string.empty);
                currentTrack = "";
            } else if (currentTrack != null ^ id3v2.getTrack() != null) {
                track.setHint(multipleSelected);
                currentTrack = "";
            } else if (!currentTrack.equals(id3v2.getTrack())) {
                track.setHint(multipleSelected);
                currentTrack = "";
            } else {
                track.setHint("");
            }

            // set the genre textview - checks for null included
            if (genreTextIfMultipleSelected == null & id3v2.getGenreDescription() == null) {
                genreTextIfMultipleSelected = getResources().getString(R.string.genre) + " - " + getResources().getString(R.string.empty);
            } else if (genreTextIfMultipleSelected != null ^ id3v2.getGenreDescription() != null) {
                genreTextIfMultipleSelected = getResources().getString(R.string.genre) + " - " + multipleSelected;
            } else if (!genreTextIfMultipleSelected.equals(id3v2.getGenreDescription())) {
                genreTextIfMultipleSelected = getResources().getString(R.string.genre) + " - " + multipleSelected;
            } else {
                genreTextIfMultipleSelected = getResources().getString(R.string.genre);
            }

            // sets the year textview - checks for null included
            if (currentYear == null & id3v2.getYear() == null) {
                year.setHint(R.string.empty);
                currentYear = "";
            } else if (currentYear != null ^ id3v2.getYear() != null) {
                year.setHint(multipleSelected);
                currentYear = "";
            } else if (!currentYear.equals(id3v2.getYear())) {
                year.setHint(multipleSelected);
                currentYear = "";
            } else {
                year.setHint("");
            }
        }

        artist.setText(currentArtist);
        album.setText(currentAlbum);
        title.setText(currentTitle);
        track.setText(currentTrack);
        genreText.setText(genreTextIfMultipleSelected);
        spinner.setSelection(currentGenre);
        year.setText(currentYear);

        Bitmap bmp;

        if (imgIsDifferent) {
            // load default image aka placeholder image
            bmp = BitmapFactory.decodeByteArray(fillImageWithPlaceHolder(), 0, fillImageWithPlaceHolder().length);
        }else {
            // load image from tags
            bmp = BitmapFactory.decodeByteArray(currentImage, 0, currentImage.length);
        }

        imageButton.setImageBitmap(bmp);
    }

    private byte[] fillImageWithPlaceHolder() {
        // load image from resources
        Drawable d = ContextCompat.getDrawable(this, R.drawable.img_placeholder);
        // conversion
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void SaveTags(List<Mp3File> mp3FileList) throws IOException, NotSupportedException {

        ID3v2 id3v2;

        for (Mp3File mp3File : mp3FileList) {

            id3v2 = mp3File.getId3v2Tag();
            
            // sets tags
            id3v2.setArtist((artist.getHint().toString().equals(multipleSelected) ? id3v2.getArtist() : artist.getText().toString()));
            id3v2.setAlbum((album.getHint().toString().equals(multipleSelected) ? id3v2.getAlbum() : album.getText().toString()));
            id3v2.setTitle((title.getHint().toString().equals(multipleSelected) ? id3v2.getTitle() : title.getText().toString()));
            id3v2.setTrack((track.getHint().toString().equals(multipleSelected) ? id3v2.getTrack() : track.getText().toString()));
            id3v2.setGenreDescription((!genreText.getText().toString().equals(getResources().getString(R.string.genre)) & !genreIsChanged) ? id3v2.getGenreDescription() : genreAdapter.getItem(currentGenrePosition));
            id3v2.setYear((year.getHint().toString().equals(multipleSelected) ? id3v2.getYear() : year.getText().toString()));

            mp3File.save(mp3File.getFilename());
        }

        // close activity
        this.finish();
    }

    private void InitializeButtons() {

        Button cancel = (Button) findViewById(R.id.edit_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button save = (Button) findViewById(R.id.convert_convert);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    SaveTags(files);
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
                isConverted = true;
            }
        });

        imageButton = (ImageButton) findViewById(R.id.edit_imagebutton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Load image gallery
                // TODO Resize image to 300x300px
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentGenrePosition = position;
        genreIsChanged = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onResume() {

        super.onResume();

        if (!isConverted) return;

        LoadMp3List();

        try {
            LoadData(this.files);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        isConverted = !isConverted;
    }
}
