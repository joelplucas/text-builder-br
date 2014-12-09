package com.lucass.databaseReset;

import com.google.code.morphia.Datastore;
import com.google.gson.Gson;
import com.lucass.model.Team;
import com.lucass.model.Teams;
import com.lucass.utils.MongoDBConnector;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.commons.io.IOUtils;

public class PopulateTeams {
    
    private Datastore ds;
    private Gson gson;
    
    private List<Team> teams;
    
    public static void main(String[] args) throws Exception {
        
        PopulateTeams popTeams = new PopulateTeams();
        popTeams.getTeamsFromJSON();
        popTeams.saveTeamsOnDB();
        popTeams.closeConection();
        
    }
    
    public PopulateTeams() {
        
        ds = MongoDBConnector.getDatastore();
        gson = new Gson();
        
    }
    
    private void getTeamsFromJSON() throws FileNotFoundException, IOException {

        String file = "/teams.json";
        BufferedReader teamFile = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)));
        
        Teams teamsList = gson.fromJson(IOUtils.toString(teamFile), Teams.class);
        teams.addAll(teamsList.getTeams());             

    }    

    private void saveTeamsOnDB() {
        for(Team team : teams) {
            ds.save(team);
        }                
    }

    private void closeConection() {
        MongoDBConnector.closeMongoDB(ds);
    }
    
}
