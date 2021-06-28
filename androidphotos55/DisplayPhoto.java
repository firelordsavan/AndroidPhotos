package com.example.androidphotos55;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    private TextView filename;
    private ImageView photoDisplay;
    private ListView tagListView;

    //private Photo selectedPhoto;
    private View prevSelection;
    private int position = -1;
    private int photoPos;
    private int albumPos;
    private Session session = new Session();
    private ArrayList<Photo> photos;

    public static final String POSITION = "position";
    public static final String PHOTO = "photo";
    public static final String ALBUM = "album";
    public static final String TAGS = "tags";
    public static final String SESSION = "session";

    public static final int ADD_TAG = 1;
    public static final int DELETE_TAG = 2; // request code for delete tag

    public static final int REQUEST_READ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filename = findViewById(R.id.filename);
        photoDisplay = findViewById(R.id.display_photo_view);
        tagListView = findViewById(R.id.tag_list);

        Bundle info = getIntent().getExtras();
        session = (Session) info.getSerializable(SESSION);
        photoPos = info.getInt(PHOTO);
        albumPos = info.getInt(ALBUM);
        photos = session.getAlbums().get(albumPos).getPhotos();

        filename.setText(photos.get(photoPos).getFileName());
        photoDisplay.setImageURI(Uri.parse(photos.get(photoPos).getLocation()));

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
        tagListView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.album, photos.get(photoPos).getTags())
        );
        tagListView.setOnItemClickListener((p, V, pos, id) -> {
            if(prevSelection != null){
                prevSelection.setBackgroundColor(getColor(R.color.white));
            }
            prevSelection = V;
            V.setBackgroundColor(getColor(R.color.purple_200));
            position = pos;
        });
    }

    public void backPhoto(View view){
        if(photoPos-1 < 0 || photoPos-1 >= photos.size()){
            return;
        }
        photoPos -= 1;
        filename.setText(photos.get(photoPos).getFileName());
        photoDisplay.setImageURI(Uri.parse(photos.get(photoPos).getLocation()));
        tagListView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.album, photos.get(photoPos).getTags())
        );
    }

    public void nextPhoto(View view){
        if(photoPos+1 < 0 || photoPos+1 >= photos.size()){
            return;
        }
        photoPos += 1;
        filename.setText(photos.get(photoPos).getFileName());
        photoDisplay.setImageURI(Uri.parse(photos.get(photoPos).getLocation()));
        tagListView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.album, photos.get(photoPos).getTags())
        );
    }

    public void addTag(View view){
        Bundle toSend = new Bundle();
        toSend.putSerializable(AddTag.PHOTO, photos.get(photoPos));
        Intent intent = new Intent(this, AddTag.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, ADD_TAG);
    }

    public void deleteTag(View view) {
        if(position < 0 || position >= photos.get(photoPos).getTags().size()){ // just checking to see if bounds are proper
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putInt(DeleteTag.POSITION, position); // position of the tag
        toSend.putSerializable(DeleteTag.SELECTED_PHOTO, photos.get(photoPos)); // Photo in which the tag is in
        Intent intent = new Intent(this, DeleteTag.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, DELETE_TAG);
    }

    public void returnPhoto(View view){
        Bundle toSend = new Bundle();
        toSend.putSerializable(TAGS, photos.get(photoPos).getTags());
        toSend.putSerializable(SESSION, session);
        Intent intent = new Intent();
        intent.putExtras(toSend);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == ADD_TAG){
            Bundle returned = intent.getExtras();
            photos.get(photoPos).getTags().add(new Tag(returned.getInt(AddTag.NEW_TYPE), returned.getString(AddTag.NEW_VALUE)));
        }

        if(requestCode == DELETE_TAG) {
            Bundle pack = intent.getExtras(); // the bundle we are getting in return from the activity
            int toDelete = pack.getInt(DeleteTag.POSITION); // the position of the tag we are deleting
            photos.get(photoPos).getTags().remove(toDelete); // deleting the tag of the photo
            position = -1;
            prevSelection = null;
        }

        try{
            Session.writeSession(this, session);
        }catch(Exception e){}

        tagListView.setAdapter(
                new ArrayAdapter<Tag>(this, R.layout.album, photos.get(photoPos).getTags())
        );
    }
}