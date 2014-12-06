package com.lucass.crawler;

import java.util.Date;

public class Tweet {
    
    private String text;
    private long id;    
    private String created_at;
    
    public Tweet() {
        
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long userId) {
        this.id = userId;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

    
}
