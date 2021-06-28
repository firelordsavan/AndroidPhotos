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

public class RemoveAlbum extends AppCompatActivity {

    private TextView actual_name;
    private TextView actual_num;

    private int position;

    public static final String POSITION = "position";
    public static final String SELECTED_ALBUM = "selectedAlbum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        actual_name = findViewById(R.id.actual_name);
        actual_num = findViewById(R.id.actual_num);

        Bundle info = getIntent().getExtras();
        Album givenAlbum = (Album) info.getSerializable(SELECTED_ALBUM);
        actual_name.setText(givenAlbum.getName());
        actual_num.setText(""+givenAlbum.getNumPhotos());
        position = info.getInt(POSITION);
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

    public void delete(View view){
        Bundle toSend = new Bundle();
        toSend.putInt(POSITION, position);
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }

}