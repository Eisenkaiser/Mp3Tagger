package com.example.admin.mp3tagger;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.admin.mp3tagger.mp3agic.ID3v24Tag;
import com.example.admin.mp3tagger.mp3agic.InvalidDataException;
import com.example.admin.mp3tagger.mp3agic.Mp3File;
import com.example.admin.mp3tagger.mp3agic.ID3v2;
import com.example.admin.mp3tagger.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {


    private final String ROOT = "/";

    private ListAdapter adapter;
    private Button edit;
    private Button select;
    private TextView path;
    private String parentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // label for displaying the current directory
        path = (TextView) findViewById(R.id.main_path_label);
        // initializing custom adapter for list view
        adapter = new ListAdapter(this, new ArrayList<ListItem>());

        initializeButtons();

        // default setting
        edit.setEnabled(false);
        select.setEnabled(false);

        try {
            // adds rows to the collection
            getDir(ROOT);
            // load the list view
            setListAdapter(adapter);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }
    }

    private void initializeButtons() {
        edit = (Button) findViewById(R.id.main_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startEditActivity(null);
            }
        });

        select = (Button) findViewById(R.id.main_select);
        select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // changes the checkbox selection of all mp3-rows
                adapter.toggleRows();
                // sets the text of the select button
                select.setText(adapter.toggle ? R.string.deselect_all : R.string.select_all);
            }
        });
    }

    private void startEditActivity(File file) {

        List<String> mp3filePaths = new ArrayList<>();

        if (file != null) {
            mp3filePaths.add(file.getPath());
        }

        for (ListAdapter.ViewHolder viewHolder : adapter.getListOfViewHolders()) {
            if (viewHolder.getPath().endsWith(".mp3")) {
                if (viewHolder.getCheckState()) {
                    mp3filePaths.add(viewHolder.getPath());
                }
            }
        }

        Intent intent = new Intent(getBaseContext(), EditActivity.class);
        intent.putStringArrayListExtra("mp3filePaths", (ArrayList<String>) mp3filePaths);
        startActivity(intent);
    }

    private void getDir(String dirPath) throws IOException, InvalidDataException, UnsupportedTagException {

        if (adapter == null) return;

        File f = new File(dirPath);
        File[] files = f.listFiles();
        Boolean containsMP3 = false;
        adapter.clearItems();
        adapter.clearViewHolders();
        path.setText("path: " + dirPath);

        if (!dirPath.equals(ROOT)) {
            adapter.addItem(new ListItem(ROOT, ROOT));
            adapter.addItem(new ListItem(f.getParent(), "../"));
        } else {
            parentPath = ROOT;
        }

        for (File file : files) {

//            if (!file.canWrite()) {
//                continue;
//            }

            ListItem item = new ListItem(file.getPath(), file.getName());
            item.setPath(file.getPath());
            item.setName(file.getName());

            if (!file.isDirectory()) {
                if (file.getName().endsWith(".mp3")) {

                    parentPath = file.getParent();

                    Mp3File mp3File = new Mp3File(file);
                    ID3v2 id3v2;
                    if (mp3File.hasId3v2Tag()) {
                        id3v2 = mp3File.getId3v2Tag();
                        item.setArtist((id3v2.getArtist() == null) ? getResources().getString(R.string.unknown_artist) : id3v2.getArtist());
                        item.setSongtitle((id3v2.getTitle() == null) ? getResources().getString(R.string.unknown_title) : id3v2.getTitle());
                    } else {
                        id3v2 = new ID3v24Tag();
                        mp3File.setId3v2Tag(id3v2);
                        id3v2 = mp3File.getId3v2Tag();
                        id3v2.setArtist("");
                        id3v2.setTitle("");
                        item.setArtist(getResources().getString(R.string.unknown_artist));
                        item.setSongtitle(getResources().getString(R.string.unknown_title));
                    }

                    containsMP3 = true;
                } else {
                    continue;
                }
            }

            adapter.addItem(item);
        }

        adapter.notifyDataSetChanged();
        ToggleButtons(containsMP3);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (adapter == null | parentPath == null) return;

        try {
            getDir(parentPath);
            edit.setEnabled(false);
            select.setText(R.string.select_all);
        } catch (IOException | InvalidDataException | UnsupportedTagException e) {
            e.printStackTrace();
        }
    }

    public void ToggleButtons(boolean containsMP3) {

        if (select == null | edit == null)
            return;

        this.select.setEnabled(containsMP3);

        for (ListAdapter.ViewHolder viewHolder : adapter.getListOfViewHolders()) {
            if (viewHolder.getPath().endsWith(".mp3")) {
                this.edit.setEnabled(viewHolder.getCheckState());
            }
        }
    }

    public Button getEdit() {
        return this.edit;
    }

    public boolean hasCertainFiles(String[] filenames, String extension){

        for (String filename : filenames){
            if (filename.endsWith(extension)) return true;
        }

        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {

        // creates File object from the chosen path
        File file = new File(adapter.getItem(position).getPath());

        if (file.isDirectory()) {
            if (file.canRead()) {
                if (file.list().length > 50 & hasCertainFiles(file.list(),".mp3")) {
                    final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
                    myAlertDialog.setTitle(R.string.warning);
                    myAlertDialog.setMessage(R.string.read_warning);

                    myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // loads the ListView from the chosen path
                            try {
                                getDir(adapter.getItem(position).getPath());
                            } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    myAlertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.cancel();
                        }
                    });

                    myAlertDialog.show();
                } else {
                    // loads the ListView from the chosen path
                    try {
                        getDir(adapter.getItem(position).getPath());
                    } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // message if path is not accessible
                new AlertDialog.Builder(this)
                        .setTitle(R.string.access_denied_message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).show();
            }
        } else {
            // opens the EditActivity and loads the selected mp3-file
            if (file.getName().endsWith(".mp3")) {
                startEditActivity(file);
            }
        }
    }
}