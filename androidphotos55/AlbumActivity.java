package com.example.androidphotos55;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    private ListView albumListView;
    private Button createAl; // button used to create an album
    private Button deleteAlbum; // button used to delete selected album
    private View prevSelection;
    private ArrayList<Album> albums = new ArrayList<Album>();
    private int position = -1;

    public Session session;

    public static final int PHOTO_ACTIVITY = 1;
    public static final int CREATE_NEW_ALBUM = 2;
    public static final int REMOVE_ALBUM = 3;
    public static final int RENAME_ALBUM = 4;
    public static final int SEARCH_PHOTO = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity);

        albumListView = findViewById(R.id.album_list); // refer to ListView that displays albums

        try {
            // Read previously saved Admin or create new one
            File file = new File(this.getFilesDir(),"Session.dat");
            if(!file.exists()) {
                file.createNewFile();
                session = new Session();
            }
            else {
                session = Session.readSession(this);
            }

        }
        catch(Exception e){
            System.out.println(e);
            session = new Session();
        }

        albums = session.getAlbums();
        albumListView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums)
        );
        albumListView.setOnItemClickListener((p, V, pos, id) -> {
            if(prevSelection != null){
                prevSelection.setBackgroundColor(getColor(R.color.white));
            }
            prevSelection = V;
            V.setBackgroundColor(getColor(R.color.purple_200));
            position = pos;
        });

    }

    public void openAlbum(View view){
        if(position < 0 || position >= albums.size()){
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putSerializable(PhotoActivity.POSITION, position);
        toSend.putSerializable(PhotoActivity.SESSION, session);
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, PHOTO_ACTIVITY);
    }

    public void createAlbum(View view) { // method to be launched when clicking create album
        Bundle newAl = new Bundle(); // bundle to store arraylist to send into
        newAl.putSerializable("albums", albums);
        Intent createNewAl = new Intent(this, CreateAlbum.class);
        createNewAl.putExtras(newAl);
        startActivityForResult(createNewAl,CREATE_NEW_ALBUM);
    }

    // launches DeleteAlbum Activity
    public void deleteAlbum(View view){
        if(position < 0 || position >= albums.size()){
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putInt(RemoveAlbum.POSITION, position);
        toSend.putSerializable(RemoveAlbum.SELECTED_ALBUM, albums.get(position));
        Intent intent = new Intent(this, RemoveAlbum.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, REMOVE_ALBUM);
    }

    // launches RenameAlbum Activity
    public void renameAlbum(View view){
        if(position < 0 || position >= albums.size()){
            return;
        }
        Bundle toSend = new Bundle();
        toSend.putString(RenameAlbum.NAME, albums.get(position).getName());
        toSend.putSerializable(RenameAlbum.ALBUMS, albums);
        Intent intent = new Intent(this, RenameAlbum.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, RENAME_ALBUM);
    }

    public void searchPhotos(View view){
        Bundle toSend = new Bundle();
        toSend.putSerializable(SearchPhoto.SESSION, session);
        Intent intent = new Intent(this, SearchPhoto.class);
        intent.putExtras(toSend);
        startActivityForResult(intent, SEARCH_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == CREATE_NEW_ALBUM){
            Bundle returned = intent.getExtras();
            albums.add(new Album(returned.getString(CreateAlbum.NEW_NAME)));
        }

        if(requestCode == REMOVE_ALBUM){
            Bundle returned = intent.getExtras();
            int toDelete = returned.getInt(RemoveAlbum.POSITION);
            albums.remove(toDelete);
            position = -1;
            prevSelection = null;
        }

        if(requestCode == RENAME_ALBUM){
            Bundle returned = intent.getExtras();
            String newName = returned.getString(RenameAlbum.NAME);
            albums.get(position).setName(newName);
        }

        if(requestCode == PHOTO_ACTIVITY){
            Bundle returned = intent.getExtras();
            if(returned.getSerializable(PhotoActivity.SESSION) != null){
                session = (Session) returned.get(PhotoActivity.SESSION);
                albums = session.getAlbums();
            }
        }

        if(requestCode == SEARCH_PHOTO){

        }

        // save changes
        try {
            Session.writeSession(this, session);
        }
        catch(Exception e) {
            System.out.println(e);
        }

        // redo the adapter to reflect changes
        albumListView.setAdapter(
                new ArrayAdapter<Album>(this, R.layout.album, albums)
        );

    }

}