package com.example.androidphotos55;

import android.os.Bundle;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class CreateAlbum extends AppCompatActivity {

    EditText alName; // name of the album we want to create
    Button makeAl; // the button that will create the new album
    String newName;
    ArrayList<Album> albumList;

    public static final String NEW_NAME = "newName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle goods = getIntent().getExtras();
        albumList = (ArrayList<Album>) goods.getSerializable("albums");
        alName = findViewById(R.id.newAlbumName);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void creator(View view) {
        newName = alName.getText().toString(); // will take the text from the input box
        if (newName.length() == 0 || newName == null) {
            Bundle toSend = new Bundle();
            toSend.putString(CreateAlbumFragment.MESSAGE_KEY, "An album name is required");
            DialogFragment newFragment = new CreateAlbumFragment();
            newFragment.setArguments(toSend);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }
        String newAlbumName = newName.toLowerCase().trim(); // New Album Name
        for(int i = 0; i < albumList.size(); i++) {
            if(newAlbumName.equals(albumList.get(i).getName().toLowerCase())) {
                Bundle toSend = new Bundle();
                toSend.putString(CreateAlbumFragment.MESSAGE_KEY, "Already have an album of this same name");
                DialogFragment newFrag = new CreateAlbumFragment();
                newFrag.setArguments(toSend);
                newFrag.show(getSupportFragmentManager(), "badfields");
                return;
            }
        }
        Bundle pack = new Bundle();
        pack.putString(NEW_NAME, newName.trim());
        Intent backward = new Intent();
        backward.putExtras(pack);
        setResult(RESULT_OK, backward);
        finish();
    }
}

