package com.example.admin.mp3tagger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deutschth on 27.08.2015.
 */
public class ListAdapter extends ArrayAdapter<ListItem> implements CompoundButton.OnCheckedChangeListener {

    private final MainActivity context;
    public boolean toggle = false;
    private ArrayList<ListItem> items;
    private List<ViewHolder> viewHolderList;

    public ListAdapter(MainActivity mainActivity, ArrayList<ListItem> items) {
        super(mainActivity, R.layout.row_mp3, items);
        this.context = mainActivity;
        this.items = items;
        viewHolderList = new ArrayList<>();
    }

    public List<ViewHolder> getListOfViewHolders() {
        return this.viewHolderList;
    }

    public void addItem(ListItem item) {
        this.items.add(item);
    }

    public void clearItems() {
        if (this.items != null) {
            this.items.clear();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder viewHolder;

        if (items.get(position).getName().endsWith(".mp3")) {

            view = inflater.inflate(R.layout.row_mp3, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.artist = (TextView) view.findViewById(R.id.row_artist);
            viewHolder.title = (TextView) view.findViewById(R.id.row_title);
            viewHolder.filename = (TextView) view.findViewById(R.id.row_mp3_filename);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.row_checkbox);
            view.setTag(viewHolder);
        } else {
            view = inflater.inflate(R.layout.row_folder, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.filename = (TextView) view.findViewById(R.id.row_filename);
            view.setTag(viewHolder);
        }

        if (items.get(position).getName().endsWith(".mp3")) {
            try {
                viewHolder.setArtist(new String(items.get(position).getArtist().getBytes("UTF-8"),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                viewHolder.setTitle(new String(items.get(position).getSongtitle().getBytes("UTF-8"),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            viewHolder.setFilename(items.get(position).getName());
            viewHolder.setPath(items.get(position).getPath());
            viewHolder.setCheckState(this.toggle);
        } else {
            viewHolder.setPath(items.get(position).getPath());
            viewHolder.setFilename(items.get(position).getName());
            viewHolder.setCheckState(this.toggle);
        }

        if (viewHolder.checkBox != null) {
            viewHolder.checkBox.setOnCheckedChangeListener(this);
        }

        viewHolderList.add(viewHolder);

        return view;
    }

    public void toggleRows() {
        this.toggle = !this.toggle;

        for (ViewHolder viewHolder:viewHolderList) {
            viewHolder.setCheckState(toggle);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        boolean isAtLeastOneChecked = false;

        for (ViewHolder viewHolder : viewHolderList) {
            if (viewHolder.checkBox != null && viewHolder.checkBox.isChecked()) {
                isAtLeastOneChecked = true;
                break;
            }
        }

        Button btn = context.getEditButton();
        btn.setEnabled(isAtLeastOneChecked);
    }

    public void clearViewHolders() {
        this.viewHolderList.clear();
        this.notifyDataSetChanged();
    }

    public static class ViewHolder {

        private CheckBox checkBox;
        private TextView artist;
        private TextView title;
        private TextView filename;
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean getCheckState() {
            return checkBox != null && this.checkBox.isChecked();
        }

        public void setCheckState(boolean toggle) {
            if (this.checkBox == null) return;
            this.checkBox.setChecked(toggle);
        }

        public void setArtist(String artist) {
            if (this.artist == null) return;
            this.artist.setText(artist);
        }

        public void setFilename(String filename) {
            if (this.filename == null) return;
            this.filename.setText(filename);
        }

        public String getTitle() {
            if (this.title == null) return "";
            return title.getText().toString();
        }

        public void setTitle(String title) {
            if (this.title == null) return;
            this.title.setText(title);
        }
    }
}