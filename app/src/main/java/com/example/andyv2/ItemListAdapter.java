package com.example.andyv2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ItemListAdapter extends ArrayAdapter<Item> {
    private Activity context;
    private List<Item> itemList;

    public ItemListAdapter(Activity context, List<Item> studentList) {
        super(context, R.layout.list_layout, studentList);
        this.context = context;
        this.itemList = studentList;
    }

    public ItemListAdapter(Context context, int resource, List<Item> objects, Activity context1, List<Item> itemList) {
        super(context, resource, objects);
        this.context = context1;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvName = listViewItem.findViewById(R.id.textViewName);

        Item item = itemList.get(position);
        tvName.setText(item.getItemName());
        ;
        ImageView imgOnePhoto = (ImageView) listViewItem.findViewById(R.id.ddddd);
        //  DownloadImageTask dit = new DownloadImageTask(_context, imgOnePhoto);
        //dit.execute(toon.getPicture());
        if (item.getItemPhotoURL() != null) {
            new ImageDownloaderTask(imgOnePhoto).execute(item.getItemPhotoURL());
        }

        return listViewItem;
    }

}
