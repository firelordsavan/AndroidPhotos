package com.example.androidphotos55;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Session implements Serializable {
    public ArrayList<Album> albums;
    public static final String storeDir = "files";
    public static final String storeFile = "Session.dat";

    public Session(){
        albums = new ArrayList<Album>();
    }

    public ArrayList<Album> getAlbums(){
        return albums;
    }
    public void setAlbums(ArrayList<Album> albums) {this.albums = albums;}

    public static void writeSession(Context context, Session session) throws IOException {
        FileOutputStream fos = context.openFileOutput(storeFile, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(session);
        os.close();
        fos.close();
    }

    public static Session readSession(Context context) throws IOException, ClassNotFoundException{
        FileInputStream fis = context.openFileInput(storeFile);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Session session = (Session)ois.readObject();
        ois.close();
        fis.close();

        return session;
    }
}
