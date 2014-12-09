package com.lucass.crawler;
 
import com.google.code.morphia.Datastore;
import com.google.gson.Gson;
import com.lucass.model.Team;
import com.lucass.model.Tweet;
import com.lucass.model.UserTweets;
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
    private  static String ConsumerSecret = "d07jJ0Wr062cM59DcKQ2uY5M44EhMlOUrWfrUvKrbnD8i1dxQi";
   
    private Gson gson;
    private HttpClient client;
    private OAuthConsumer consumer;
    
    private List<Tweet> tweets;
    
    /**
     * @param args
    */
    public static void main(String[] args) throws Exception {
        String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=joelplucas";
        CrawlerStarter crawler = new CrawlerStarter();
        crawler.crawlTweets(url);
        crawler.printTweetTexts();
    }
    
    public CrawlerStarter() {
        tweets = new ArrayList<Tweet>();       
        gson = new Gson();

        consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        
        client = new DefaultHttpClient();
    }
    
    public void crawlTweets(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, IOException {
        List<Team> teamsToCrawl = getTeamsFromDB();
        
        HttpGet request = new HttpGet(url);
        consumer.sign(request);
        
        HttpResponse response = client.execute(request);       
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode == 200) {
            String reponseText = "{\"tweets\": " + IOUtils.toString(response.getEntity().getContent()) + "}";
            
            UserTweets userTweets = gson.fromJson(reponseText, UserTweets.class);
            List<Tweet> tweetsFromUser = userTweets.getTweets();
            tweets.addAll(tweetsFromUser);
        }
    }
    
    public void printTweetTexts() {
        for(Tweet tweet : tweets) {
            System.out.println(tweet.getText());
        }
    }
    
    private List<Team> getTeamsFromDB() {
        Datastore ds = MongoDBConnector.getDatastore();
        List<Team> teams = ds.createQuery(Team.class).asList();
        
        MongoDBConnector.closeMongoDB(ds);
        return teams;
    }
}
