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

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private ListAdapter listAdapter;
    private Button editButton;
    private Button selectButton;
    private String currentParentPath;
    private String rootDirectory = "/";
    private TextView currentPath;

    public Button getEditButton() {
        return this.editButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPath = (TextView) findViewById(R.id.main_path_lable);
        listAdapter = new ListAdapter(this, new ArrayList<ListItem>());
        initializeButtons();

        editButton.setEnabled(false);
        selectButton.setEnabled(false);

        try {
            // initialize ListView
            getDir(rootDirectory);
            setListAdapter(listAdapter);
        } catch (IOException | TagException e) {
            e.printStackTrace();
        }
    }

    private void initializeButtons() {
        editButton = (Button) findViewById(R.id.main_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startEditActivity(null);
            }
        });

        selectButton = (Button) findViewById(R.id.main_select);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // changes the checkbox selection of all mp3-rows
                listAdapter.toggleRows();
                // sets the text of the select button
                selectButton.setText(listAdapter.toggle ? R.string.deselect_all : R.string.select_all);
            }
        });
    }

    private void startEditActivity(File file) {

        List<String> mp3filePaths = new ArrayList<>();

        if (file != null) mp3filePaths.add(file.getPath());

        for (ListAdapter.ViewHolder viewHolder : listAdapter.getListOfViewHolders()) {
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

    private void getDir(String dirPath) throws IOException, TagException {

        if (listAdapter == null) return;

        listAdapter.clearItems();
        listAdapter.clearViewHolders();

        File f = new File(dirPath);
        File[] files = f.listFiles();
        Boolean containsMP3 = false;

        currentPath.setText("path: " + dirPath);

        if (!dirPath.equals(rootDirectory)) {
            listAdapter.addItem(new ListItem(rootDirectory, rootDirectory));
            listAdapter.addItem(new ListItem(f.getParent(), "../"));
        }

        currentParentPath = rootDirectory;

        for (File file : files) {

            ListItem item = new ListItem(file.getPath(), file.getName());
            item.setPath(file.getPath());
            item.setName(file.getName());

            if (!file.isDirectory()) {
                if (file.getName().endsWith(".mp3")) {
                    currentParentPath = file.getParent();
                    MP3File mp3File = new MP3File(file);
                    AbstractID3v2 id3v2tag = mp3File.getID3v2Tag();
                    item.setArtist(new String(id3v2tag.getLeadArtist().getBytes("UTF-8"),"UTF-8"));
                    item.setSongtitle(new String(id3v2tag.getSongTitle().getBytes("UTF-8"),"UTF-8"));
                    containsMP3 = true;
                } else {
                    continue;
                }
            }

            listAdapter.addItem(item);
        }

        listAdapter.notifyDataSetChanged();
        ToggleButtons(containsMP3);
    }

    @Override
    public void onResume() {

        if (listAdapter == null | currentParentPath == null) return;

        super.onResume();

        try {
            getDir(currentParentPath);
            editButton.setEnabled(false);
            selectButton.setText(R.string.select_all);
        } catch (IOException | TagException e) {
            e.printStackTrace();
        }
    }

    public void ToggleButtons(boolean containsMP3) {

        if (selectButton == null | editButton == null)
            return;

        this.selectButton.setEnabled(containsMP3);

        for (ListAdapter.ViewHolder viewHolder : listAdapter.getListOfViewHolders()) {
            if (viewHolder.getPath().endsWith(".mp3")) {
                this.editButton.setEnabled(viewHolder.getCheckState());
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // creates File object from the chosen path
        File file = new File(listAdapter.getItem(position).getPath());

        if (file.isDirectory()) {
            if (file.canRead()) {
                // loads the ListView from the chosen path
                try {
                    getDir(listAdapter.getItem(position).getPath());
                } catch (IOException | TagException e) {
                    e.printStackTrace();
                }
            } else {
                // message if path is not accessible
                new AlertDialog.Builder(this)
                        .setTitle(R.string.access_denied_message)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
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