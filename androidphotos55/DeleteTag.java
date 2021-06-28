package com.example.androidphotos55;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class DeleteTag extends AppCompatActivity {
    private TextView tag_name;

    private int position; // position of tag in tag list

    public static final String POSITION = "position"; // key for position
    public static final String SELECTED_PHOTO = "selectedPhoto"; // key for selected photo


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_tag); // the activity in question
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // activates the up arrow
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tag_name = findViewById(R.id.dtag_name2); // setting the textview to be tag_name by id

        Bundle info = getIntent().getExtras(); // getting bundle from previous activity
        Photo givenPhoto = (Photo) info.getSerializable(SELECTED_PHOTO); // getting the photo in question from the previous activity
        position = info.getInt(POSITION); // getting the position of the tag in question that we want to remove
        ArrayList<Tag> tList = givenPhoto.getTags(); // getting tags of said photo
        tag_name.setText(tList.get(position).toString()); //setting the tag_name textview to be tag_name
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
