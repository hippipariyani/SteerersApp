package com.heisen_berg.steerersapp.model;

import java.util.ArrayList;

/**
 * Created by heisen-berg on 24/02/18.
 */

public class Post {
    public String id, description;
    public ArrayList<String> photos=new ArrayList<String>();
    public String authorName;
    public String authorID;
    public long timestamp;

    public Post() {
    }

    public Post(String id, String description, ArrayList<String> photos, String authorName, String authorID, long timestamp) {
        this.id = id;
        this.description = description;
        this.photos = photos;
        this.authorName = authorName;
        this.authorID = authorID;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
