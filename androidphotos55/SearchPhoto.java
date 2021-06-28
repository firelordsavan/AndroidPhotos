package com.example.androidphotos55;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchPhoto extends AppCompatActivity {

    private ListView searchResult;
    private EditText peopleInput;
    private EditText locationInput;
    private Spinner searchChoice;
    private Spinner tag1;
    private Spinner tag2;

    private View prevSelection;
    private int position = -1;
    private Session session = new Session();
    private ArrayList<Album> albums;
    private ArrayList<Photo> photos;

    public static final String SESSION = "session";

    public static final int REQUEST_READ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchResult = findViewById(R.id.search_results);
        peopleInput = findViewById(R.id.people_input);
        locationInput = findViewById(R.id.location_input);
        searchChoice = findViewById(R.id.search_choice);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);

        Bundle info = getIntent().getExtras();
        session = (Session) info.getSerializable(SESSION);
        albums = session.getAlbums();
        photos = new ArrayList<Photo>();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchChoice.setAdapter(adapter);
        searchChoice.setSelection(0);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.search, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tag1.setAdapter(adapter2);
        tag1.setSelection(0);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.search, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tag2.setAdapter(adapter3);
        tag2.setSelection(0);

        // Check for permissions or request for them
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showList();
        }
        else{
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults){
        if(requestCode == REQUEST_READ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showList();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permission, grantResults);
        }
    }

    public void showList(){
        searchResult.setAdapter(
                new PhotoAdapter(this, R.layout.photo, photos)
        );
        searchResult.setOnItemClickListener((p, V, pos, id) -> {
            if(prevSelection != null){
                prevSelection.setBackgroundColor(getColor(R.color.white));
            }
            prevSelection = V;
            V.setBackgroundColor(getColor(R.color.purple_200));
            position = pos;
        });
    }

    public void searchPeople(View view){
        String people = peopleInput.getText().toString().toLowerCase().trim();
        ArrayList<Photo> results = new ArrayList<Photo>();
        // hashset to keep track of already added photos
        Set<Photo> addedPhotos = new HashSet<Photo>();
        for(Album album : albums) {
            ArrayList<Photo> albumPhotos = album.getPhotos();
            for(Photo photo : albumPhotos) {
                if(!addedPhotos.contains(photo)) {
                    ArrayList<Tag> photoTags = photo.getTags();
                    for(Tag tag : photoTags) {
                        if(tag.getTagType().equals("person") && tag.getTagValue().toLowerCase().indexOf(people) != -1){
                            addedPhotos.add(photo);
                            results.add(photo);
                            break;
                        }
                    }
                }
            }
        }
        photos = results;
        searchResult.setAdapter(
                new PhotoAdapter(this, R.layout.photo, photos)
        );
    }

    public void searchLocation(View view){
        String location = peopleInput.getText().toString().toLowerCase().trim();
        ArrayList<Photo> results = new ArrayList<Photo>();
        // hashset to keep track of already added photos
        Set<Photo> addedPhotos = new HashSet<Photo>();
        for(Album album : albums) {
            ArrayList<Photo> albumPhotos = album.getPhotos();
            for(Photo photo : albumPhotos) {
                if(!addedPhotos.contains(photo)) {
                    ArrayList<Tag> photoTags = photo.getTags();
                    for(Tag tag : photoTags) {
                        if(tag.getTagType().equals("location") && tag.getTagValue().toLowerCase().indexOf(location) != -1){
                            addedPhotos.add(photo);
                            results.add(photo);
                            break;
                        }
                    }
                }
            }
        }
        photos = results;
        searchResult.setAdapter(
                new PhotoAdapter(this, R.layout.photo, photos)
        );
    }

    public void searchNone(){
        String location = peopleInput.getText().toString().toLowerCase().trim();
        String type = tag1.getSelectedItem().toString();
        ArrayList<Photo> results = new ArrayList<Photo>();
        // hashset to keep track of already added photos
        Set<Photo> addedPhotos = new HashSet<Photo>();
        for(Album album : albums) {
            ArrayList<Photo> albumPhotos = album.getPhotos();
            for(Photo photo : albumPhotos) {
                if(!addedPhotos.contains(photo)) {
                    ArrayList<Tag> photoTags = photo.getTags();
                    for(Tag tag : photoTags) {
                        if(tag.getTagType().equals(type) && tag.getTagValue().toLowerCase().indexOf(location) != -1){
                            addedPhotos.add(photo);
                            results.add(photo);
                            break;
                        }
                    }
                }
            }
        }
        photos = results;
        searchResult.setAdapter(
                new PhotoAdapter(this, R.layout.photo, photos)
        );
    }

    public void searchJoint(View view){
        if(searchChoice.getSelectedItemPosition() == 0){
            searchNone();
        }
        else if(searchChoice.getSelectedItemPosition() == 1){
            searchJointOR();
        }
        else{
            searchJointAND();
        }
    }

    public void searchJointOR(){
        String people = peopleInput.getText().toString().toLowerCase().trim();
        String location = locationInput.getText().toString().toLowerCase().trim();
        String type1 = tag1.getSelectedItem().toString();
        String type2 = tag2.getSelectedItem().toString();
        ArrayList<Photo> results = new ArrayList<Photo>();
        // hashset to keep track of already added photos
        Set<Photo> addedPhotos = new HashSet<Photo>();
        for(Album album : albums) {
            ArrayList<Photo> albumPhotos = album.getPhotos();
            for(Photo photo : albumPhotos) {
                if(!addedPhotos.contains(photo)) {
                    ArrayList<Tag> photoTags = photo.getTags();
                    for(Tag tag : photoTags) {
                        if((tag.getTagType().equals(type1) && tag.getTagValue().toLowerCase().indexOf(people) != -1) ||
                                (tag.getTagType().equals(type2) && tag.getTagValue().toLowerCase().indexOf(location) != -1)) {
                            results.add(photo);
                            addedPhotos.add(photo);
                            break;
                        }
                    }
                }
            }
        }
        photos = results;
        searchResult.setAdapter(
                new PhotoAdapter(this, R.layout.photo, photos)
        );
    }

    public void searchJointAND(){
        String people = peopleInput.getText().toString().toLowerCase().trim();
        String location = locationInput.getText().toString().toLowerCase().trim();
        String type1 = tag1.getSelectedItem().toString();
        String type2 = tag2.getSelectedItem().toString();
        ArrayList<Photo> results = new ArrayList<Photo>();
        // hashset to keep track of already added photos
        Set<Photo> addedPhotos = new HashSet<Photo>();
        for(Album album : albums) {
            ArrayList<Photo> albumPhotos = album.getPhotos();
            for(Photo photo : albumPhotos) {
                if(!addedPhotos.contains(photo)) {
                    ArrayList<Tag> photoTags = photo.getTags();
                    boolean checkTag1 = false;
                    boolean checkTag2 = false;

                    for(Tag tag : photoTags) {
                        if(tag.getTagType().equals(type1) && tag.getTagValue().toLowerCase().indexOf(people) != -1) {
                            checkTag1 = true;
                        }
                        if(tag.getTagType().equals(type2) && tag.getTagValue().toLowerCase().indexOf(location) != -1) {
                            checkTag2 = true;
                        }
                        if(checkTag1 && checkTag2) {
                            results.add(photo);
                            addedPhotos.add(photo);
                            break;
                        }
                    }
                }
            }
        }
        photos = results;
        searchResult.setAdapter(
                new PhotoAdapter(this, R.layout.photo, photos)
        );
    }

    public void returnAlbum(View view){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}