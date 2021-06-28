package com.example.androidphotos55;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {
    private ArrayList<Tag> tags; // arraylist of all tags in a photo, in this case exclusively person and location
    private String fileName; // this will just be the file name for this photo, explicit captions not required
    private String location; // the location of the photo (not sure how to implement this on android device)

    public Photo(String fileName, String location){
        this.setFileName(fileName);
        this.setLocation(location);
        this.tags = new ArrayList<Tag>();
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String toString(){
        return getFileName() + "\n" + getLocation();
    }
}
