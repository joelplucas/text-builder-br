package com.lucass.utils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBConnector {
    
    private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DATABASE = "sentimentSoccer";
    private static final String MONGO_PACKAGE = "com.lucass.model";
    private static final String USER = null;
    private static final String PASSWORD = null; 
   
    private MongoDBConnector() {
        
    }
    
    public static Datastore getDatastore() {   
        Morphia morphia = new Morphia();
        morphia.mapPackage(MONGO_PACKAGE);

	Datastore ds = null;
        try {
            Mongo mongo = new MongoClient(new ServerAddress(HOST,PORT));
            if(USER!=null && PASSWORD!=null) {
                ds = morphia.createDatastore(mongo, DATABASE, USER, PASSWORD.toCharArray());
            } else {
                ds = morphia.createDatastore(mongo, DATABASE);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(MongoDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

	return ds;
    }
    
    public static void closeMongoDB(Datastore ds) {
        ds.getMongo().close();
    }
    
}
