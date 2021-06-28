package com.example.androidphotos55;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class PhotoAdapter extends ArrayAdapter<Photo> {

    Context context;

    public PhotoAdapter(Context context, int layout, ArrayList<Photo> values) {
        super(context, layout, values);
        this.context = context;
    }

    private class ViewContainer {
        ImageView photoImage;
        TextView photoText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewContainer holder = null;
        Photo photo = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.photo, null);
            holder = new ViewContainer();
            holder.photoText = (TextView) convertView.findViewById(R.id.photo_text);
            holder.photoImage = (ImageView) convertView.findViewById(R.id.photo_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewContainer) convertView.getTag();
        }

        holder.photoText.setText(photo.getFileName());
        holder.photoImage.setImageURI(Uri.parse(photo.getLocation()));

        return convertView;
    }
}