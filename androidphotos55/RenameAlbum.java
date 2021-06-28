package com.example.androidphotos55;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class RenameAlbum extends AppCompatActivity {

    private EditText newName;
    private ArrayList<Album> albumList;

    public static final String NAME = "name";
    public static final String ALBUMS = "albums";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newName = findViewById(R.id.rename_album_edit);

        Bundle info = getIntent().getExtras();
        newName.setText(info.getString(NAME));
        albumList = (ArrayList<Album>) info.getSerializable(ALBUMS);
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

    public void rename(View view){
        String name = newName.getText().toString(); // will take the text from the input box
        if (newName.length() == 0 || newName == null) {
            Bundle toSend = new Bundle();
            toSend.putString(CreateAlbumFragment.MESSAGE_KEY, "An album name is required");
            DialogFragment newFragment = new CreateAlbumFragment();
            newFragment.setArguments(toSend);
            newFragment.show(getSupportFragmentManager(), "badfields");
            return;
        }
        String newAlbumName = name.toLowerCase().trim(); // New Album Name
        for(int i = 0; i < albumList.size(); i++) {
            if(newAlbumName.equals(albumList.get(i).getName().toLowerCase())) {
                Bundle wap = new Bundle();
                wap.putString(CreateAlbumFragment.MESSAGE_KEY, "Already have an album of this same name");
                DialogFragment newFrag = new CreateAlbumFragment();
                newFrag.setArguments(wap);
                newFrag.show(getSupportFragmentManager(), "badfields");
                return;
            }
        }
        Bundle toSend = new Bundle();
        toSend.putString(NAME, newName.getText().toString().trim());
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }
}