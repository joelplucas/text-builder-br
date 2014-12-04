package com.lucass;
 
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;


public class App {
   
    private static String AccessToken = "2891460417-1WMZPDxcu7oQigR4MkUo75y3BZqcRjORRgY5XFU";
    private static String AccessSecret = "jl5mr736jo50zZZfGqu2m67uo3v5wOZR7BjWr6EPz0Zmp";
    private static String ConsumerKey = "AeidfIbX8t5taGVVQ1FCJL89p";
    private  static String ConsumerSecret = "d07jJ0Wr062cM59DcKQ2uY5M44EhMlOUrWfrUvKrbnD8i1dxQi";
   
    /**
     * @param args
    */
    public static void main(String[] args) throws Exception {
    
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey, ConsumerSecret);
 
        consumer.setTokenWithSecret(AccessToken, AccessSecret);
        HttpGet request = new HttpGet("http://api.twitter.com/1.1/followers/ids.json?cursor=-1&screen_name=josdirksen");
        consumer.sign(request);
 
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(request);
 
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());
        System.out.println(IOUtils.toString(response.getEntity().getContent()));
    }
}
