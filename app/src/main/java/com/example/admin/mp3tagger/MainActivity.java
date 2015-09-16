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

    private final String ROOT_DIRECTORY = "/";
    private ListAdapter listAdapter;

    // current parent path
    private String parentPath;

    // buttons instances
    private Button edit;
    private Button select;
    private TextView path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // label for displaying the current directory
        path = (TextView) findViewById(R.id.main_path_label);
        // initializing custom listAdapter for listView
        listAdapter = new ListAdapter(this, new ArrayList<ListItem>());
        // sets instances of the buttons
        initializeButtons();

        try {
            // adds rows to the listView
            getDir(ROOT_DIRECTORY);
            // load the listView adapter
            setListAdapter(listAdapter);
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets instances from buttons
     */
    private void initializeButtons() {

        edit = (Button) findViewById(R.id.main_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // starts new activity for editing id3-tags
                startEditActivity(null);
            }
        });

        select = (Button) findViewById(R.id.main_select);
        select.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // changes the checkbox selection of all mp3-rows
                listAdapter.toggleRows();
                // sets the text of the select button
                select.setText(listAdapter.toggle ? R.string.deselect_all : R.string.select_all);
            }
        });

        // default setting
        edit.setEnabled(false);
        select.setEnabled(false);
    }

    /**
     * starts new edit activity
     *
     * @param file
     */
    private void startEditActivity(File file) {

        List<String> mp3filePaths = new ArrayList<>();

        // adds file to the path list in case of a single selection
        if (file != null) {
            mp3filePaths.add(file.getPath());
        }

        // iterates the listAdapter for selected rows and adds them to the path list
        for (ListAdapter.ViewHolder viewHolder : listAdapter.getListOfViewHolders()) {
            if (viewHolder.getPath().endsWith(".mp3")) {
                if (viewHolder.getCheckState()) {
                    mp3filePaths.add(viewHolder.getPath());
                }
            }
        }

        // this is necessary to pass over values to the new activity
        Intent intent = new Intent(getBaseContext(), EditActivity.class);
        intent.putStringArrayListExtra("mp3filePaths", (ArrayList<String>) mp3filePaths);
        startActivity(intent);
    }

    /**
     * iterates through a given path and adds listItems to the listAdapter
     *
     * @param dirPath
     * @throws IOException
     * @throws InvalidDataException
     * @throws UnsupportedTagException
     */
    private void getDir(String dirPath) throws IOException, InvalidDataException, UnsupportedTagException {

        if (listAdapter == null) return;

        // create file instance of the param path
        File f = new File(dirPath);
        // get files from the path
        File[] files = f.listFiles();

        // determines if the edit and select buttons are enabled true or false
        Boolean containsMP3 = false;

        // clears the listAdapter
        listAdapter.clearItems();
        listAdapter.clearViewHolders();

        // sets the TextView for the current directory
        path.setText("path: " + dirPath);

        // adds the first two elements to the listAdapter for navigating
        if (!dirPath.equals(ROOT_DIRECTORY)) {
            listAdapter.addItem(new ListItem(ROOT_DIRECTORY, ROOT_DIRECTORY));
            listAdapter.addItem(new ListItem(f.getParent(), "../"));
        } else {
            parentPath = ROOT_DIRECTORY;
        }

        // iterates the path and adds mp3 items to the listAdapter
        for (File file : files) {

            ListItem item = new ListItem(file.getPath(), file.getName());
            item.setPath(file.getPath());
            item.setName(file.getName());

            if (!file.isDirectory() && file.getName().endsWith(".mp3")) {
                // sets current path
                parentPath = file.getParent();

                Mp3File mp3File = new Mp3File(file);
                ID3v2 id3v2;

                if (mp3File.hasId3v2Tag()) {

                    id3v2 = mp3File.getId3v2Tag();

                    // if (null) unknown artist as display text in the item, else the usual artist or title from the id3-tag
                    item.setArtist((id3v2.getArtist() == null) ? getResources().getString(R.string.unknown_artist) : id3v2.getArtist());
                    item.setSongtitle((id3v2.getTitle() == null) ? getResources().getString(R.string.unknown_title) : id3v2.getTitle());

                } else {

                    // creates new id3-tag
                    id3v2 = new ID3v24Tag();
                    mp3File.setId3v2Tag(id3v2);
                    id3v2 = mp3File.getId3v2Tag();

                    // default value empty string instead of null
                    id3v2.setArtist("");
                    id3v2.setTitle("");

                    // sets items display as unknown artist/title
                    item.setArtist(getResources().getString(R.string.unknown_artist));
                    item.setSongtitle(getResources().getString(R.string.unknown_title));

                }

                containsMP3 = true;
            }

            // add item to listAdapter
            listAdapter.addItem(item);
        }

        // necessary to update view
        listAdapter.notifyDataSetChanged();

        ToggleButtons(containsMP3);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (listAdapter == null | parentPath == null) return;

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

        // sets the checkboxes
        for (ListAdapter.ViewHolder viewHolder : listAdapter.getListOfViewHolders()) {
            if (viewHolder.getPath().endsWith(".mp3")) {
                this.edit.setEnabled(viewHolder.getCheckState());
            }
        }
    }

    public Button getEdit() {
        return this.edit;
    }

    /**
     * checks if list contains filenames with a specific extension
     * @param filenames
     * @param extension
     * @return
     */
    public boolean hasCertainFiles(String[] filenames, String extension) {

        for (String filename : filenames) {
            if (filename.endsWith(extension)) return true;
        }

        return false;
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {

        // creates File object from the chosen path
        File file = new File(listAdapter.getItem(position).getPath());

        if (file.isDirectory()) {

            if (file.canRead()) {

                if (file.list().length > 50 & hasCertainFiles(file.list(), ".mp3")) {

                    // alert dialog if path contains a lot of mp3s
                    final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
                    myAlertDialog.setTitle(R.string.warning);
                    myAlertDialog.setMessage(R.string.read_warning);

                    myAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // loads the ListView from the chosen path
                            try {
                                getDir(listAdapter.getItem(position).getPath());
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
                        getDir(listAdapter.getItem(position).getPath());
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
