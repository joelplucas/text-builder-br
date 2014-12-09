package com.lucass.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;
import java.util.List;

@Entity(noClassnameStored = true)
public class Team {
    
    @Id
    private String name;
    
    @Property
    private List<String> words;

    public Team() {
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
    
}