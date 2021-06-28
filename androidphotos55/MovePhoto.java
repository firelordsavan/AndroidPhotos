package com.example.androidphotos55;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class MovePhoto extends AppCompatActivity {

    private Spinner albumChoices;
    private int selectedPhoto;
    private Album selectedAlbum;
    private ArrayList<Album> albums;

    public static final String POSITION = "position"; // position of photo
    public static final String SELECTED_ALBUM = "selectedAlbum";
    public static final String ALBUM_LIST = "albumlist";
    public static final String ALBUM_POS = "albumPos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle info = getIntent().getExtras();
        selectedPhoto = info.getInt(POSITION);
        selectedAlbum = (Album) info.getSerializable(SELECTED_ALBUM);
        albums = (ArrayList<Album>) info.getSerializable(ALBUM_LIST);

        albumChoices = findViewById(R.id.album_choices);
        if(albums.size() != 0) {
            ArrayAdapter<Album> adapter = new ArrayAdapter<Album>(this, android.R.layout.simple_spinner_item, albums);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            albumChoices.setAdapter(adapter);
            albumChoices.setSelection(0);
        }
    }

    public void cancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void movePhoto(View view){
        String filePath = selectedAlbum.getPhotos().get(selectedPhoto).getLocation();
        // Check to see if photo already exists in album
        ArrayList<Photo> existingPhotos = albums.get(albumChoices.getSelectedItemPosition()).getPhotos();
        // check if photo already exists
        for(int i = 0; i < existingPhotos.size(); i++) {
            if(filePath.equals(existingPhotos.get(i).getLocation())) {
                Bundle toSend = new Bundle();
                toSend.putString(CreateAlbumFragment.MESSAGE_KEY, "Photo already Exists in Album");
                DialogFragment newFragment = new CreateAlbumFragment();
                newFragment.setArguments(toSend);
                newFragment.show(getSupportFragmentManager(), "badfields");
                return;
            }
        }

        Bundle toSend = new Bundle();
        toSend.putInt(ALBUM_POS, albumChoices.getSelectedItemPosition());
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }
}