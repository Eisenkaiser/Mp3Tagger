package com.example.admin.mp3tagger;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ListActivity {

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button dirChooserButton = (Button) findViewById(R.id.ChooseDirButton);
//        dirChooserButton.setOnClickListener(new View.OnClickListener()
//        {
//            private String m_chosenDir = "";
//            private boolean m_newFolderEnabled = true;
//
//            @Override
//            public void onClick(View v)
//            {
//                // Create DirectoryChooserDialog and register a callback
//                DirectoryChooserDialog directoryChooserDialog =
//                        new DirectoryChooserDialog(MainActivity.this,
//                                new DirectoryChooserDialog.ChosenDirectoryListener()
//                                {
//                                    @Override
//                                    public void onChosenDir(String chosenDir)
//                                    {
//                                        m_chosenDir = chosenDir;
//                                        Toast.makeText(
//                                                MainActivity.this, "Chosen directory: " +
//                                                        chosenDir, Toast.LENGTH_LONG).show();
//                                    }
//                                });
//                // Toggle new folder button enabling
//                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
//                // Load directory chooser dialog for initial 'm_chosenDir' directory.
//                // The registered callback will be called upon final directory selection.
//                directoryChooserDialog.chooseDirectory(m_chosenDir);
//                m_newFolderEnabled = ! m_newFolderEnabled;
//            }
//        });
//    }

        // Use the current directory as title
        path = "/storage";

        if (getIntent().hasExtra("path"))
        {
            path = getIntent().getStringExtra("path");
        }

        setTitle(path);

        // Read all files sorted into the values-array
        List values = FindFiles(true);
        File dir = new File(path);

//        if (!dir.canRead())
//        {
//            setTitle(getTitle() + " (inaccessible)");
//        }
//
//        String[] list = FindFiles(true);//dir.list();
//
//        if (list != null)
//        {
//            for (String file : list)
//            {
//                if (!file.startsWith("."))
//                {
//                    values.add(file);
//                }
//            }
//        }

        Collections.sort(values);

        // Put the data into the list
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, values);
        setListAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String filename = (String)getListAdapter().getItem(position);

        if (path.endsWith(File.separator))
        {
            filename = path + filename;
        }
        else
        {
            filename = path + File.separator + filename;
        }

        if (new File(filename).isDirectory())
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("path", filename);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
        }
    }

    private List<String> FindFiles(Boolean fullPath) {
        final List<String> tFileList = new ArrayList<String>();

        String[] fileTypes = new String[]{".mp3"}; // file extensions you're looking for
        FilenameFilter[] filter = new FilenameFilter[fileTypes .length];

        int i = 0;
        for (final String type : fileTypes ) {
            filter[i] = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + type);
                }
            };
            i++;
        }

        FileUtils fileUtils = new FileUtils();
        File[] allMatchingFiles = fileUtils.listFilesAsArray(
                new File("/sdcard"), filter, -1);
        for (File f : allMatchingFiles) {
            if (fullPath) {
                tFileList.add(f.getAbsolutePath());
            }
            else {
                tFileList.add(f.getName());
            }
        }
        return tFileList;
    }
}
