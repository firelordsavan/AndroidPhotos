package com.example.androidphotos55;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String name; // the name of the album
    private int numPhotos; // the number of photos in a given album
    private ArrayList<Photo> photos; // the photos in a given album

    public Album(String name) { // constructor to create an Album
        this.name = name;
        this.numPhotos = 0;
        photos = new ArrayList<Photo>();
    }
    public String getName() { // get method to return name of an album
        return name;
    }
    public void setName(String name) { // set the name of an album
        this.name = name;
    }
    public int getNumPhotos() { // get method to find amount of photos in an album
        return numPhotos;
    }
    public void setNumPhotos(int numPhotos) { // sets the number of photos in an album
        this.numPhotos = numPhotos;
    }
    public ArrayList<Photo> getPhotos() { // gets all the photo objects that belong to this album
        return photos;
    }
    public void addPhoto(Photo newPhoto) { // adds photo to album
        photos.add(newPhoto);
        numPhotos += 1;
    }
    public void removePhoto(int index) { // removes a photo from an album
        Photo deletedPhoto = photos.remove(index);
        numPhotos -= 1;
    }
    public void setPhotos(ArrayList<Photo> photos){
        this.photos = photos;
        numPhotos = photos.size();
    }
    public String toString() { // overridden toString method for Album
        String result = name + "\n" + numPhotos + " Photos\n";
        return result;
    }

}







