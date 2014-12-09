package com.lucass.model;

import com.lucass.model.Tweet;
import java.io.Serializable;
import java.util.List;

public class Teams {
    private List<Team> teams;

    public Teams() {
        
    }
    
    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    } 
}
