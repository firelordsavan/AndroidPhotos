package com.example.androidphotos55;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private ListView photoListView;

    private View prevSelection;
    private int position = -1;
    private int albumPos;
    private Session session = new Session();
    private ArrayList<Album> albums;

    public static final String PHOTOS = "photos";
    public static final String POSITION = "position";
    public static final String SESSION = "session";

    public static final int DISPLAY_PHOTO = 1;
    public static final int ADD_PHOTO = 2;
    public static final int REMOVE_PHOTO = 3;
    public static final int MOVE_PHOTO = 4;

    public static final int REQUEST_READ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        photoListView = findViewById(R.id.photo_list);

        Bundle info = getIntent().getExtras();
        session = (Session) info.getSerializable(SESSION);
        albums = session.getAlbums();
        albumPos = info.getInt(POSITION);

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
        photoListView.setAdapter(
                new PhotoAdapter(this, R.layout.photo, albums.get(albumPos).getPhotos())
        );
        photoListView.setOnItemClickListener((p, V, pos, id) -> {
            if(prevSelection != null){
                prevSelection.setBackgroundColor(getColor(R.color.white));
            }
            prevSelection = V;
            V.setBackgroundColor(getColor(R.color.purple_200));
            position = pos;
        });
    }

    public void displayPhoto(View view){
        if(position < 0 || position >= albums.get(albumPos).getPhotos().size()){
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putSerializable(DisplayPhoto.POSITION, position);
        toSend.putInt(DisplayPhoto.PHOTO, position);
        toSend.putInt(DisplayPhoto.ALBUM, albumPos);
        toSend.putSerializable(DisplayPhoto.SESSION, session);
        Intent intent = new Intent(this, DisplayPhoto.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, DISPLAY_PHOTO);
    }

    public void addPhoto(View view){
        Intent photoPicker = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, ADD_PHOTO);
    }

    public void removePhoto(View view) {
        if(position < 0 || position >= albums.get(albumPos).getPhotos().size()){
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putInt(RemovePhoto.POSITION, position); // position of the photo
        toSend.putSerializable(RemovePhoto.SELECTED_ALBUM, albums.get(albumPos)); // album in which the photo is in
        Intent intent = new Intent(this, RemovePhoto.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, REMOVE_PHOTO);
    }

    public void movePhoto(View view) {
        if(position < 0 || position >= albums.get(albumPos).getPhotos().size()){
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putInt(MovePhoto.POSITION,position); // position of the photo
        toSend.putSerializable(MovePhoto.SELECTED_ALBUM, albums.get(albumPos)); // album in which the photo is in
        toSend.putSerializable(MovePhoto.ALBUM_LIST, albums); // sending in the list of albums
        Intent intent = new Intent(this, MovePhoto.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, MOVE_PHOTO);
    }

    public void returnAlbum(View view){
        Bundle toSend = new Bundle();
        toSend.putSerializable(SESSION, session);
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == ADD_PHOTO){
            // give persistable permission rights to Intent
            try{
                getContentResolver().takePersistableUriPermission(intent.getData(), intent.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
            }catch(Exception e){}

            Uri selectedPhoto = intent.getData();
            String filePath = selectedPhoto.toString();
            String fileName = "No Filename";

            // Get Filename if available
            try{
                fileName = new File(filePath).getName();
            }catch(Exception e){}

            // Check to see if photo already exists in album
            ArrayList<Photo> existingPhotos = albums.get(albumPos).getPhotos();
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

            Photo newPhoto = null; // Reference to new Photo to be added
            // go through all of User's Album objects and Photo objects to see if Photo already exists
            for(Album album : albums) {
                ArrayList<Photo> albumPhotos = album.getPhotos();
                for(Photo photo : albumPhotos) {
                    if(filePath.equals(photo.getLocation())) {
                        newPhoto = photo;
                        break;
                    }
                }
            }

            if(newPhoto == null) {
                newPhoto = new Photo(fileName, filePath);
            }
            albums.get(albumPos).addPhoto(newPhoto);
        }

        if(requestCode == DISPLAY_PHOTO){
            Bundle returned = intent.getExtras();
            if(returned.getSerializable(PhotoActivity.SESSION) != null){
                session = (Session) returned.get(PhotoActivity.SESSION);
                albums = session.getAlbums();
            }
        }

        if(requestCode == REMOVE_PHOTO) {
            Bundle pack = intent.getExtras();
            int toDelete = pack.getInt(RemoveAlbum.POSITION);
            albums.get(albumPos).getPhotos().remove(toDelete);
            int alCard = albums.get(albumPos).getNumPhotos();
            alCard = alCard - 1;
            albums.get(albumPos).setNumPhotos(alCard);
            position = -1;
            prevSelection = null;
        }

        if(requestCode == MOVE_PHOTO) {
            Bundle returned = intent.getExtras();
            int newAlbumPos = returned.getInt(MovePhoto.ALBUM_POS);
            albums.get(newAlbumPos).addPhoto(albums.get(albumPos).getPhotos().get(position));
            albums.get(albumPos).removePhoto(position);
            position = -1;
            prevSelection = null;
        }

        try{
            Session.writeSession(this, session);
        }catch(Exception e){}

        photoListView.setAdapter(
                new PhotoAdapter(this, R.layout.photo, albums.get(albumPos).getPhotos())
        );
    }
}