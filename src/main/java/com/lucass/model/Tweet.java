package com.lucass.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import java.util.Date;
import org.bson.types.ObjectId;

@Entity(noClassnameStored = true)
public class Tweet {
    
    @Id
    private long _id;
    @Property
    private String text;
    @Property    
    private ObjectId team;   
    @Property    
    private Date createdAt;    
    
    
    public Tweet() {   
    }
    
    public Tweet(long id, String text, ObjectId team, Date createdAt) {
        this._id = id;
        this.text = text;
        this.team = team;
        this.createdAt = createdAt;
    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ObjectId getTeam() {
        return team;
    }

    public void setTeam(ObjectId team) {
        this.team = team;
    }  

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }   
}
