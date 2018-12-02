package codedragon.com.sciscoop;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class Query {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = Query.class.getSimpleName();

    private Query() {
    }

    /**
     * Query the Science News data and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

        // Create a URL object
        URL url = createUrl( requestUrl );

        // Perform the HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest( url );
        } catch (IOException e) {
            Log.e( LOG_TAG, "There was a problem making the HTTP request.", e );
        }

        // Extract relevant fields from the JSON response and create a list of {@link News} objects.
        List<News> articles = extractResponseFromJson( jsonResponse );

        // Return the list of {@link News} objects
        return articles;
    }

    /**
     * Returns a new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL( stringUrl );
        } catch (MalformedURLException e) {
            Log.e( LOG_TAG, "There was a problem building the URL.", e );
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, return.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout( 10000 );
            urlConnection.setConnectTimeout( 15000 );
            urlConnection.setRequestMethod( "GET" );
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream( inputStream );
            } else {
                Log.e( LOG_TAG, "Error response code: " + urlConnection.getResponseCode() );
            }

        } catch (IOException e) {
            Log.e( LOG_TAG, "There was a problem retrieving the news JSON results.", e );
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole response.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream,
                    Charset.forName( "UTF-8" ) );

            BufferedReader reader = new BufferedReader( inputStreamReader );
            String line = reader.readLine();

            while (line != null) {
                output.append( line );
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return the list of {@link News} objects that has been built.
     */
    public static List<News> extractResponseFromJson(String newsJSON) {
        // If the JSON string is empty or null, return.
        if (TextUtils.isEmpty( newsJSON )) {
            return null;
        }

        // Create an empty ArrayList to add News object to.
        List<News> articles = new ArrayList<>();


        try {
            // Create a JsonObject from the Json response string
            JSONObject baseJsonResponse = new JSONObject( newsJSON );

            // Extract the JSONObject accociated with the key "response"
            JSONObject responseObject = baseJsonResponse.getJSONObject( "response" );

            JSONArray resultsArray = responseObject.getJSONArray( "results" );

            // For each news article in the resultsArray, create an {@link News} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news object at postion i within the list of news articles
                JSONObject currentArticle = resultsArray.getJSONObject( i );

                // Extract the value for the key called "sectionName"
                String sectionName = currentArticle.getString( "sectionName" );

                // Extract the value for the key called "webTitle"
                String webTitle = currentArticle.getString( "webTitle" );

                // Extract the value for the key called "webPublicationDate"
                long webPublicationDate = currentArticle.optLong( "webPublicationDate" );

                // Extract the value for the key called "webUrl"
                String webUrl = currentArticle.getString( "webUrl" );

                JSONArray tagsArray = currentArticle.getJSONArray( "tags" );


                // For each tag in the tagsArray, create an {@link News} object
                for (int t = 0; t < tagsArray.length(); t++) {

                    // Get a single news object at position t within the list of tags
                    JSONObject currentAuthor = tagsArray.getJSONObject( t );

                    String authorName = currentAuthor.getString( "webTitle" );


                /* Create a new {@link News} object with the sectionName, webTitle, authorName,
                  webPublicationDate, and webUrl from the JSON response. */
                    News article = new News( sectionName, webTitle, authorName,
                            webPublicationDate, webUrl );
                    articles.add( article );
                }
            }
        }
        catch (JSONException e) {
            Log.e( "Query", "There was a problem parsing the news JSON results", e );
        }

        // Return the whole list of News articles
        return articles;
    }
}
