package com.example.androidphotos55;

import java.io.Serializable;

public class Tag implements Serializable {
    private String tagType; // type of the tag (must be person or location)
    private String tagValue; // the value of the tag

    public Tag(int mode, String value){
        if(mode == 0){
            this.setTagType("person");
        }
        else{
            this.setTagType("location");
        }
        this.setTagValue(value);
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String toString(){
        return getTagType() + " : " + getTagValue();
    }
}
