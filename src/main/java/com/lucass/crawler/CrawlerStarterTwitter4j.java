package com.lucass.crawler;

import com.google.code.morphia.Datastore;
import com.google.gson.Gson;
import com.lucass.model.Team;
import com.lucass.model.TeamTweets;
import com.lucass.model.Tweet;
import com.lucass.utils.MongoDBConnector;
import java.util.ArrayList;
import java.util.List;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class CrawlerStarterTwitter4j {
    private final String CONSUMER_KEY = "AeidfIbX8t5taGVVQ1FCJL89p";
    private final String CONSUMER_KEY_SECRET = "d07jJ0Wr062cM59DcKQ2uY5M44EhMlOUrWfrUvKrbnD8i1dxQi";
    private final String ACCESS_TOKEN = "2891460417-1WMZPDxcu7oQigR4MkUo75y3BZqcRjORRgY5XFU";
    private final String ACCESS_TOKEN_SECRET = "jl5mr736jo50zZZfGqu2m67uo3v5wOZR7BjWr6EPz0Zmp";
    
    private Gson gson;
    private Datastore ds;
    private Twitter twitter;
    private List<Tweet> tweets;
    
    public CrawlerStarterTwitter4j() {
        twitter = new TwitterFactory().getInstance();
        ds = MongoDBConnector.getDatastore();
        gson = new Gson();
    }
    
    public static void main(String[] args) throws Exception {    
        CrawlerStarterTwitter4j crawler = new CrawlerStarterTwitter4j();
        crawler.run();
    }

    public void run() {
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
        AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        twitter.setOAuthAccessToken(accessToken);
        
        List<Team> teams = getTeams(ds);
        tweets = getTeamsTweets(teams);
        
        System.out.println(tweets.size());        
    }
    
    private List<Tweet> getTeamsTweets(List<Team> teams) {
        List<Tweet> tweets = new ArrayList<Tweet>();
        for(Team team : teams) {
            
            long minimumId = 581040785199665200L;
            
            List<Status> tweetsStatus = new ArrayList<Status>();
            
            for(String tag : team.getWords()) {  
                System.out.println(tag);
                getTweetsByTag(tweetsStatus, tag, minimumId, -1);
            }
            System.out.println(tweetsStatus.size());
        }
        return tweets;
    }
    
    private void getTweetsByTag(List<Status> tweets, String tag, long minimumId, long maximumId) {
        try {
            Query query = new Query(tag);
            query.setCount(100);
            query.setLang("pt");
            query.setResultType(Query.ResultType.recent);
            query.setSinceId(minimumId);
            if(maximumId > 0) query.setMaxId(maximumId);

            QueryResult result = twitter.search(query);
            tweets.addAll(result.getTweets());
            if(result.getTweets().size() == 100) {
                long lastId = result.getTweets().get(99).getId();
                getTweetsByTag(tweets, tag, minimumId, lastId);
            }
            //for(Status status : result.getTweets()) {
                //String a = status.getText();
                //long id = status.getId();
                //System.out.println(query);
                //System.out.println(a);
                //System.out.println(id);
                /*TeamTweets userTweets = gson.fromJson(status.getText(), TeamTweets.class);
                List<Tweet> tweetsFromUser = userTweets.getTweets();
                tweets.addAll(tweetsFromUser);*/
            //}
        } catch (TwitterException ex) {
            System.out.println("Twitter error for " + tag);
            ex.printStackTrace();                    
        }
    }

    
    private List<Team> getTeams(Datastore ds) {
        List<Team> teams = ds.createQuery(Team.class).asList();       
        return teams;
    }

}
