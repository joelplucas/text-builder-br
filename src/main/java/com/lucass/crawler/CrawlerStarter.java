package com.lucass.crawler;
 
import com.google.code.morphia.Datastore;
import com.google.gson.Gson;
import com.lucass.model.Team;
import com.lucass.model.Tweet;
import com.lucass.model.TeamTweets;
import com.lucass.utils.MongoDBConnector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class CrawlerStarter {
   
    private static String AccessToken = "2891460417-1WMZPDxcu7oQigR4MkUo75y3BZqcRjORRgY5XFU";
    private static String AccessSecret = "jl5mr736jo50zZZfGqu2m67uo3v5wOZR7BjWr6EPz0Zmp";
    private static String ConsumerKey = "AeidfIbX8t5taGVVQ1FCJL89p";
    private static String ConsumerSecret = "d07jJ0Wr062cM59DcKQ2uY5M44EhMlOUrWfrUvKrbnD8i1dxQi";
    
    private static String TwitterBaseUrl = "https://api.twitter.com/1.1/";
   
    private Gson gson;
    private HttpClient client;
    private OAuthConsumer consumer;
    private Datastore ds;
    
    private List<Tweet> tweets;
    private List<Team> teams;
    
    /**
     * @param args
    */
    public static void main(String[] args) throws Exception {
        
        CrawlerStarter crawler = new CrawlerStarter();
        crawler.getTeamsFromDB();
        crawler.getTeamsTweets();
        crawler.closeConection();
   
    }
    
    public CrawlerStarter() {
        ds = MongoDBConnector.getDatastore();
        
        gson = new Gson();
        tweets = new ArrayList<Tweet>();  
        teams = new ArrayList<Team>();  

        consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        
        client = new DefaultHttpClient();
    }

    
    private List<Team> getTeamsFromDB() {
        teams = ds.createQuery(Team.class).asList();       
        return teams;
    }

    private void getTeamsTweets() throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {

        for(Team team : teams) {
            
            for(String word : team.getWords()) {                
                String url = TwitterBaseUrl + "search/tweets.json?q=" + word;
                System.out.println(url);
                
                HttpGet request = new HttpGet(url);
                consumer.sign(request);
                
                HttpResponse response = client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                if(statusCode == 200) {
                    String reponseText = IOUtils.toString(response.getEntity().getContent());
                    TeamTweets userTweets = gson.fromJson(reponseText, TeamTweets.class);
                    List<Tweet> tweetsFromUser = userTweets.getTweets();
                    tweets.addAll(tweetsFromUser);
                }
                if(response.getEntity() != null) {
                    response.getEntity().consumeContent();
                }

            }  
            
        }        

    }
    
    private void closeConection() {
        MongoDBConnector.closeMongoDB(ds);
    }

  
}
