package com.lucass.crawler;

import com.google.code.morphia.Datastore;
import com.google.gson.Gson;
import com.lucass.model.Team;
import com.lucass.model.Tweet;
import com.lucass.utils.MongoDBConnector;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class CrawlerStarter {
    private final String CONSUMER_KEY = "AeidfIbX8t5taGVVQ1FCJL89p";
    private final String CONSUMER_KEY_SECRET = "d07jJ0Wr062cM59DcKQ2uY5M44EhMlOUrWfrUvKrbnD8i1dxQi";
    private final String ACCESS_TOKEN = "2891460417-1WMZPDxcu7oQigR4MkUo75y3BZqcRjORRgY5XFU";
    private final String ACCESS_TOKEN_SECRET = "jl5mr736jo50zZZfGqu2m67uo3v5wOZR7BjWr6EPz0Zmp";
    
    private Gson gson;
    private Datastore ds;
    private Twitter twitter;
    private List<Tweet> tweets;
    
    public CrawlerStarter() {
        twitter = new TwitterFactory().getInstance();
        ds = MongoDBConnector.getDatastore();
        gson = new Gson();
    }
    
    public static void main(String[] args) throws Exception {    
        CrawlerStarter crawler = new CrawlerStarter();
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
            ObjectId teamId = team.getId();      
            long minimumId = team.getLastId() + 1;
            List<Status> tweetsStatus = new ArrayList<Status>();
            
            System.out.println(team.getName());
            
            int newTweetsCount = 0;
            
            for(String tag : team.getWords()) {  
                System.out.printf(" - %s", tag);
                tweetsStatus = getTweetsByTag(tweetsStatus, tag, minimumId, -1);
                System.out.printf(" (%d)\n", (tweetsStatus.size() - newTweetsCount));
                newTweetsCount = tweetsStatus.size();
            }
            
            System.out.printf("Total de Novos Tweets: %d\n\n", newTweetsCount);
            
            if(newTweetsCount > 0) {
                saveTweets(tweetsStatus, teamId);
                long lastId = 0;
                for(Status status : tweetsStatus) {
                    if(status.getId() > lastId) {
                        lastId = status.getId();
                    }
                }
                team.setLastId(lastId);
                ds.save(team);
            }
        }
        return tweets;
    }
    
    private List<Status> getTweetsByTag(List<Status> tweetsStatus, String tag, long minimumId, long maximumId) {
        try {
            System.out.printf(".");
            Query query = new Query(tag);
            query.setLang("pt");
            query.setResultType(Query.ResultType.recent);
            if(minimumId > 1) {
                query.setSinceId(minimumId);
                query.setCount(100);
            }
            if(maximumId > 0) query.setMaxId(maximumId);
            
            QueryResult result = twitter.search(query);
            tweetsStatus.addAll(result.getTweets());
            if(result.getTweets().size() == 100) {
                long lastId = result.getTweets().get(99).getId();
                tweetsStatus = getTweetsByTag(tweetsStatus, tag, minimumId, lastId);
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
        
        return tweetsStatus;
    }
    
    private void saveTweets(List<Status> tweetsStatus, ObjectId teamId) {
        
        for(Status status : tweetsStatus) {
            Tweet newTweet = new Tweet(status.getId(), status.getText(), teamId, status.getCreatedAt());
            ds.save(newTweet);
        }
    }
    
    private List<Team> getTeams(Datastore ds) {
        List<Team> teams = ds.createQuery(Team.class).asList();       
        return teams;
    }

}
