package com.example.androidphotos55;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class RemovePhoto extends AppCompatActivity {

    private TextView actual_name;

    private int position;

    public static final String POSITION = "position";
    public static final String SELECTED_ALBUM = "selectedAlbum";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actual_name = findViewById(R.id.actual_name2);

        Bundle info = getIntent().getExtras();
        Album givenAlbum = (Album) info.getSerializable(SELECTED_ALBUM);
        position = info.getInt(POSITION);
        ArrayList<Photo> plist = givenAlbum.getPhotos(); // getting photos of said album
        actual_name.setText(plist.get(position).getFileName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void delete(View view) {
        Bundle toSend = new Bundle();
        toSend.putInt(POSITION, position);
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }
}
