package com.example.projektzaliczeniowy;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterAttachment extends BaseAdapter {

    private final Context context;
    private ArrayList<String> attachmentListAdapter;
    public ArrayList<String> itemsSelected = new ArrayList<String>();

    public AdapterAttachment(Context context, ArrayList<String> attachmentListAdapter) {
        this.context = context;
        this.attachmentListAdapter = attachmentListAdapter;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.attachment_list_layout,parent,false);
        }

        ImageView attachment = (ImageView) view.findViewById(R.id.attachment);
        attachment.setImageURI(Uri.parse(String.valueOf(itemsSelected)));

        return view;
    }


    //jeśli .jpg to imageview, jeśli .mp4 to videoview
}
