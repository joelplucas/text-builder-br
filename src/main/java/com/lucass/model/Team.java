package com.lucass.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import java.util.List;
import org.bson.types.ObjectId;

@Entity(noClassnameStored = true)
public class Team {
    
    @Id
    private ObjectId _id = new ObjectId();
    
    @Property
    private String name;
    
    @Property
    private long lastId;
    
    @Property
    private List<String> words;

    public Team() {
    
    }

    public ObjectId getId() {
        return _id;
    }
    
    public void setId(ObjectId id) {
        this._id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public long getLastId() {
        return lastId;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    /*public int getNumTweets() {
        return numTweets;
    }

    public void setNumTweets(int numTweets) {
        this.numTweets = numTweets;
    } */
    
}